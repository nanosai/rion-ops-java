package com.nanosai.rionops.rion.read.object;


import com.nanosai.rionops.rion.RionFieldTypes;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An RionObjectReader instance can read an object (instance) of some class from ION data ("normalize" the object in
 * other words). An RionObjectReader instance is targeted at a single Java class. To read objects of multiple classes,
 * create on RionObjectReader per class you want to read instances of.
 *
 *
 */
public class RionObjectReader<T> {

    private Class typeClass = null;

    private Map<RionKeyFieldKey, IRionFieldReader> fieldReaderMap = new HashMap<>();
    private RionFieldReaderNop nopFieldReader = new RionFieldReaderNop();

    private RionKeyFieldKey currentKeyFieldKey = new RionKeyFieldKey();

    private CyclicObjectGraphReadState readState = new CyclicObjectGraphReadState();


    public RionObjectReader(Class typeClass, Map<RionKeyFieldKey, IRionFieldReader> fieldReaderMap) {
        this.typeClass = typeClass;
        this.fieldReaderMap = fieldReaderMap;
    }


    /**
     * Creates an RionObjectReader targeted at the given class.
     * @param typeClass The class this RionObjectReader instance should be able to read instances of, from ION data.
     */
    public RionObjectReader(Class<T> typeClass) {
        this(typeClass, new RionObjectReaderConfiguratorNopImpl());
     }


    /**
     * Creates an RionObjectReader targeted at the given class.
     * The IRionObjectReaderConfigurator can configure (modify) this RionObjectReader instance.
     * For instance, the configurator can signal that some fields should not be read, or that different field names
     * are used in the ION data which should be mapped to other field names in the target Java class.
     *
     * @param typeClass The class this RionObjectReader instance should be able to read instances of, from ION data.
     * @param configurator  The configurator that can configure each field reader (one per field in the target class) of this RionObjectReader - even exclude them.
     */
    public RionObjectReader(Class<T> typeClass, IRionObjectReaderConfigurator configurator) {
        this.typeClass = typeClass;
        Field[] fields = this.typeClass.getDeclaredFields();

        this.fieldReaderMap = RionFieldReaderUtil.createFieldReaders(fields, configurator, new HashMap<>());
    }



    private void putFieldReader(String fieldName, IRionFieldReader fieldReader) {
        try {
            this.fieldReaderMap.put(new RionKeyFieldKey(fieldName.getBytes("UTF-8")), fieldReader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Deprecated //Use readAcyclic() instead
    public Object read(byte[] source, int sourceOffset){
        return read(source, sourceOffset, instantiateType());
    }


    @Deprecated //Use readAcyclic() instead
    public Object read(byte[] source, int sourceOffset, Object dest){
        this.currentKeyFieldKey.setSource(source);

        int leadByte = 255 & source[sourceOffset++];
        int fieldType = leadByte >> 4;

        //todo if not object - throw exception

        int lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        if(lengthLength == 0){
            return null; //object field with value null is always 1 byte long.
        }

        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }
        int endIndex = sourceOffset + length;

        while(sourceOffset < endIndex){
            leadByte     = 255 & source[sourceOffset++];
            fieldType    = leadByte >> 4;
            lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

            //todo can this be optimized with a switch statement?

            //expect a key field
            if(fieldType == RionFieldTypes.KEY || fieldType == RionFieldTypes.KEY_SHORT){

                //distinguish between length and lengthLength depending on compact key field or normal key field
                length = 0;
                if(fieldType == RionFieldTypes.KEY_SHORT){
                    length = leadByte & 15;
                } else {
                    for(int i=0; i<lengthLength; i++){
                        length <<= 8;
                        length |= 255 & source[sourceOffset++];
                    }
                }

                this.currentKeyFieldKey.setOffsets(sourceOffset, length);

                IRionFieldReader reader = this.fieldReaderMap.get(this.currentKeyFieldKey);
                if(reader == null){
                    reader = this.nopFieldReader;
                }

                //find beginning of next field value - then call field reader.
                sourceOffset += length;

                //todo check for end of object - if found, call reader.setNull() - no value field following the key field.

                int nextLeadByte  = 255 & source[sourceOffset];
                int nextFieldType = nextLeadByte >> 4;

                if(nextFieldType != RionFieldTypes.KEY && nextFieldType != RionFieldTypes.KEY_SHORT){
                    sourceOffset += reader.read(source, sourceOffset, dest);
                } else {
                    //next field is also a key - meaning the previous key has a value of null (no value field following it).
                    reader.setNull(dest);
                }
            }
        }

        return dest;
    }

    public Object readCyclic(byte[] source, int sourceOffset){
        return readCyclic(source, sourceOffset, instantiateType());
    }


    public Object readCyclic(byte[] source, int sourceOffset, Object dest) {
        this.readState.clear();
        this.readState.addObjectAsRead(dest);

        this.currentKeyFieldKey.setSource(source);

        int leadByte = 255 & source[sourceOffset++];
        int fieldType = leadByte >> 4;

        //todo if not object - throw exception

        int lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        if(lengthLength == 0){
            return null; //object field with value null is always 1 byte long.
        }

        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }
        int endIndex = sourceOffset + length;

        while(sourceOffset < endIndex){
            leadByte     = 255 & source[sourceOffset++];
            fieldType    = leadByte >> 4;
            lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

            //todo can this be optimized with a switch statement?

            //expect a key field
            if(fieldType == RionFieldTypes.KEY || fieldType == RionFieldTypes.KEY_SHORT){

                //distinguish between length and lengthLength depending on compact key field or normal key field
                length = 0;
                if(fieldType == RionFieldTypes.KEY_SHORT){
                    length = leadByte & 15;
                } else {
                    for(int i=0; i<lengthLength; i++){
                        length <<= 8;
                        length |= 255 & source[sourceOffset++];
                    }
                }

                this.currentKeyFieldKey.setOffsets(sourceOffset, length);

                IRionFieldReader fieldReader = this.fieldReaderMap.get(this.currentKeyFieldKey);
                if(fieldReader == null){
                    fieldReader = this.nopFieldReader;
                }

                //find beginning of next field value - then call field reader.
                sourceOffset += length;

                //todo check for end of object - if found, call reader.setNull() - no value field following the key field.

                int nextLeadByte  = 255 & source[sourceOffset];
                int nextFieldType = nextLeadByte >> 4;

                if(nextFieldType != RionFieldTypes.KEY && nextFieldType != RionFieldTypes.KEY_SHORT){
                    int fieldLength = fieldReader.readCyclic(source, sourceOffset, dest, this.readState);
                    sourceOffset += fieldLength;
                } else {
                    //next field is also a key - meaning the previous key has a value of null (no value field following it).
                    fieldReader.setNull(dest);
                }
            }
        }

        return dest;
    }



    private Object instantiateType() {
        try {
             return this.typeClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null; //todo remove later when rethrowing exceptions.
    }


    public static class CyclicObjectGraphReadState {
        protected List<Object> objects = new ArrayList<>();

        public void clear() {
            this.objects.clear();
        }

        public void addObjectAsRead(Object obj) {
            this.objects.add(obj);
        }

        public Object getObject(int index){
            return this.objects.get(index);
        }
    }

}
