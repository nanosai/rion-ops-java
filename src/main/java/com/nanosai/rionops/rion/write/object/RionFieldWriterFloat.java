package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterFloat extends RionFieldWriterBase implements IRionFieldWriter {


    public RionFieldWriterFloat(Field field, String alias) {
        super(field, alias);
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            float value = (float) field.get(sourceObject);
            int valueIntBits = Float.floatToIntBits(value);

            //magic number "4" is the length in bytes of a 32 bit floating point number in ION.

            dest[destOffset++] = (byte) (255 & ((RionFieldTypes.FLOAT << 4) | 4));

            for(int i=(4-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (valueIntBits >> i));
            }

            return 5;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
