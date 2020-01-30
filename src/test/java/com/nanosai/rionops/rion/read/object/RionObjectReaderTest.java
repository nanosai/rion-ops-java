package com.nanosai.rionops.rion.read.object;


import com.nanosai.rionops.rion.pojo.*;
import com.nanosai.rionops.rion.write.object.RionObjectWriter;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionObjectReaderTest {


    @Test
    public void test() {
        RionObjectWriter writer  = new RionObjectWriter(TestPojo.class);
        RionObjectReader reader  = new RionObjectReader(TestPojo.class);

        byte[] source = new byte[10 * 1024];

        TestPojo sourcePojo = new TestPojo();
        sourcePojo.field0 = false;
        sourcePojo.field1 = 123;
        sourcePojo.field2 = 456.456F;
        sourcePojo.field3 = 456789.456789D;
        sourcePojo.field4 = "abc";
        sourcePojo.field5 = "987654321098765";

        Calendar calendar = sourcePojo.field8;
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2014);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        TestPojo destPojo = null;

        int length = writer.writeObject(sourcePojo, 2, source, 0);   //write object first

        //todo fix error in TypedObjectReader related to reading compact keys

        destPojo = (TestPojo) reader.read(source, 0);

        assertEquals(false, destPojo.field0);
        assertEquals(123, destPojo.field1);
        assertEquals(456.456F, destPojo.field2, 0);
        assertEquals("abc", destPojo.field4);
        assertEquals("987654321098765", destPojo.field5);
        assertEquals("", destPojo.field6);
        assertEquals(null, destPojo.field7);
        assertEquals(calendar, destPojo.field8);
    }


    @Test
    public void test_readInto() {
        RionObjectWriter writer  = new RionObjectWriter(TestPojo.class);
        RionObjectReader reader  = new RionObjectReader(TestPojo.class);

        byte[] source = new byte[10 * 1024];

        TestPojo sourcePojo = new TestPojo();
        sourcePojo.field0 = false;
        sourcePojo.field1 = 123;
        sourcePojo.field2 = 456.456F;
        sourcePojo.field3 = 456789.456789D;
        sourcePojo.field4 = "abc";
        sourcePojo.field5 = "987654321098765";

        Calendar calendar = sourcePojo.field8;
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2014);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        TestPojo destPojo = null;

        int length = writer.writeObject(sourcePojo, 2, source, 0);   //write object first

        //todo fix error in TypedObjectReader related to reading compact keys

        TestPojo destPojoInput = new TestPojo();
        destPojo = (TestPojo) reader.read(source, 0, destPojoInput);

        assertEquals(false, destPojo.field0);
        assertEquals(123, destPojo.field1);
        assertEquals(456.456F, destPojo.field2, 0);
        assertEquals("abc", destPojo.field4);
        assertEquals("987654321098765", destPojo.field5);
        assertEquals("", destPojo.field6);
        assertNull(destPojo.field7);
        assertEquals(calendar, destPojo.field8);
    }


    @Test
    public void testArrayDouble() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayDouble.class);
        RionObjectReader reader = new RionObjectReader(PojoArrayDouble.class);

        byte[] dest = new byte[100 * 1024];

        PojoArrayDouble pojo = new PojoArrayDouble();
        pojo.doubles = new double[]{1.1d, 4.4d, 9.9d, -1.1d};

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayDouble pojo2 = (PojoArrayDouble) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.doubles) ;
        assertEquals(4, pojo2.doubles.length);
        assertEquals(1.1d, pojo2.doubles[0], 0);
        assertEquals(4.4d, pojo2.doubles[1], 0);
        assertEquals(9.9d, pojo2.doubles[2], 0);
        assertEquals(-1.1d, pojo2.doubles[3], 0);

    }


    @Test
    public void testArrayFloat() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayFloat.class);
        RionObjectReader reader = new RionObjectReader(PojoArrayFloat.class);

        byte[] dest = new byte[100 * 1024];

        PojoArrayFloat pojo = new PojoArrayFloat();
        pojo.floats = new float[]{1.1f, 4.4f, 9.9f, -1.1f};

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayFloat pojo2 = (PojoArrayFloat) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.floats) ;
        assertEquals(4, pojo2.floats.length);
        assertEquals(1.1f, pojo2.floats[0], 0);
        assertEquals(4.4f, pojo2.floats[1], 0);
        assertEquals(9.9f, pojo2.floats[2], 0);
        assertEquals(-1.1f, pojo2.floats[3], 0);

    }


    @Test
    public void testArrayShort() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayShort.class);
        RionObjectReader reader = new RionObjectReader(PojoArrayShort.class);

        byte[] dest = new byte[100 * 1024];

        PojoArrayShort pojo = new PojoArrayShort();
        pojo.shorts = new short[]{1, 4, 9, -1};

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayShort pojo2 = (PojoArrayShort) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.shorts) ;
        assertEquals(4, pojo2.shorts.length);
        assertEquals(1, pojo2.shorts[0]);
        assertEquals(4, pojo2.shorts[1]);
        assertEquals(9, pojo2.shorts[2]);
        assertEquals(-1, pojo2.shorts[3]);

    }


    @Test
    public void testArrayInt() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayInt.class);
        RionObjectReader reader = new RionObjectReader(PojoArrayInt.class);

        byte[] dest = new byte[100 * 1024];

        PojoArrayInt pojo = new PojoArrayInt();
        pojo.ints = new int[]{1, 4, 9, -1};

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayInt pojo2 = (PojoArrayInt) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.ints) ;
        assertEquals(4, pojo2.ints.length);
        assertEquals(1, pojo2.ints[0]);
        assertEquals(4, pojo2.ints[1]);
        assertEquals(9, pojo2.ints[2]);
        assertEquals(-1, pojo2.ints[3]);

    }


    @Test
    public void testArrayLong() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayLong.class);
        RionObjectReader reader = new RionObjectReader(PojoArrayLong.class);

        byte[] dest = new byte[100 * 1024];

        PojoArrayLong pojo = new PojoArrayLong();
        pojo.longs = new long[]{1, 4, 9, -1};

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayLong pojo2 = (PojoArrayLong) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.longs) ;
        assertEquals(4, pojo2.longs.length);
        assertEquals(1, pojo2.longs[0]);
        assertEquals(4, pojo2.longs[1]);
        assertEquals(9, pojo2.longs[2]);
        assertEquals(-1, pojo2.longs[3]);

    }

    @Test
    public void testByteArrayField() {
        RionObjectWriter writer = new RionObjectWriter(PojoArrayByte.class);
        RionObjectReader reader = new RionObjectReader(PojoArrayByte.class);

        byte[] dest   = new byte[100 * 1024];

        PojoArrayByte pojo = new PojoArrayByte();
        pojo.bytes = new byte[]{ 1, 4, 9 };

        int bytesWritten = writer.writeObject(pojo, 1, dest, 0);

        PojoArrayByte pojo2 = (PojoArrayByte) reader.read(dest, 0);

        assertNotNull(pojo2) ;
        assertNotNull(pojo2.bytes) ;
        assertEquals(3, pojo2.bytes.length);
        assertEquals(1, pojo2.bytes[0]);
        assertEquals(4, pojo2.bytes[1]);
        assertEquals(9, pojo2.bytes[2]);
    }



    @Test
    public void testTableField() {
        RionObjectWriter writer  = new RionObjectWriter(TestPojoArray.class);
        RionObjectReader reader  = new RionObjectReader(TestPojoArray.class);

        byte[] source = new byte[10 * 1024];

        TestPojoArray sourcePojoArray = new TestPojoArray();

        sourcePojoArray.testObjects    = new TestPojoArray.TestObject[3];
        sourcePojoArray.testObjects[0] = new TestPojoArray.TestObject();
        sourcePojoArray.testObjects[1] = new TestPojoArray.TestObject();
        sourcePojoArray.testObjects[2] = new TestPojoArray.TestObject();

        TestPojoArray destPojo         = new TestPojoArray();

        int length = writer.writeObject(sourcePojoArray, 2, source, 0);

        destPojo = (TestPojoArray) reader.read(source, 0);

    }


    @Test
    public void testObjectField() {
        RionObjectWriter writer = new RionObjectWriter(PojoWithPojo.class);
        RionObjectReader reader = new RionObjectReader(PojoWithPojo.class);

        byte[] source   = new byte[100 * 1024];

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

        int bytesWritten = writer.writeObject(pojo, 2, source, 0);

        System.out.println("bytesWritten = " + bytesWritten);

        PojoWithPojo pojo2 = (PojoWithPojo) reader.read(source, 0);

        assertEquals(10, pojo2.field0.field0);
        assertEquals(11, pojo2.field0.field1);
        assertEquals(12, pojo2.field0.field2);
        assertEquals(13, pojo2.field0.field3);
        assertEquals(14, pojo2.field0.field4);
        assertEquals(15, pojo2.field0.field5);
        assertEquals(16, pojo2.field0.field6);
        assertEquals(17, pojo2.field0.field7);
        assertEquals(18, pojo2.field0.field8);
        assertEquals(19, pojo2.field0.field9);

    }


    @Test
    public void testReadWithConfigurator() {
        RionObjectWriter writer = new RionObjectWriter(SmallPojo.class);
        RionObjectReader reader = new RionObjectReader(SmallPojo.class);

        SmallPojo sourcePojo = new SmallPojo();
        sourcePojo.field0 = false;
        sourcePojo.field1 = 999;
        sourcePojo.field2 = 999.99f;

        byte[] source   = new byte[100 * 1024];

        writer.writeObject(sourcePojo, 2, source, 0);

        SmallPojo readPojo = (SmallPojo) reader.read(source, 0);

        assertEquals(false  , readPojo.field0);
        assertEquals(999    , readPojo.field1);
        assertEquals(999.99F, readPojo.field2, 0F);


        RionObjectReader reader2 = new RionObjectReader(SmallPojo.class, config -> {
            if("field2".equals(config.fieldName)){
                config.include = false;
            }
        });

        SmallPojo readPojo2 = (SmallPojo) reader2.read(source, 0);
        assertEquals(false  , readPojo2.field0);
        assertEquals(999    , readPojo2.field1);
        assertEquals(123.12F, readPojo2.field2, 0F);  //value should not be bytesRead.


        RionObjectWriter writer3 = new RionObjectWriter(SmallPojo.class, config -> {
            if("field0".equals(config.fieldName)){
                config.alias = "f0";
            }
        });

        RionObjectReader reader3 = new RionObjectReader(SmallPojo.class, config -> {
            if("field0".equals(config.fieldName)){
                config.alias = "f0";
            }
        });


        writer3.writeObject(sourcePojo, 2, source, 0);

        SmallPojo readPojo3 = (SmallPojo) reader3.read(source, 0);

        assertEquals(false  , readPojo3.field0);
        assertEquals(999    , readPojo3.field1);
        assertEquals(999.99F, readPojo3.field2, 0F);
    }




    @Test
    public void testReadWithConfiguratorRecursively() {
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


        RionObjectReader reader = new RionObjectReader(PojoWithPojo.class, fieldConfig -> {
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

        PojoWithPojo pojoRead = (PojoWithPojo) reader.read(dest, 0);

        assertNotNull(pojoRead);
        assertNotNull(pojoRead.field0);

        assertEquals(10, pojoRead.field0.field0);
        assertEquals(11, pojoRead.field0.field1);
        assertEquals( 2, pojoRead.field0.field2);
        assertEquals(13, pojoRead.field0.field3);
        assertEquals(14, pojoRead.field0.field4);
        assertEquals(15, pojoRead.field0.field5);
        assertEquals(16, pojoRead.field0.field6);
        assertEquals(17, pojoRead.field0.field7);
        assertEquals(18, pojoRead.field0.field8);
        assertEquals(19, pojoRead.field0.field9);
    }


    @Test
    public void testReadWithConfiguratorOnTablesRecursively() {
        RionObjectWriter writer = new RionObjectWriter(PojoArray10Float.class, fieldConfig -> {

            assertNotNull(fieldConfig.field);

            if(PojoArray10Float.class.equals(fieldConfig.field.getDeclaringClass())){
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                }
            }
            if(Pojo10Float.class.equals(fieldConfig.field.getDeclaringClass())){
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                } else if("field1".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f1";
                }
            }
        });

        PojoArray10Float pojo = new PojoArray10Float(3);
        pojo.pojos[0].field0 = 111.111f;
        pojo.pojos[0].field2 = 222.222f; //this field is ignored in the reader, so in the bytesRead object it should have the default value.
        pojo.pojos[0].field3 = 333.333f;

        pojo.pojos[1].field0 = 444.444f;
        pojo.pojos[1].field2 = 222.222f; //this field is ignored in the reader, so in the bytesRead object it should have the default value.
        pojo.pojos[1].field3 = 555.555f;

        pojo.pojos[2].field0 = 666.666f;
        pojo.pojos[2].field2 = 222.222f; //this field is ignored in the reader, so in the bytesRead object it should have the default value.
        pojo.pojos[2].field3 = 777.777f;


        byte[] dest   = new byte[100 * 1024];

        int bytesWritten = writer.writeObject(pojo, 2, dest, 0);
        System.out.println("bytesWritten = " + bytesWritten);


        RionObjectReader reader = new RionObjectReader(PojoArray10Float.class, fieldConfig -> {
            if(PojoArray10Float.class.equals(fieldConfig.field.getDeclaringClass())){
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                }
//                System.out.println("Configurator applied to PojoArray10Float");
            }
            if(Pojo10Float.class.equals(fieldConfig.field.getDeclaringClass())){
                if("field0".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f0";
                } else if("field1".equals(fieldConfig.fieldName)){
                    fieldConfig.alias = "f1";
                } else if("field2".equals(fieldConfig.fieldName)){
                    fieldConfig.include = false;
                }
//                System.out.println("Configurator applied to Pojo10Float");
            }
        });


        PojoArray10Float pojoRead = (PojoArray10Float) reader.read(dest, 0);

        assertEquals(3, pojoRead.pojos.length);

        assertEquals(111.111F     , pojoRead.pojos[0].field0, 0F);
        assertEquals(12.12F       , pojoRead.pojos[0].field1, 0F);
        assertEquals(123.123F     , pojoRead.pojos[0].field2, 0F);
        assertEquals(333.333F     , pojoRead.pojos[0].field3, 0F);
        assertEquals(12345.12345F , pojoRead.pojos[0].field4, 0F);
        assertEquals(-1.1F        , pojoRead.pojos[0].field5, 0F);
        assertEquals(-12.12F      , pojoRead.pojos[0].field6, 0F);
        assertEquals(-123.123F    , pojoRead.pojos[0].field7, 0F);
        assertEquals(-1234.1234F  , pojoRead.pojos[0].field8, 0F);
        assertEquals(-12345.12345F, pojoRead.pojos[0].field9, 0F);

        assertEquals(444.444F     , pojoRead.pojos[1].field0, 0F);
        assertEquals(12.12F       , pojoRead.pojos[1].field1, 0F);
        assertEquals(123.123F     , pojoRead.pojos[1].field2, 0F);
        assertEquals(555.555F     , pojoRead.pojos[1].field3, 0F);
        assertEquals(12345.12345F , pojoRead.pojos[1].field4, 0F);
        assertEquals(-1.1F        , pojoRead.pojos[1].field5, 0F);
        assertEquals(-12.12F      , pojoRead.pojos[1].field6, 0F);
        assertEquals(-123.123F    , pojoRead.pojos[1].field7, 0F);
        assertEquals(-1234.1234F  , pojoRead.pojos[1].field8, 0F);
        assertEquals(-12345.12345F, pojoRead.pojos[1].field9, 0F);

        assertEquals(666.666F     , pojoRead.pojos[2].field0, 0F);
        assertEquals(12.12F       , pojoRead.pojos[2].field1, 0F);
        assertEquals(123.123F     , pojoRead.pojos[2].field2, 0F);
        assertEquals(777.777F     , pojoRead.pojos[2].field3, 0F);
        assertEquals(12345.12345F , pojoRead.pojos[2].field4, 0F);
        assertEquals(-1.1F        , pojoRead.pojos[2].field5, 0F);
        assertEquals(-12.12F      , pojoRead.pojos[2].field6, 0F);
        assertEquals(-123.123F    , pojoRead.pojos[2].field7, 0F);
        assertEquals(-1234.1234F  , pojoRead.pojos[2].field8, 0F);
        assertEquals(-12345.12345F, pojoRead.pojos[2].field9, 0F);

    }


}
