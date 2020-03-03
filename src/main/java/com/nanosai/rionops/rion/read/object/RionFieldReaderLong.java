package com.nanosai.rionops.rion.read.object;

import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderLong implements IRionFieldReader {

    private Field field = null;

    public RionFieldReaderLong(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = source[sourceOffset++];
        //int fieldType    = leadByte >> 3;  //todo use field type for validation ?
        int length = leadByte & 15;

        if(length == 0){
            return 1; //long field with null value is always 1 byte long
        }

        long theLong = 0;
        for(int i=0;i<length; i++){
            theLong <<= 8;
            theLong |= (255 & source[sourceOffset++]);
        }
        if( (leadByte >> 4) == RionFieldTypes.INT_NEG){
            theLong = (-theLong) - 1;
        }


        try {
            field.set(destination, theLong);
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
