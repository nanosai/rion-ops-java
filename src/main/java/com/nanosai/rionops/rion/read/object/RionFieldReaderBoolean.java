package com.nanosai.rionops.rion.read.object;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderBoolean implements IRionFieldReader {

    private Field field = null;

    public RionFieldReaderBoolean(Field field) {
        this.field = field;
    }

    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int value = (255 & source[sourceOffset]);
        value &=15;

        switch(value){
            case 1 : {
                try {
                    field.set(destination, true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2 : {
                try {
                    field.set(destination, false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
            default : {
                //do nothing - boolean value is either null (0) or undefined (3,4,5,6 or 7).
            }
        }

        return 1; // boolean field is always 1 byte long
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

    }

}
