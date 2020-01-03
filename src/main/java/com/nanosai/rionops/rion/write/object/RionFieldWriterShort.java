package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.RionUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterShort extends RionFieldWriterBase implements IRionFieldWriter {


    public RionFieldWriterShort(Field field, String alias) {
        super(field, alias);
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            long value = (short) field.get(sourceObject);
            int ionFieldType = RionFieldTypes.INT_POS;
            if(value < 0){
                ionFieldType = RionFieldTypes.INT_NEG;
                value  = -(value+1);
            }

            int length = RionUtil.byteLengthOfInt64Value(value);

            dest[destOffset++] = (byte) (255 & ((ionFieldType << 4) | length));

            for(int i=(length-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (value >> i));
            }

            return 1 + length;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
