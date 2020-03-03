package com.nanosai.rionops.rion.read.object;


import com.nanosai.rionops.rion.RionFieldTypes;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderNop implements IRionFieldReader {

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        int fieldType    = leadByte >> 4;
        int lengthLength = leadByte & 15;  // 7 = binary 00000111 - filters out 5 top bits

        if(lengthLength == 0){
            return 1; //field with null value is always 1 byte long
        }

        //todo skip correct amount of bytes - depending on field type. Not all field types have explicit length bytes.

        switch(fieldType){
            case RionFieldTypes.BOOLEAN: {
                return 1;
            }
            case RionFieldTypes.UTF_8_SHORT: ;
            case RionFieldTypes.UTC_DATE_TIME: ;
            //case RionFieldTypes.COMPLEX_TYPE_ID_SHORT: ;
            case RionFieldTypes.KEY_SHORT: ;
            case RionFieldTypes.INT_POS: ;
            case RionFieldTypes.INT_NEG: ;
            case RionFieldTypes.FLOAT : {
                return 1 + lengthLength;
            }

            case RionFieldTypes.EXTENDED : {
                int fieldTypeExtended = source[sourceOffset++]; //read extended field type - first byte after lead byte
                switch(fieldTypeExtended) {
                    case RionFieldTypes.EXT_ELEMENT_COUNT: {
                        return 1 + 1 + lengthLength; //element count uses extended short encoding.
                    }
                }
                return 1 + 1 + lengthLength; //default extended element encoding uses 1 byte for extended type
            }

            //fine for all fields that use the lengthLength field normally - meaning Normal length fields (not Short and Tiny).
            default : {
                int fieldLength = 0;
                for(int i=0; i<lengthLength; i++){
                    fieldLength <<= 8;
                    fieldLength |= 255 & source[sourceOffset++];
                }
                return 1 + lengthLength + fieldLength;
            }
        }

    }

    @Override
    public int readAcyclic(byte[] source, int sourceOffset, Object destination) {
        return read(source, sourceOffset, destination);
    }

    @Override
    public int readCyclic(byte[] source, int sourceOffset, Object destination, RionObjectReader.CyclicObjectGraphReadState readState) {
        return read(source, sourceOffset, destination);
    }

    @Override
    public void setNull(Object destination) {
        //do nothing, right?
    }


}
