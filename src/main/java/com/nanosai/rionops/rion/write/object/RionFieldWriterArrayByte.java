package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.RionUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterArrayByte extends RionFieldWriterBase implements IRionFieldWriter {

    public RionFieldWriterArrayByte(Field field, String alias) {
        super(field, alias);
    }

    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            byte[] value = (byte[]) field.get(sourceObject);

            if(value == null) {
                dest[destOffset++] = (byte) (255 & ((RionFieldTypes.BYTES << 4))); //byte array which is null
                return 1;
            }

            int length = value.length;

            int lengthLength = RionUtil.byteLengthOfInt64Value(length);
            dest[destOffset++] = (byte) (255 & ((RionFieldTypes.BYTES << 4) | lengthLength) );

            for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (length >> i));
            }

            System.arraycopy(value, 0, dest, destOffset, value.length);

            return 1 + lengthLength + length; //total length of a UTF-8 field
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
