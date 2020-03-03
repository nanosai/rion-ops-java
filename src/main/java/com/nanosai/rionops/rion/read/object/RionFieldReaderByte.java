package com.nanosai.rionops.rion.read.object;

import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderByte implements IRionFieldReader {

    private Field field = null;

    public RionFieldReaderByte(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        //int fieldType    = leadByte >> 3;  //todo use field type for validation ?
        int length = leadByte & 15;

        if(length == 0){
            return 1; //byte field with null value is always 1 byte long.
        }

        byte theByte = (byte) (255 & source[sourceOffset++]);
        for(int i=1;i<length; i++){
            theByte <<= 8;
            theByte |= 255 & source[sourceOffset++];
        }
        if( (leadByte >> 4) == RionFieldTypes.INT_NEG){
            theByte = (byte) ((-theByte) - 1);
        }


        try {
            field.set(destination, theByte);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 1 + length;
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
        try {
            field.set(destination, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
