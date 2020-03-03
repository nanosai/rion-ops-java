package com.nanosai.rionops.rion.read.object;


import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderObject implements IRionFieldReader {

    private Field field     = null;
    private Class typeClass = null;

    private Field[] fields = null;

    private Map<RionKeyFieldKey, IRionFieldReader> fieldReaderMap = new HashMap<>();
    private RionFieldReaderNop nopFieldReader = new RionFieldReaderNop();

    private RionKeyFieldKey currentKeyFieldKey = new RionKeyFieldKey();

    public RionFieldReaderObject(Field field, IRionObjectReaderConfigurator configurator) {
        this.field     = field;
        this.typeClass = field.getType();
        this.fields    = this.typeClass.getDeclaredFields();
    }

    public void generateFieldReaders(IRionObjectReaderConfigurator configurator, Map<Field, IRionFieldReader> existingFieldReaders) {
        this.fieldReaderMap = RionFieldReaderUtil.createFieldReaders(this.fields, configurator, existingFieldReaders);
    }


    @Override
    public int read(byte[] source, int sourceOffset, Object finalDestination) {
        this.currentKeyFieldKey.setSource(source);

        Object destination =  instantiateType();  //todo move until after null check (lengthLength == 0) ??

        int leadByte = 255 & source[sourceOffset++];
        int fieldType = leadByte >> 4;

        //todo if not object - throw exception ?

        int lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        if(lengthLength == 0){
            //todo set null value on field ?
            /*
            try {
                this.field.set(finalDestination, null);
            } catch (IllegalAccessException e) {
                //todo do something more intelligent here!
                e.printStackTrace();
            }
             */

            return 1; //object field with value null is always 1 byte long.
        }

        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }
        int thisFieldLength = 1 + lengthLength + length;
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
                    sourceOffset += reader.read(source, sourceOffset, destination);
                } else {
                    //next field is also a key - meaning the previous key has a value of null (no value field following it).
                    reader.setNull(destination);
                }
            }

        }

        try {
            this.field.set(finalDestination, destination);
        } catch (IllegalAccessException e) {
            //todo do something more intelligent here!
            e.printStackTrace();
        }

        //return 1 + lengthLength + length;
        return thisFieldLength;

    }


    @Override
    public int readAcyclic(byte[] source, int sourceOffset, Object finalDestination) {
        return read(source, sourceOffset, finalDestination);
    }

    @Override
    public int readCyclic(byte[] source, int sourceOffset, Object finalDestination, RionObjectReader.CyclicObjectGraphReadState readState) {
        int leadByte     = 255 & source[sourceOffset++];
        int fieldType    = leadByte >> 4;
        int extFieldType = -1;
        int lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        //null check (lengthLength == 0) ?
        if(lengthLength == 0) {
            try {
                this.field.set(finalDestination, null);
            } catch (IllegalAccessException e) {
                //todo do something more intelligent here!
                e.printStackTrace();
            }
            return 1; // null fields are always only 1 byte long
        }

        if(fieldType == RionFieldTypes.EXTENDED) {
            extFieldType = 255 & source[sourceOffset++];
        }

        if(fieldType == RionFieldTypes.EXTENDED && extFieldType == RionFieldTypes.EXT_REFERENCE) {
            //read reference field.
            int referencedObjectIndex = 255 & source[sourceOffset++];
            for(int i=1;i<lengthLength; i++){
                referencedObjectIndex <<= 8;
                referencedObjectIndex |= 255 & source[sourceOffset++];
            }

            Object destination = readState.getObject(referencedObjectIndex);

            try {
                this.field.set(finalDestination, destination);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return 2 + lengthLength;
        } else {
            this.currentKeyFieldKey.setSource(source);

            Object destination =  instantiateType();
            readState.addObjectAsRead(destination);

            //todo if not object - throw exception

            //todo is this nulll check necessary anymore, since we have already made a null check earlier?
            //if(lengthLength == 0){
            //    return 1; //object field with value null is always 1 byte long.
            //}

            int length = 255 & source[sourceOffset++];  //length gets overwritten - distinguish between outer field total length (object length) and nested field length
            for(int i=1; i<lengthLength; i++){
                length <<= 8;
                length |= 255 & source[sourceOffset++];
            }
            int thisFieldLength =  1 + lengthLength + length;
            int endIndex = sourceOffset + length;

            while(sourceOffset < endIndex){
                leadByte     = 255 & source[sourceOffset++];
                fieldType    = leadByte >> 4;
                lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

                //todo can this be optimized with a switch statement?

                //expect a key field
                if(fieldType == RionFieldTypes.KEY || fieldType == RionFieldTypes.KEY_SHORT){

                    //distinguish between length and lengthLength depending on compact key field or normal key field
                    length = 0; //todo use another variable for this field's length, instead of overwriting object field's length.
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

                    //find beginning of next field after key / key short field - then call field reader.
                    sourceOffset += length;

                    //todo check for end of object - if found, call reader.setNull() - no value field following the key field.

                    int nextLeadByte  = 255 & source[sourceOffset];
                    int nextFieldType = nextLeadByte >> 4;

                    if(nextFieldType != RionFieldTypes.KEY && nextFieldType != RionFieldTypes.KEY_SHORT){
                        int fieldLength = reader.readCyclic(source, sourceOffset, destination, readState);
                        sourceOffset += fieldLength;
                    } else {
                        //next field is also a key - meaning the previous key has a value of null (no value field following it).
                        reader.setNull(destination);
                    }
                }

            }

            try {
                this.field.set(finalDestination, destination);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            //int totalLength = 1 + lengthLength + length;
            //return totalLength;

            return thisFieldLength;
        }
    }



    @Override
    public void setNull(Object destination) {
        try {
            field.set(destination, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

}
