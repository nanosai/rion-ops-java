package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.pojo.*;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionObjectWriterTest {


    @Test
    public void test() {
        RionObjectWriter writer = new RionObjectWriter(TestPojo.class);
        byte[] dest   = new byte[1024];

        TestPojo testPojo = new TestPojo();
        Calendar calendar = testPojo.field8;
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2014);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        writer.writeObject(testPojo, 2, dest, 0);

        int index = 0;

        assertEquals((RionFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);  //object field started - 194 = object field type << 3 | 2 (sourceLength sourceLength)
        assertEquals(  0, 255 & dest[index++]);  //sourceLength of object - MSB
        assertEquals(118, 255 & dest[index++]);  //sourceLength of object - LSB

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('0', 255 & dest[index++]);  //value of char 6 field

        assertEquals((RionFieldTypes.BOOLEAN << 4) | 1, 255 & dest[index++]);  //lead byte of boolean field

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('1', 255 & dest[index++]);  //value of char 6 field

        assertEquals((RionFieldTypes.INT_POS << 4) | 2, 255 & dest[index++]);  //lead byte of boolean field
        assertEquals( 1234 >> 8 , 255 & dest[index++]);  //value of char 1 field
        assertEquals( 1234 & 255, 255 & dest[index++]);  //value of char 1 field

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);  //lead byte of compact key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('2', 255 & dest[index++]);  //value of char 6 field

        int floatBits = Float.floatToIntBits(123.12F);
        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);  //lead byte of sshort field
        assertEquals( 255 & (floatBits >> 24), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (floatBits >> 16), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (floatBits >>  8), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (floatBits)      , 255 & dest[index++]);  //value of char 1 field

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);  //value of char 2 field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('3', 255 & dest[index++]);  //value of char 6 field

        long longBits = Double.doubleToLongBits(123456.1234D);
        assertEquals((RionFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);  //lead byte of sint field
        assertEquals( 255 & (longBits >> 56), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 48), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 40), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 32), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 24), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >> 16), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits >>  8), 255 & dest[index++]);  //value of char 1 field
        assertEquals( 255 & (longBits)      , 255 & dest[index++]);  //value of char 1 field

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);  //value of char 2 field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('4', 255 & dest[index++]);  //value of char 6 field

        assertEquals((RionFieldTypes.UTF_8_SHORT << 4) | 7, 255 & dest[index++]);  //lead byte of long field
        assertEquals('a', 255 & dest[index++]);  //value of long field
        assertEquals('b', 255 & dest[index++]);  //value of long field
        assertEquals('c', 255 & dest[index++]);  //value of long field
        assertEquals('d', 255 & dest[index++]);  //value of long field
        assertEquals('e', 255 & dest[index++]);  //value of long field
        assertEquals('f', 255 & dest[index++]);  //value of long field
        assertEquals('g', 255 & dest[index++]);  //value of long field

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('5', 255 & dest[index++]);  //value of char 6 field


        assertEquals((RionFieldTypes.UTF_8 << 4) | 1, 255 & dest[index++]);  //lead byte of long field
        assertEquals( 16, 255 & dest[index++]);  //value of long field
        assertEquals('0', 255 & dest[index++]);  //value of long field
        assertEquals('1', 255 & dest[index++]);  //value of long field
        assertEquals('2', 255 & dest[index++]);  //value of long field
        assertEquals('3', 255 & dest[index++]);  //value of long field
        assertEquals('4', 255 & dest[index++]);  //value of long field
        assertEquals('5', 255 & dest[index++]);  //value of long field
        assertEquals('6', 255 & dest[index++]);  //value of long field
        assertEquals('7', 255 & dest[index++]);  //value of long field
        assertEquals('8', 255 & dest[index++]);  //value of long field
        assertEquals('9', 255 & dest[index++]);  //value of long field
        assertEquals('0', 255 & dest[index++]);  //value of long field
        assertEquals('1', 255 & dest[index++]);  //value of long field
        assertEquals('2', 255 & dest[index++]);  //value of long field
        assertEquals('3', 255 & dest[index++]);  //value of long field
        assertEquals('4', 255 & dest[index++]);  //value of long field
        assertEquals('5', 255 & dest[index++]);  //value of long field

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('6', 255 & dest[index++]);  //value of char 6 field

        assertEquals((RionFieldTypes.UTF_8 << 4) | 1, 255 & dest[index++]);  //lead byte of long field
        assertEquals( 0, 255 & dest[index++]);  //field length

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('7', 255 & dest[index++]);  //value of char 6 field

        assertEquals((RionFieldTypes.UTF_8 << 4) | 0, 255 & dest[index++]);  //lead byte of long field


        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);   //lead byte of key field
        assertEquals('f', 255 & dest[index++]);   //value of char 1 field
        assertEquals('i', 255 & dest[index++]);   //value of char 2 field
        assertEquals('e', 255 & dest[index++]);   //value of char 3 field
        assertEquals('l', 255 & dest[index++]);   //value of char 4 field
        assertEquals('d', 255 & dest[index++]);   //value of char 5 field
        assertEquals('8', 255 & dest[index++]);  //value of char 6 field

        assertEquals((RionFieldTypes.UTC_DATE_TIME << 4) | 7, 255 & dest[index++]);  //lead byte of long field
        assertEquals(2014 >> 8 , 255 & dest[index++]);
        assertEquals(2014 & 255, 255 & dest[index++]);
        assertEquals( 12, 255 & dest[index++]);
        assertEquals( 31, 255 & dest[index++]);
        assertEquals( 23, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);
        assertEquals( 59, 255 & dest[index++]);


    }


    @Test
    public void testPojoArrayDouble() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayDouble.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayDouble pojo = new PojoArrayDouble();
        pojo.doubles = new double[]{ 1.1d, 4.4d, 9.9d, -1.1d };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);
        assertEquals(50, bytesWritten);
        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 1, 255 & dest[index++]);
        assertEquals(48, 255 & dest[index++]);
        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 7, 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('u', 255 & dest[index++]);
        assertEquals('b', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);

        assertEquals((RionFieldTypes.ARRAY << 4) | 1, 255 & dest[index++]);
        assertEquals(38, 255 & dest[index++]);

        //array element count
        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d) >> 56), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d) >> 48), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d) >> 40), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d) >> 32), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(1.1d)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d) >> 56), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d) >> 48), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d) >> 40), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d) >> 32), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(4.4d)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d) >> 56), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d) >> 48), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d) >> 40), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d) >> 32), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(9.9d)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 8, 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d) >> 56), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d) >> 48), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d) >> 40), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d) >> 32), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Double.doubleToLongBits(-1.1d)      ), 255 & dest[index++]);
    }


    @Test
    public void testPojoArrayFloat() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayFloat.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayFloat pojo = new PojoArrayFloat();
        pojo.floats = new float[]{ 1.1f, 4.4f, 9.9f, -1.1f };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);
        assertEquals(33, bytesWritten);
        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 1, 255 & dest[index++]);
        assertEquals(31, 255 & dest[index++]);
        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);

        assertEquals((RionFieldTypes.ARRAY << 4) | 1, 255 & dest[index++]);
        assertEquals(22, 255 & dest[index++]);

        //array element count
        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(1.1f) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(1.1f) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(1.1f) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(1.1f)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(4.4f) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(4.4f) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(4.4f) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(4.4f)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(9.9f) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(9.9f) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(9.9f) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(9.9f)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(-1.1f) >> 24), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(-1.1f) >> 16), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(-1.1f) >>  8), 255 & dest[index++]);
        assertEquals( 255 & (Float.floatToIntBits(-1.1f)      ), 255 & dest[index++]);
    }


    @Test
    public void testPojoArrayShort() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayShort.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayShort pojo = new PojoArrayShort();
        pojo.shorts = new short[]{ 1, 4, 9, -1 };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);
        assertEquals(21, bytesWritten);
        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 1, 255 & dest[index++]);
        assertEquals(19, 255 & dest[index++]);
        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);

        assertEquals((RionFieldTypes.ARRAY << 4) | 1, 255 & dest[index++]);
        assertEquals(10, 255 & dest[index++]);

        //array element count
        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 1, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 9, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_NEG << 4) | 1, 255 & dest[index++]);
        assertEquals( 0, 255 & dest[index++]);
    }


    @Test
    public void testPojoArrayInt() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayInt.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayInt pojo = new PojoArrayInt();
        pojo.ints = new int[]{ 1, 4, 9, -1 };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);
        assertEquals(19, bytesWritten);
        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 1, 255 & dest[index++]);
        assertEquals(17, 255 & dest[index++]);
        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 4, 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);

        assertEquals((RionFieldTypes.ARRAY << 4) | 1, 255 & dest[index++]);
        assertEquals(10, 255 & dest[index++]);

        //array element count
        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 1, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 9, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_NEG << 4) | 1, 255 & dest[index++]);
        assertEquals( 0, 255 & dest[index++]);
    }


    @Test
    public void testPojoArrayLong() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayLong.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayLong pojo = new PojoArrayLong();
        pojo.longs = new long[]{ 1, 4, 9, -1 };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);
        assertEquals(20, bytesWritten);
        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 1, 255 & dest[index++]);
        assertEquals( 18, 255 & dest[index++]);
        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 5, 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('g', 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);

        assertEquals((RionFieldTypes.ARRAY << 4) | 1, 255 & dest[index++]);
        assertEquals(10, 255 & dest[index++]);

        //array element count
        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 1, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 9, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_NEG << 4) | 1, 255 & dest[index++]);
        assertEquals( 0, 255 & dest[index++]);


    }

    @Test
    public void testPojoArrayByte() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayByte.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayByte pojo = new PojoArrayByte();
        pojo.bytes = new byte[]{ 1, 4, 9 };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        int index  = 0;
        assertEquals(13, bytesWritten);
        assertEquals((RionFieldTypes.OBJECT << 4) | 1, 255 & dest[index++]);
        assertEquals( 11, 255 & dest[index++]);
        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 5, 255 & dest[index++]);
        assertEquals('b', 255 & dest[index++]);
        assertEquals('y', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);
        assertEquals((RionFieldTypes.BYTES << 4) | 1, 255 & dest[index++]);
        assertEquals( 3, 255 & dest[index++]);
        assertEquals( 1, 255 & dest[index++]);
        assertEquals( 4, 255 & dest[index++]);
        assertEquals( 9, 255 & dest[index++]);


    }


    @Test
    public void testPojoArray10Float() {
        RionObjectWriter writer = new RionObjectWriter(PojoArray10Float.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArray10Float pojoArray10 = new PojoArray10Float(10);

        int length = writer.writeObject(pojoArray10, 2, dest, 0);
        System.out.println("sourceLength = " + length);

    }


    @Test
    public void testPojoWithPojo() {
        RionObjectWriter writer = new RionObjectWriter(PojoWithPojo.class);

        byte[] dest   = new byte[100 * 1024];

        PojoWithPojo pojo = new PojoWithPojo();

        int bytesWritten = writer.writeObject(pojo, 2, dest, 0);

        System.out.println("bytesWritten = " + bytesWritten);

        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);
        assertEquals( 100, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);

        assertEquals((RionFieldTypes.OBJECT    << 4) | 2, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);
        assertEquals(  90, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   1, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   2, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('3', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   3, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('4', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   4, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('5', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   5, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('6', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   6, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('7', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   7, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('8', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   8, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('9', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS    << 4) | 1, 255 & dest[index++]);
        assertEquals(   9, 255 & dest[index++]);
    }



    @Test
    public void testConfigurator() {
        RionObjectWriter writer = new RionObjectWriter(SmallPojo.class, fieldConfig -> {
            if("field0".equals(fieldConfig.fieldName)){
                fieldConfig.alias = "f0";
            } else if("field1".equals(fieldConfig.fieldName)){
                fieldConfig.alias = "f1";
            } else if("field2".equals(fieldConfig.fieldName)){
                fieldConfig.include = false;
            }
        });

        byte[] dest   = new byte[100 * 1024];

        SmallPojo pojo = new SmallPojo();

        int bytesWritten = writer.writeObject(pojo, 2, dest, 0);

        System.out.println("bytesWritten = " + bytesWritten);

        int index = 0;

        assertEquals((RionFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);
        assertEquals(  10, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 2, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);

        assertEquals((RionFieldTypes.BOOLEAN << 4) | 1, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 2, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 2, 255 & dest[index++]);
        assertEquals( 1234 >> 8 , 255 & dest[index++]);
        assertEquals( 1234 & 255, 255 & dest[index++]);

    }


    @Test
    public void testConfiguratorRecursively() {
        RionObjectWriter writer = new RionObjectWriter(PojoWithPojo.class, fieldConfig -> {

            assertNotNull(fieldConfig.field);

            if(PojoWithPojo.class.equals(fieldConfig.field.getDeclaringClass())){
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                }
            }
            if(Pojo10Int.class.equals(fieldConfig.field.getDeclaringClass())){
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                } else if("field1".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f1";
                } else if("field2".equals(fieldConfig.fieldName)){
                    fieldConfig.include = false;
                }
            }
        });

        byte[] dest   = new byte[100 * 1024];

        PojoWithPojo pojo = new PojoWithPojo();
        pojo.field0.field0 = 10;
        pojo.field0.field1 = 11;
        pojo.field0.field2 = 12;
        pojo.field0.field3 = 13;
        pojo.field0.field4 = 14;
        pojo.field0.field5 = 15;
        pojo.field0.field6 = 16;
        pojo.field0.field7 = 17;
        pojo.field0.field8 = 18;
        pojo.field0.field9 = 19;

        int bytesWritten = writer.writeObject(pojo, 2, dest, 0);

        System.out.println("bytesWritten = " + bytesWritten);

        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);
        assertEquals(  79, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 2, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);

        assertEquals((RionFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);
        assertEquals(  73, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 2, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 10, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 2, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 11, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('3', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 13, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('4', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 14, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('5', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 15, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('6', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 16, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('7', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 17, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('8', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 18, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('9', 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals( 19, 255 & dest[index++]);
    }


    @Test
    public void testConfiguratorOnTablesRecursively() {
        RionObjectWriter writer = new RionObjectWriter(PojoArray10Float.class, fieldConfig -> {

            assertNotNull(fieldConfig.field);

            if(PojoArray10Float.class.equals(fieldConfig.field.getDeclaringClass())){
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                }
                //System.out.println("Configurator applied to PojoArray10Float");
            }
            if(Pojo10Float.class.equals(fieldConfig.field.getDeclaringClass())){
                //System.out.println("Configurator applied to Pojo10Float");
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                } else if("field1".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f1";
                } else if("field2".equals(fieldConfig.fieldName)){
                    fieldConfig.include = false;
                }
            }
        });

        PojoArray10Float pojo = new PojoArray10Float(3);

        byte[] dest   = new byte[100 * 1024];
        int bytesWritten = writer.writeObject(pojo, 2, dest, 0);
        System.out.println("bytesWritten = " + bytesWritten);

        int index = 0;
        assertEquals((RionFieldTypes.OBJECT << 4) | 2, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);
        assertEquals( 201, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 5, 255 & dest[index++]);
        assertEquals('p', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('j', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('s', 255 & dest[index++]);

        assertEquals((RionFieldTypes.TABLE << 4) | 2, 255 & dest[index++]);
        assertEquals(   0, 255 & dest[index++]);
        assertEquals( 192, 255 & dest[index++]);

        assertEquals((RionFieldTypes.INT_POS << 4) | 1, 255 & dest[index++]);
        assertEquals(   3, 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 2, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('0', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 2, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('3', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('4', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('5', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('6', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('7', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('8', 255 & dest[index++]);

        assertEquals((RionFieldTypes.KEY_SHORT << 4) | 6, 255 & dest[index++]);
        assertEquals('f', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('9', 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F)      ), 255 & dest[index++]);


        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F)      ), 255 & dest[index++]);


        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1.1F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12.12F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(1234.1234F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(12345.12345F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1.1F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12.12F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-123.123F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-1234.1234F)      ), 255 & dest[index++]);

        assertEquals((RionFieldTypes.FLOAT << 4) | 4, 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >> 24), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >> 16), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F) >>  8), 255 & dest[index++]);
        assertEquals(255 & (Float.floatToIntBits(-12345.12345F)      ), 255 & dest[index++]);


    }





}
