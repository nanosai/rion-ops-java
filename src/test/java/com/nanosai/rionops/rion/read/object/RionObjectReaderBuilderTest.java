package com.nanosai.rionops.rion.read.object;

import com.nanosai.rionops.rion.pojo.TestPojo;
import com.nanosai.rionops.rion.write.object.RionObjectWriter;
import com.nanosai.rionops.rion.write.object.RionObjectWriterBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class RionObjectReaderBuilderTest {


    @Test
    public void test() {
        RionObjectReaderBuilder builder = new RionObjectReaderBuilder();

        builder.setTypeClass(TestPojo.class);
        builder.addDeclaredFieldsForTypeClass();

        assertEquals(9, builder.fieldReaderMap.size());

        RionObjectReader reader = builder.build();
    }


    @Test
    public void combinedWriteReadTest() {
        RionObjectWriterBuilder writerBuilder = new RionObjectWriterBuilder();
        writerBuilder.addDeclaredFields(TestPojo.class);
        RionObjectWriter writer = writerBuilder.build();

        byte[] data = new byte[1024];
        TestPojo pojo = new TestPojo();
        pojo.field4 = "Hello world";

        int bytesWritten = writer.writeObject(pojo, 2, data, 0);


        RionObjectReaderBuilder readerBuilder = new RionObjectReaderBuilder();
        readerBuilder.setTypeClass(TestPojo.class);
        readerBuilder.addDeclaredFieldsForTypeClass();
        RionObjectReader reader = readerBuilder.build();

        TestPojo pojoRead = (TestPojo) reader.read(data, 0);

        assertNotSame(pojo, pojoRead);

        assertEquals("Hello world", pojoRead.field4);

    }
}
