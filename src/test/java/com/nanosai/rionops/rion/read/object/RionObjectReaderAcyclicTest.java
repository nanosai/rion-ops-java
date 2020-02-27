package com.nanosai.rionops.rion.read.object;

import com.nanosai.rionops.rion.pojo.PojoCyclic;
import com.nanosai.rionops.rion.write.object.RionObjectWriter;
import com.nanosai.rionops.rion.write.object.RionObjectWriterBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RionObjectReaderAcyclicTest {

    @Test
    public void test() {
        byte[] dest = new byte[1024];

        RionObjectWriterBuilder writerBuilder = new RionObjectWriterBuilder();
        writerBuilder.addDeclaredFields(PojoCyclic.class);

        RionObjectWriter writer = writerBuilder.build();

        PojoCyclic pojoParent = new PojoCyclic();
        PojoCyclic pojoChild  = new PojoCyclic();

        pojoParent.child = pojoChild;
        pojoChild.parent = pojoParent;

        int bytesWritten = writer.writeCyclic(pojoParent, 2, dest, 0);

        RionObjectReaderBuilder readerBuilder = new RionObjectReaderBuilder();
        RionObjectReader reader = readerBuilder.setTypeClass(PojoCyclic.class).addDeclaredFieldsForTypeClass().build();

        PojoCyclic pojoCyclicOut = (PojoCyclic) reader.readCyclic(dest, 0);

        assertNotNull(pojoCyclicOut);
        assertNotSame(pojoCyclicOut, pojoParent);
        assertNotSame(pojoCyclicOut, pojoChild);

        assertNull(pojoCyclicOut.parent);
        assertNotNull((pojoCyclicOut.child));

        assertNull((pojoCyclicOut.child.child));
        assertNotNull((pojoCyclicOut.child.parent));

        assertNotSame(pojoCyclicOut, pojoCyclicOut.child);
        assertSame   (pojoCyclicOut, pojoCyclicOut.child.parent);
    }
}
