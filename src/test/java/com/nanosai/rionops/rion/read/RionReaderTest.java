package com.nanosai.rionops.rion.read;

import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.write.RionWriter;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


/**
    This class tests the RionReader, but uses the RrionWriter in the tests, so RrionWriter is also tested implicitly.
 */
public class RionReaderTest {

    RionReader reader = new RionReader();


    @Test
    public void testWriteReadNullFields() {
        byte[] source = new byte[10 * 1024];

        RionWriter writer = new RionWriter();
        writer.setDestination(source, 0);

        writer.writeBytesNull();
        writer.writeBytes(null);
        writer.writeBytes( new byte[]{} );
        writer.writeBytes( new byte[]{1,2,3} );

        writer.writeBooleanNull();
        writer.writeBooleanObj(null);
        writer.writeBooleanObj(new Boolean(true));
        writer.writeBoolean(true);

        writer.writeInt64Null();
        writer.writeInt64Obj(null);
        writer.writeInt64Obj(new Long(123));
        writer.writeInt64(123);

        writer.writeFloatNull();
        writer.writeFloat32Obj (null);
        writer.writeFloat64Obj (null);
        writer.writeFloat32Obj (new Float(123.45));
        writer.writeFloat64Obj (new Double(123.45));

        writer.writeUtf8Null();
        writer.writeUtf8 ((String) null);
        writer.writeUtf8 ("" );
        writer.writeUtf8 ("123" );
        writer.writeUtf8 ((byte[]) null );
        writer.writeUtf8 ((byte[]) null, 0, 0 );
        writer.writeUtf8 (new byte[0]);
        writer.writeUtf8 (new byte[0], 0, 0 );

        writer.writeUtcNull();
        writer.writeUtc (null, 9);
        writer.writeUtc (new GregorianCalendar(), 9 );

        writer.writeArrayNull ();
        writer.writeTableNull ();
        writer.writeObjectNull ();

        RionReader reader = new RionReader(source, 0, writer.index);

        //Bytes fields
        reader.nextParse();
        assertEquals(RionFieldTypes.BYTES, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.BYTES, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.BYTES, reader.fieldType);
        assertFalse(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.BYTES, reader.fieldType);
        assertFalse(reader.isNull());

        //Boolean fields
        reader.nextParse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertFalse(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertFalse(reader.isNull());

        //Int fields
        reader.nextParse();
        assertEquals(RionFieldTypes.INT_POS, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.INT_POS, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.isNull());

        //Float fields
        reader.nextParse();
        assertEquals(RionFieldTypes.FLOAT, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.FLOAT, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.FLOAT, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.FLOAT, reader.fieldType);
        assertFalse(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.FLOAT, reader.fieldType);
        assertFalse(reader.isNull());

        //UTF-8 fields
        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertTrue(reader.isNull());
        assertNull(reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertTrue(reader.isNull());
        assertNull(reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertFalse(reader.isNull());
        assertEquals("", reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8_SHORT, reader.fieldType);
        assertFalse(reader.isNull());
        assertEquals("123", reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertTrue(reader.isNull());
        assertNull(reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertTrue(reader.isNull());
        assertNull(reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertFalse(reader.isNull());
        assertEquals(0, reader.fieldLength);
        assertEquals("", reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertFalse(reader.isNull());
        assertEquals(0, reader.fieldLength);
        assertEquals("", reader.readUtf8String());


        //UTC fields
        reader.nextParse();
        assertEquals(RionFieldTypes.UTC_DATE_TIME, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTC_DATE_TIME, reader.fieldType);
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTC_DATE_TIME, reader.fieldType);
        assertFalse(reader.isNull());

        //Array field
        reader.nextParse();
        assertEquals(RionFieldTypes.ARRAY, reader.fieldType);
        assertTrue(reader.isNull());

        //Table field
        reader.nextParse();
        assertEquals(RionFieldTypes.TABLE, reader.fieldType);
        assertTrue(reader.isNull());

        //Table field
        reader.nextParse();
        assertEquals(RionFieldTypes.OBJECT, reader.fieldType);
        assertTrue(reader.isNull());

    }



    @Test
    public void testSetSource_byteArray() {
        byte[] source = new byte[10 * 1024];

        RionWriter writer = new RionWriter();
        writer.setDestination(source, 0);

        writer.writeBytes(new byte[]{1, 2, 3, 4, 5});
        writer.writeBytes(null);

        RionReader reader = new RionReader();
        assertFalse(reader.hasNext());

        reader.setSource(source, 0, writer.index);
        assertTrue(reader.hasNext());

        writer.setDestination(source, 1000);
        writer.writeBytes(new byte[]{1, 2, 3, 4, 5});
        writer.writeBytes(null);

        reader.setSource(source, 1000, writer.index - 1000);
        assertTrue(reader.hasNext());

        reader.nextParse();
        assertEquals(5, reader.fieldLength);
        assertEquals(1, reader.source[reader.index]);
        assertEquals(2, reader.source[reader.index + 1]);
        assertEquals(3, reader.source[reader.index + 2]);
        assertEquals(4, reader.source[reader.index + 3]);
        assertEquals(5, reader.source[reader.index + 4]);


    }


    /*
    @Test
    public void testSetSource_MemoryBlock() {
        MemoryAllocator allocator = new MemoryAllocator(new byte[1024], new long[128]);
        MemoryBlock   memoryBlock = allocator.getMemoryBlock().allocate(128);

        RionWriter writer = new RionWriter().setDestination(memoryBlock);

        writer.writeKey("abc");
        writer.writeUtf8("abc");
        writer.writeInt64(123);

        memoryBlock.writeIndex = writer.index;

        RionReader RionReader = new RionReader().setSource(memoryBlock);

        assertTrue(RionReader.hasNext());


    }
    */



    @Test
    public void testReadBytes() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += RionWriter.writeBytes(source, index, new byte[]{1, 2, 3, 4, 5});
        index += RionWriter.writeBytes(source, index, null);
        index += RionWriter.writeBytesNull(source, index);

        assertEquals(9, index);

        reader.setSource(source, 0, source.length);
        reader.nextParse();

        assertEquals(RionFieldTypes.BYTES, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readBytes(dest);
        assertEquals(5, length);
        assertEquals(1, dest[0]);
        assertEquals(2, dest[1]);
        assertEquals(3, dest[2]);
        assertEquals(4, dest[3]);
        assertEquals(5, dest[4]);

        length = reader.readBytes(dest, 0, 3);
        assertEquals(3, length);
        assertEquals(2, dest[0]);
        assertEquals(3, dest[1]);
        assertEquals(4, dest[2]);

        reader.nextParse();
        length = reader.readBytes(dest, 0, 3);
        assertEquals(0, length); // null field - no bytes read.
        assertTrue(reader.isNull());

        reader.nextParse();
        length = reader.readBytes(dest, 0, 3);
        assertEquals(0, length);
        assertTrue(reader.isNull());
    }


    @Test
    public void testReadBoolean() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += RionWriter.writeBoolean(source, index, true);
        index += RionWriter.writeBoolean(source, index, false);
        index += RionWriter.writeBooleanObj(source, index, null);
        index += RionWriter.writeBooleanNull(source, index);

        assertEquals(4, index);

        reader.parse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertTrue(reader.readBoolean());

        reader.nextParse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertFalse(reader.readBoolean());

        reader.nextParse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertNull(reader.readBooleanObj());
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(RionFieldTypes.BOOLEAN, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertNull(reader.readBooleanObj());
        assertTrue(reader.isNull());

    }


    @Test
    public void testReadInt64(){
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += RionWriter.writeInt64(source, index,  65535);
        index += RionWriter.writeInt64(source, index, -65535);
        index += RionWriter.writeInt64Obj(source, index, null);
        index += RionWriter.writeInt64Null(source, index);

        assertEquals(8, index);

        reader.parse();
        assertEquals(65535, reader.readInt64());

        reader.nextParse();
        assertEquals(-65535, reader.readInt64());

        reader.nextParse();
        assertEquals(0   , reader.readInt64());
        assertEquals(null, reader.readInt64Obj());
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(0   , reader.readInt64());
        assertEquals(null, reader.readInt64Obj());
        assertTrue(reader.isNull());
    }


    @Test
    public void testReadFloat32() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += RionWriter.writeFloat32(source, index, 123.45F);
        index += RionWriter.writeFloat32Obj(source, index, null);
        index += RionWriter.writeFloatNull(source, index);

        assertEquals(7, index);

        reader.nextParse();
        assertEquals(123.45F, reader.readFloat32(), 0);

        reader.nextParse();
        assertEquals(0, reader.readFloat32(),0);
        assertNull(reader.readFloat32Obj());
        assertTrue(reader.isNull());

        reader.nextParse();
        assertEquals(0, reader.readFloat32(),0);
        assertNull(reader.readFloat32Obj());
        assertTrue(reader.isNull());

    }


    @Test
    public void testReadFloat64() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += RionWriter.writeFloat64(source, index, 123456.123456D);
        index += RionWriter.writeFloat64Obj(source, index, null);

        reader.parse();
        assertEquals(123456.123456D, reader.readFloat64(), 0);

        reader.next();
        reader.parse();
        assertEquals(0, reader.readFloat64(),0);
        assertNull(reader.readFloat64Obj());
        assertTrue(reader.isNull());

    }

    @Test
    public void testReadUtf8() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += RionWriter.writeUtf8(source, index, "Hellå");
        assertEquals(7, index);
        index += RionWriter.writeUtf8(source, index, "0123456789abcdef");
        index += RionWriter.writeUtf8(source, index, (String) null);
        index += RionWriter.writeUtf8Null(source, index);

        assertEquals(27, index);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(RionFieldTypes.UTF_8_SHORT, reader.fieldType);
        assertEquals(6, reader.fieldLength);  //Danish character å requires 2 bytes in UTF-8

        int length = reader.readUtf8(dest);
        assertEquals(6, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals(0xc3, 255 & dest[4]);
        assertEquals(0xa5, 255 & dest[5]);

        length = reader.readUtf8(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hellå", reader.readUtf8String());

        reader.nextParse();
        assertEquals(RionFieldTypes.UTF_8, reader.fieldType);
        assertEquals("0123456789abcdef", reader.readUtf8String());
        length = reader.readUtf8(dest, 0, 16);
        assertEquals(16, length);
        assertEquals('0', dest[0]);
        assertEquals('1', dest[1]);
        assertEquals('2', dest[2]);
        assertEquals('3', dest[3]);
        assertEquals('4', dest[4]);
        assertEquals('5', dest[5]);
        assertEquals('6', dest[6]);
        assertEquals('7', dest[7]);
        assertEquals('8', dest[8]);
        assertEquals('9', dest[9]);
        assertEquals('a', dest[10]);
        assertEquals('b', dest[11]);
        assertEquals('c', dest[12]);
        assertEquals('d', dest[13]);
        assertEquals('e', dest[14]);
        assertEquals('f', dest[15]);

        reader.nextParse();
        length = reader.readUtf8(dest, 0, 3);
        assertEquals(0, length);
        assertNull  (reader.readUtf8String());
        assertTrue(reader.isNull());

        reader.nextParse();
        length = reader.readUtf8(dest, 0, 3);
        assertEquals(0, length);
        assertNull  (reader.readUtf8String());
        assertTrue(reader.isNull());

    }

    @Test
    public void testReadUtcCalendar() {
        byte[] source = new byte[10 * 1024];

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR , 2014);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        int index = 0;
        index += RionWriter.writeUtc(source, index, calendar, 9);
        index += RionWriter.writeUtc(source, index, null, 9);
        index += RionWriter.writeUtcNull(source, index);

        assertEquals(12, index);
        reader.setSource(source, 0, index);

        reader.parse();
        Calendar calendar2 = reader.readUtcCalendar();

        assertEquals(2014, calendar2.get(Calendar.YEAR)) ;
        assertEquals(11  , calendar2.get(Calendar.MONTH)) ;
        assertEquals(31  , calendar2.get(Calendar.DAY_OF_MONTH)) ;
        assertEquals(23  , calendar2.get(Calendar.HOUR_OF_DAY)) ;
        assertEquals(59  , calendar2.get(Calendar.MINUTE)) ;
        assertEquals(59  , calendar2.get(Calendar.SECOND)) ;
        assertEquals(999  , calendar2.get(Calendar.MILLISECOND)) ;

        assertEquals(TimeZone.getTimeZone("UTC")  , calendar2.getTimeZone()) ;

        reader.nextParse();
        assertTrue(reader.isNull());
        assertNull(reader.readUtcCalendar());

        reader.nextParse();
        assertTrue(reader.isNull());
        assertNull(reader.readUtcCalendar());

    }






    /*
    @Test
    public void testReadComplexTypeIdShort() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;

        byte[] bytes = new byte[]{1,2,4 };
        index += RrionWriter.writeComplexTypeIdShort(source, index, bytes);

        reader.setSource(source, 0, 4);
        reader.parse();

        assertEquals(RionFieldTypes.COMPLEX_TYPE_ID_SHORT, reader.fieldType);
        reader.readComplexTypeIdShort(dest);

        assertEquals(1, dest[0]);
        assertEquals(2, dest[1]);
        assertEquals(4, dest[2]);

        assertEquals(0, dest[3]);
        assertEquals(0, dest[4]);
        assertEquals(0, dest[5]);

        reader.readComplexTypeIdShort(dest, 3, 3);

        assertEquals(1, dest[3]);
        assertEquals(2, dest[4]);
        assertEquals(4, dest[5]);
    }
    */


    /*
    @Test
    public void testReadComplexTypeId() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;

        byte[] bytes = new byte[]{1,2,4 };
        index += RionWriter.writeComplexTypeId(source, index, bytes);

        reader.setSource(source, 0, 4);
        reader.next();
        reader.parse();

        assertEquals(RionFieldTypes.EXTENDED, reader.fieldType);
        assertEquals(RionFieldTypes.COMPLEX_TYPE_ID, reader.fieldTypeExtended);
        reader.readComplexTypeId(dest);

        assertEquals(1, dest[0]);
        assertEquals(2, dest[1]);
        assertEquals(4, dest[2]);

        assertEquals(0, dest[3]);
        assertEquals(0, dest[4]);
        assertEquals(0, dest[5]);

        reader.readComplexTypeId(dest, 3, 3);

        assertEquals(1, dest[3]);
        assertEquals(2, dest[4]);
        assertEquals(4, dest[5]);
    }

     */

    /*
    @Test
    public void testReadComplexTypeVersion() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;

        byte[] bytes = new byte[]{1,2,4 };
        index += RionWriter.writeComplexTypeVersion(source, index, bytes);

        reader.setSource(source, 0, 4);
        reader.next();
        reader.parse();

        assertEquals(RionFieldTypes.EXTENDED, reader.fieldType);
        assertEquals(RionFieldTypes.COMPLEX_TYPE_VERSION, reader.fieldTypeExtended);
        reader.readComplexTypeId(dest);

        assertEquals(1, dest[0]);
        assertEquals(2, dest[1]);
        assertEquals(4, dest[2]);

        assertEquals(0, dest[3]);
        assertEquals(0, dest[4]);
        assertEquals(0, dest[5]);

        reader.readComplexTypeId(dest, 3, 3);

        assertEquals(1, dest[3]);
        assertEquals(2, dest[4]);
        assertEquals(4, dest[5]);
    }


     */

    @Test
    public void testReadKey() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += RionWriter.writeKey(source, index, "Hello");
        index += RionWriter.writeKey(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.nextParse();

        assertEquals(RionFieldTypes.KEY, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readKey(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readKey(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readKeyAsUtf8String());

        reader.next();
        reader.parse();
        length = reader.readKey(dest, 0, 3);
        assertEquals(0, length);
        assertNull  (reader.readKeyAsUtf8String());

    }


    @Test
    public void readKeyAsUtf8String() {
        byte[] source = new byte[10 * 1024];
        int index = 0;

        index += RionWriter.writeKey(source, index, "Hellå");
        index += RionWriter.writeKey(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.nextParse();

        assertEquals(RionFieldTypes.KEY, reader.fieldType);
        assertEquals(6, reader.fieldLength);

        assertEquals("Hellå", reader.readKeyAsUtf8String());
    }



    @Test
    public void testReadKeyShort() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += RionWriter.writeKeyShort(source, index, "Hello");
        index += RionWriter.writeKeyShort(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(RionFieldTypes.KEY_SHORT, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readKeyShort(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readKeyShort(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readKeyShortAsUtf8String());

        reader.next();
        reader.parse();
        length = reader.readKeyShort(dest, 0, 3);
        assertEquals(0, length);
        assertNull(reader.readKeyShortAsUtf8String());

    }

    @Test
    public void testReadExtendedNormal() {
        byte[] source = new byte[10 * 1024];

        source[0] = (byte) (0xFF & 0xF1);  // 0xF8 = extended field type, field length length = 1 meaing 1 length byte;
        source[1] = (byte) (0xFF & 0xFF);  // set field type to 255 - to test it works with values above 127
        source[2] = (byte) 2; // length byte - length of field value is 2 bytes
        source[3] = (byte) 3; // value byte 1
        source[4] = (byte) 9; // value byte 2

        reader.setSource(source, 0, 5);
        reader.nextParse();

        assertEquals(15, reader.fieldType); // expect extended field type
        assertEquals(255, reader.fieldTypeExtended); // expect extended field type
        assertEquals(1, reader.fieldLengthLength);
        assertEquals(2, reader.fieldLength);
        assertEquals(3, reader.source[reader.index]);
        assertEquals(9, reader.source[reader.index + 1]);

        assertEquals(5, reader.nextIndex);

        reader.next();
        assertEquals(5, reader.index);



    }


    /*
    @Test
    public void testReadElementCount() {
        byte[] dest = new byte[10 * 1024];

        int index = 0;
        index += RrionWriter.writeElementCount(dest, index, 1024);
        index += RrionWriter.writeElementCount(dest, index, 2048);

        assertEquals(8, index);

        reader.setSource(dest, 0, dest.length);
        reader.next();
        reader.parse();

        assertEquals(RionFieldTypes.EXTENDED     , reader.fieldType);
        assertEquals(RionFieldTypes.ELEMENT_COUNT, reader.fieldTypeExtended);
        assertEquals(2, reader.fieldLengthLength);
        assertEquals(2, reader.fieldLength);

        int offset = 2;
        assertEquals(1024 >> 8 , 255 & dest[offset++]);
        assertEquals(1024 & 255, 255 & dest[offset++]);

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.EXTENDED     , reader.fieldType);
        assertEquals(RionFieldTypes.ELEMENT_COUNT, reader.fieldTypeExtended);
        assertEquals(2, reader.fieldLengthLength);
        assertEquals(2, reader.fieldLength);

        offset = 6;
        assertEquals(2048 >> 8  , 255 & dest[offset++]);
        assertEquals(2048 & 255, 255 & dest[offset++]);

    }
    */




    @Test
    public void testMoveIntoAndOutOf() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        int object1StartIndex = index;
        index += RionWriter.writeObjectBegin(source, index, 2);
        index += RionWriter.writeKey (source, index, "field1");
        index += RionWriter.writeUtf8(source, index, "value1");
        index += RionWriter.writeKey (source, index, "field2");
        index += RionWriter.writeInt64 (source, index, 1234);
        RionWriter.writeObjectEnd(source, object1StartIndex, 2, index - object1StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte

        int object2StartIndex = index;
        index += RionWriter.writeObjectBegin(source, index, 2);
        index += RionWriter.writeKey (source, index, "field1");
        index += RionWriter.writeUtf8(source, index, "value1");
        index += RionWriter.writeKey (source, index, "field2");
        index += RionWriter.writeInt64(source, index, 1234);
        RionWriter.writeObjectEnd(source, object2StartIndex, 2, index - object2StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte


        // First check that outer level navigation works - skipping over the fields of the objects.
        reader.setSource(source, 0, index);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.OBJECT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertFalse(reader.hasNext());
        assertEquals(RionFieldTypes.OBJECT, reader.fieldType);

        reader.next();
        assertFalse(reader.hasNext());
        assertEquals(index, reader.nextIndex);


        // Second check that parsing into the objects also works
        reader.setSource(source, 0, index);
        reader.next();
        reader.parse();

        assertEquals(RionFieldTypes.OBJECT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.moveInto();
        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.UTF_8_SHORT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.moveOutOf();
        assertEquals(RionFieldTypes.OBJECT, reader.fieldType);
        assertEquals(object1StartIndex + 1 + 2, reader.index); // +1 for lead byte, +2 for lengthLength
        assertEquals(object2StartIndex        , reader.nextIndex);
        assertTrue(reader.hasNext());

        reader.next();
        assertEquals(object2StartIndex        , reader.index);

        reader.parse();
        assertEquals(RionFieldTypes.OBJECT, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.moveInto();
        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.UTF_8_SHORT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(RionFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.moveOutOf();
        assertFalse(reader.hasNext());

    }


    @Test
    public void testReadObjects() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        int object1StartIndex = index;
        index += RionWriter.writeObjectBegin(source, index, 2);
        index += RionWriter.writeKey (source, index, "fieldName");
        index += RionWriter.writeUtf8(source, index, "John");
        index += RionWriter.writeKey (source, index, "id");
        index += RionWriter.writeInt64 (source, index, 1234);
        RionWriter.writeObjectEnd(source, object1StartIndex, 2, index - object1StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte

        reader.setSource(source, 0, index);

        Map object = null;
        while(reader.hasNext()){
            reader.next();
            reader.parse();

            if(reader.fieldType == RionFieldTypes.OBJECT){
                reader.moveInto();

                object = parseObject();

                reader.moveOutOf();
            }
        }

        assertNotNull(object);
        assertEquals(2, object.size());
        assertEquals("John", object.get("fieldName")) ;
        assertEquals(new Long(1234)  , object.get("id")) ;



        index = 0;
        object1StartIndex = index;
        index += RionWriter.writeObjectBegin(source, index, 2);
        RionWriter.writeObjectEnd(source, object1StartIndex, 2, index - object1StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte

        reader.setSource(source, 0, index);

        while(reader.hasNext()){
            reader.next();
            reader.parse();

            if(reader.fieldType == RionFieldTypes.OBJECT){
                reader.moveInto();

                object = parseObject();

                reader.moveOutOf();
            }
        }

        assertNotNull(object);
        assertEquals(0, object.size());

    }

    private Map parseObject() {
        Map object;
        object = new HashMap();
        while(reader.hasNext()){
            reader.next();
            reader.parse();

            String key = null;
            if(reader.fieldType == RionFieldTypes.KEY ||
               reader.fieldType == RionFieldTypes.KEY_SHORT){

                key = reader.readKeyAsUtf8String();
            }

            if(reader.hasNext()){
                reader.next();
                reader.parse();

                if("fieldName".equals(key)){
                    object.put(key, reader.readUtf8String());
                } else if("id".equals(key)){
                    object.put(key, reader.readInt64());
                }
            }
        }
        return object;
    }




}
