package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.RionUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterString extends RionFieldWriterBase implements IRionFieldWriter {

    public RionFieldWriterString(Field field, String alias) {
        super(field, alias);
    }

    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            String value = (String) field.get(sourceObject);

            if(value == null){
                dest[destOffset++] = (byte) (255 & ((RionFieldTypes.UTF_8 << 4) | 0));
                return 1;
            }

            //todo optimize this - do not get bytes from a string like this. UTF-8 encode char-for-char with charAt() instead.
            byte[] valueBytes = value.getBytes("UTF-8");


            int length = valueBytes.length;

            if(length > 0 && length <= 15){
                dest[destOffset++] = (byte) (255 & ((RionFieldTypes.UTF_8_SHORT << 4) | length) );
                System.arraycopy(valueBytes, 0, dest, destOffset, valueBytes.length);

                return 1 + length;
            } else {
                int lengthLength = RionUtil.byteLengthOfInt64Value(length);
                dest[destOffset++] = (byte) (255 & ((RionFieldTypes.UTF_8 << 4) | lengthLength) );

                for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                    dest[destOffset++] = (byte) (255 & (length >> i));
                }

                System.arraycopy(valueBytes, 0, dest, destOffset, valueBytes.length);

                return 1 + lengthLength + length; //total length of a UTF-8 field
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //will never happen - UTF-8 always supported
        }
        return 0;
    }
}
