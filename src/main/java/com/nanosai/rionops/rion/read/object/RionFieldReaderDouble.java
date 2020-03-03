package com.nanosai.rionops.rion.read.object;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderDouble implements IRionFieldReader {

    private Field field = null;

    public RionFieldReaderDouble(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        //int fieldType    = leadByte >> 3;  //todo use for field match verification?

        int length = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        if(length == 0){
            return 1; //double field with null value is only 1 byte long
        }

        /*
        long theLong = 255 & source[sourceOffset++];;
        for(int i=1;i<length; i++){
            theLong <<= 8;
            theLong |= 255 & source[sourceOffset++];
        }
        */

        long theLong = 255 & source[sourceOffset++];
        theLong <<= 8;
        theLong |= 255 & source[sourceOffset++];
        theLong <<= 8;
        theLong |= 255 & source[sourceOffset++];
        theLong <<= 8;
        theLong |= 255 & source[sourceOffset++];
        theLong <<= 8;
        theLong |= 255 & source[sourceOffset++];
        theLong <<= 8;
        theLong |= 255 & source[sourceOffset++];
        theLong <<= 8;
        theLong |= 255 & source[sourceOffset++];
        theLong <<= 8;
        theLong |= 255 & source[sourceOffset++];


        try {
            field.set(destination, Double.longBitsToDouble(theLong));
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
