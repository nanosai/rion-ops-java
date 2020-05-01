package com.nanosai.rionops.rion.hex;

import com.nanosai.rionops.rion.read.RionReader;
import com.nanosai.rionops.rion.write.RionWriter;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RionToHexConverterTest {



    @Test
    public void testConvertBytes() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        writer.writeBytes(new byte[]{1,2,3});

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        assertEquals("01 03 010203", dest.toString());
    }

    @Test
    public void testConvertBoolean() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        writer.writeBoolean(true);

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        assertEquals("11", dest.toString());
    }

    @Test
    public void testConvertIntPos() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        writer.writeInt64(0x123ABC);

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        assertEquals("23 123ABC", dest.toString());

        writer.setDestination(source, 0);
        writer.writeInt64(0x123ABC);
        writer.writeInt64(0x456DEF);
        writer.writeInt64(0x789ACE);

        dest.delete(0, dest.length());
        rionReader.setSource(source, 0, writer.index);

        converter.convertFormatted(rionReader, dest);

        String expected =
                  "23 123ABC\r\n"
                + "23 456DEF\r\n"
                + "23 789ACE"
                ;
        assertEquals(expected, dest.toString());
    }

    @Test
    public void testConvertIntNeg() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        writer.writeInt64(-0x123ABC);

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        assertEquals("33 123ABB", dest.toString());

        writer.setDestination(source, 0);
        writer.writeInt64(-0x123ABC);
        writer.writeInt64(-0x456DEF);
        writer.writeInt64(-0x789ACE);

        dest.delete(0, dest.length());
        rionReader.setSource(source, 0, writer.index);

        converter.convertFormatted(rionReader, dest);

        String expected =
                  "33 123ABB\r\n"
                + "33 456DEE\r\n"
                + "33 789ACD"
                ;
        assertEquals(expected, dest.toString());
    }


    @Test
    public void testConvertFloat() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        writer.writeFloat32(123.456F);
        writer.writeFloat64(123.456D);

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        String expected =
                "44 42F6E979\r\n" +
                "48 405EDD2F1A9FBE77";

        assertEquals(expected, dest.toString());
    }


    @Test
    public void testConvertUtf8() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        writer.writeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        assertEquals("51 1A 4142434445464748494A4B4C4D4E4F505152535455565758595A", dest.toString());
    }


    @Test
    public void testConvertUtf8Short() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        writer.writeUtf8("ABC");

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        assertEquals("63 414243", dest.toString());
    }

    @Test
    public void testConvertUtc() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 48);
        calendar.set(Calendar.SECOND, 32);
        calendar.set(Calendar.MILLISECOND, 96);

        writer.writeUtc(calendar, 9);

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        assertEquals("79 07E401011730200060", dest.toString());
    }


    @Test
    public void testConvertObject() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source).setNestedFieldStack(new int[16]);

        writer.writeObjectBeginPush(1);
        writer.writeInt64(0x01);
        writer.writeInt64(0x010203);
        writer.writeInt64(0xABCDEF);

        writer.writeObjectBeginPush(1);
        writer.writeUtf8("XYZ");
        writer.writeObjectEndPop();

        writer.writeObjectEndPop();

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        String expected =
                 "C1 10\r\n"
                +"    21 01\r\n"
                +"    23 010203\r\n"
                +"    23 ABCDEF\r\n"
                +"    C1 04\r\n"
                +"        63 58595A"
                ;

        assertEquals(expected, dest.toString());
    }

    @Test
    public void testConvertArray() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source).setNestedFieldStack(new int[16]);

        writer.writeArrayBeginPush(1);
        writer.writeInt64(0x01);
        writer.writeInt64(0x010203);
        writer.writeInt64(0xABCDEF);

        writer.writeObjectBeginPush(1);
        writer.writeUtf8("XYZ");
        writer.writeObjectEndPop();

        writer.writeArrayEndPop(4);

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        String expected =
                 "A1 12\r\n"
                +"    21 04\r\n"
                +"\r\n"
                +"    21 01\r\n"
                +"    23 010203\r\n"
                +"    23 ABCDEF\r\n"
                +"    C1 04\r\n"
                +"        63 58595A"
                ;

        assertEquals(expected, dest.toString());
    }

    @Test
    public void testConvertTable() {
        byte[] source = new byte[1024];

        RionWriter writer = new RionWriter(source).setNestedFieldStack(new int[16]);

        writer.writeTableBeginPush(1);
        writer.writeKey("ABC");
        writer.writeKey("DEF");
        writer.writeKey("GHI");

        writer.writeInt64(0x01);
        writer.writeInt64(0x010203);
        writer.writeInt64(0xABCDEF);

        writer.writeInt64(0x02);
        writer.writeInt64(0x040506);

        writer.writeObjectBeginPush(1);
        writer.writeUtf8("XYZ");
        writer.writeObjectEndPop();

        writer.writeInt64(0x03);
        writer.writeInt64(0x070809);
        writer.writeInt64(0xABCDEF);

        writer.writeTableEndPop(3);

        StringBuilder dest = new StringBuilder();
        RionToHexConverter converter = new RionToHexConverter();

        RionReader rionReader = new RionReader().setSource(source, 0, writer.index);
        converter.convertFormatted(rionReader, dest);

        String expected =
                 "B1 31\r\n"
                +"    21 03\r\n"
                +"    D1 03 414243\r\n"
                +"    D1 03 444546\r\n"
                +"    D1 03 474849\r\n"
                +"    21 01\r\n"
                +"    23 010203\r\n"
                +"    23 ABCDEF\r\n"
                +"\r\n"
                +"    21 02\r\n"
                +"    23 040506\r\n"
                +"    C1 04\r\n"
                +"        63 58595A\r\n"
                +"\r\n"
                +"    21 03\r\n"
                +"    23 070809\r\n"
                +"    23 ABCDEF"
                ;

        String actual = dest.toString();
        assertEquals(expected, dest.toString());
    }




}
