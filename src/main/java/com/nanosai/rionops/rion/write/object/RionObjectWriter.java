package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An RionObjectWriter instance can write an object (instance) of some class to ION data ("ionize" the object in other words).
 * An RionObjectWriter is targeted at a single Java class. To serialize objects of multiple classes, create one RionObjectWriter
 * per class.
 *
 */
public class RionObjectWriter<T> {

    protected Class   typeClass = null;
    protected IRionFieldWriter[] fieldWriters = null;

    protected CyclicObjectGraphWriteState writeState = new CyclicObjectGraphWriteState();


    public RionObjectWriter(IRionFieldWriter[] fieldWriters){
        this.fieldWriters = fieldWriters;
    }

    /**
     * Creates an RionObjectWriter targeted at the class passed as parameter to this constructor.
     *
     * @param typeClass The class this RionObjectWriter should be able to write instances of (to ION).
     */

    public RionObjectWriter(Class typeClass) {
        this(typeClass, RionObjectWriterConfiguratorNopImpl.DEFAULT_INSTANCE);
    }


    /**
     * Creates an RionObjectWriter targeted at the class passed as parameter to this constructor.
     * The IRionObjectWriterConfigurator can configure (modify) this RionObjectWriter instance. For instance,
     * it can signal that some fields should not be included when writing the object, or modify what field
     * fieldName is to be used when writing the object.
     *
     * @param typeClass    The class this RionObjectWriter should be able to write instances of (to ION).
     * @param configurator The configurator that can configure each field writer (one per field of the target class) in this RionObjectWriter - even exclude them.
     */
    public RionObjectWriter(Class typeClass, IRionObjectWriterConfigurator configurator){
        this.typeClass = typeClass;

        this.fieldWriters = RionFieldWriterUtil.createFieldWriters(this.typeClass.getDeclaredFields(), configurator, new HashMap<>());
    }

    /**
     *
     * @param src The source object to write
     * @param maxLengthLength The maximum length-length (no of length bytes) the object will need when serialized to RION.
     * @param destination The byte array into which the serialized RION will be written.
     * @param destinationOffset The offset into the byte array to start writing the RION data.
     * @deprecated Use writeAcyclic instead - it has the same behaviour as writeObject() has.
     * @return The number of bytes written to the destination byte array.
     */


    public int writeObject(Object src, int maxLengthLength, byte[] destination, int destinationOffset){
        return writeAcyclic(src, maxLengthLength, destination, destinationOffset);
    }

    public int writeAcyclic(Object src, int maxLengthLength, byte[] destination, int destinationOffset){
        destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.OBJECT << 4) | maxLengthLength));

        int lengthOffset   = destinationOffset; //store length start offset for later use
        destinationOffset += maxLengthLength;


        for(int i=0; i<fieldWriters.length; i++){
            if(fieldWriters[i] != null){
                destinationOffset += fieldWriters[i].writeKeyField(destination, destinationOffset);
                destinationOffset += fieldWriters[i].writeValueField(src, destination, destinationOffset, maxLengthLength);
            }
        }

        int fullFieldLength   = destinationOffset - (lengthOffset + maxLengthLength);

        switch(maxLengthLength){
            case 4 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 24));
            case 3 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 16));
            case 2 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >>  8));
            case 1 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength));
        }

        return 1 + maxLengthLength + fullFieldLength;
    }

    public int writeCyclic(Object src, int maxLengthLength, byte[] destination, int destinationOffset) {

        // 1) Write the reference of the src object to the object graph write state.
        //    Root object (src) has of course not been written before, so no reason to check if it should be written or
        //    if a Reference field should be written instead.
        this.writeState.clear();
        this.writeState.addObjectWritten(src);

        // 2) call all field writers writeCyclic methods, passing object graph write state as parameter.
        destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.OBJECT << 4) | maxLengthLength));

        int lengthOffset   = destinationOffset; //store length start offset for later use
        destinationOffset += maxLengthLength;

        for(int i=0; i<fieldWriters.length; i++){
            if(fieldWriters[i] != null){
                destinationOffset += fieldWriters[i].writeKeyField(destination, destinationOffset);
                destinationOffset += fieldWriters[i].writeValueFieldCyclic(src, destination, destinationOffset, maxLengthLength, this.writeState);
            }
        }

        int fullFieldLength   = destinationOffset - (lengthOffset + maxLengthLength);

        switch(maxLengthLength){
            case 4 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 24));
            case 3 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 16));
            case 2 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >>  8));
            case 1 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength));
        }

        return 1 + maxLengthLength + fullFieldLength;
    }


    public static class CyclicObjectGraphWriteState {
        protected List<Object> objectsWritten = new ArrayList<>();

        public void addObjectWritten(Object object) {
            this.objectsWritten.add(object);
        }

        public void clear() {
            this.objectsWritten.clear();
        }


        public int getObjectIndex(Object object){
            for(int i=0, n=this.objectsWritten.size(); i<n; i++) {
                if(object == this.objectsWritten.get(i)){
                    return i;
                }
            }
            return -1;
        }

        public boolean isObjectWritten(Object object){
            for(int i=0, n=this.objectsWritten.size(); i<n; i++) {
                if(object == this.objectsWritten.get(i)){
                    return true;
                }
            }
            return false;
        }


    }





}
