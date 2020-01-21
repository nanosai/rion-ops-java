package com.nanosai.rionops.rion.write.object;

import com.nanosai.rionops.rion.pojo.TestPojo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RionObjectWriterBuilderTest {


    @Test
    public void testBuildWithReflectionFields() {
        RionObjectWriterBuilder builder = new RionObjectWriterBuilder();
        builder.addFields(TestPojo.class);

        assertEquals(7, builder.fieldWriters.size());

        RionObjectWriter writer = builder.build();

        assertNotNull(writer.fieldWriters);
        assertEquals(7, writer.fieldWriters.length);

        assertEquals(RionFieldWriterBoolean.class  , writer.fieldWriters[0].getClass());
        assertEquals(RionFieldWriterLong.class     , writer.fieldWriters[1].getClass());
        assertEquals(RionFieldWriterFloat.class    , writer.fieldWriters[2].getClass());
        assertEquals(RionFieldWriterDouble.class   , writer.fieldWriters[3].getClass());
        assertEquals(RionFieldWriterString.class   , writer.fieldWriters[4].getClass());
        assertEquals(RionFieldWriterString.class   , writer.fieldWriters[5].getClass());
        assertEquals(RionFieldWriterCalendar.class , writer.fieldWriters[6].getClass());
    }


    @Test
    public void testBuildEmpty() {
        RionObjectWriterBuilder builder = new RionObjectWriterBuilder();
        RionObjectWriter writer = builder.build();

        assertNotNull(writer.fieldWriters);
        assertEquals(0, writer.fieldWriters.length);
    }


}
