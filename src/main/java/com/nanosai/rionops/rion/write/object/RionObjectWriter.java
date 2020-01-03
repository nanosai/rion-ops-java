package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;

import java.util.HashMap;

/**
 * An RionObjectWriter instance can write an object (instance) of some class to ION data ("ionize" the object in other words).
 * An RionObjectWriter is targeted at a single Java class. To serialize objects of multiple classes, create one RionObjectWriter
 * per class.
 *
 */
public class RionObjectWriter {

    public Class   typeClass = null;
    public IRionFieldWriter[] fieldWriters = null;

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


    public int writeObject(Object src, int maxLengthLength, byte[] destination, int destinationOffset){
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




}
