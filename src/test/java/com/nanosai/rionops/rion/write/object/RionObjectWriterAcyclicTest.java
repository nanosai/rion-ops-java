package com.nanosai.rionops.rion.write.object;

import com.nanosai.rionops.rion.pojo.PojoCyclic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RionObjectWriterAcyclicTest {

    @Test
    public void test() {
        byte[] dest = new byte[1024];

        RionObjectWriterBuilder builder = new RionObjectWriterBuilder();
        builder.addDeclaredFields(PojoCyclic.class);

        RionObjectWriter writer = builder.build();

        PojoCyclic pojoParent = new PojoCyclic();
        PojoCyclic pojoChild  = new PojoCyclic();

        pojoParent.child = pojoChild;
        pojoChild.parent = pojoParent;

        int bytesWritten = writer.writeCyclic(pojoParent, 2, dest, 0);

        int index = 0;
        assertEquals(37, bytesWritten);

        assertEquals(0xC2, 255 & dest[index++]);
        assertEquals( 0, 255 & dest[index++]);
        assertEquals(34, 255 & dest[index++]);

        assertEquals(0xE6, 255 & dest[index++]);
        assertEquals('p', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);

        assertEquals(0xC0, 255 & dest[index++]);

        assertEquals(0xE5, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);

        assertEquals(0xC2, 255 & dest[index++]);
        assertEquals( 0, 255 & dest[index++]);
        assertEquals(17, 255 & dest[index++]);

        assertEquals(0xE6, 255 & dest[index++]);
        assertEquals('p', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);

        assertEquals(0xF1, 255 & dest[index++]);  //Extended field type - length of value = 1
        assertEquals(0x0 , 255 & dest[index++]);  //Reference type (extended type)
        assertEquals(0, 255 & dest[index++]);     //Referencing RION field with index 0

        assertEquals(0xE5, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);

        assertEquals(0xC0, 255 & dest[index++]);

        System.out.println("bytesWritten = " + bytesWritten);



    }
}
