package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterBoolean extends RionFieldWriterBase implements IRionFieldWriter {

    public RionFieldWriterBoolean(Field field, String alias) {
        super(field, alias);
    }

    @Override
    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {
        try {
            boolean value = (Boolean) field.get(sourceObject);

            if(value){
                destination[destinationOffset] = (byte) (255 & ((RionFieldTypes.BOOLEAN << 4) | 1));
            } else {
                destination[destinationOffset] = (byte) (255 & ((RionFieldTypes.BOOLEAN << 4) | 2));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 1;    //total length of a boolean field is always 1
    }


 }
