package com.nanosai.rionops.rion.read.object;

import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderShort implements IRionFieldReader {

    private Field field = null;

    public RionFieldReaderShort(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        //int fieldType    = leadByte >> 3;  //todo use field type for validation ?
        int length = leadByte & 15;

        if(length == 0){
            return 1; //short field with null value is always 1 byte long.
        }

        short theShort = (short) (255 & source[sourceOffset++]);
        for(int i=1;i<length; i++){
            theShort <<= 8;
            theShort |= 255 & source[sourceOffset++];
        }
        if( (leadByte >> 4) == RionFieldTypes.INT_NEG){
            theShort = (short) ((-theShort) - 1);
        }


        try {
            field.set(destination, theShort);
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
