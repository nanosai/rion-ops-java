package com.nanosai.rionops.rion.write.object;

import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterDouble extends RionFieldWriterBase implements IRionFieldWriter {

    public RionFieldWriterDouble(Field field, String alias) {
        super(field, alias);
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            double value = (double) field.get(sourceObject);
            long valueLongBits = Double.doubleToLongBits(value);

            //magic number "8" is the length in bytes of a 32 bit floating point number in ION.

            dest[destOffset++] = (byte) (255 & ((RionFieldTypes.FLOAT << 4) | 8));

            for(int i=(8-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (valueLongBits >> i));
            }

            return 9;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
