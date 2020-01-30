package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.pojo.RecursiveArrayPojo;
import com.nanosai.rionops.rion.pojo.RecursivePojo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Created by jjenkov on 18/03/2017.
 */
public class RionObjectWriterRecursivePojoTests {


    @Test
    public void testWriteRecursivePojo() {
        RionObjectWriter rionObjectWriter = new RionObjectWriter(RecursivePojo.class);

        RionFieldWriterObject ionFieldWriterObject = (RionFieldWriterObject) rionObjectWriter.fieldWriters[1];

        assertEquals(RionFieldWriterString.class, rionObjectWriter.fieldWriters[0].getClass());
        assertEquals(RionFieldWriterObject.class, rionObjectWriter.fieldWriters[1].getClass());
        assertSame(rionObjectWriter.fieldWriters[1], ionFieldWriterObject.fieldWriters[1]);

        RecursivePojo root   = new RecursivePojo();
        RecursivePojo child1 = new RecursivePojo();
        RecursivePojo child2 = new RecursivePojo();

        root.setName("root");
        child1.setName("child1");
        child2.setName("child2");

        root.setChild1(child1);
        root.setChild2(child2);

        byte[] dest = new byte[1024];

        rionObjectWriter.writeObject(root, 2, dest, 0);

        int index = 0;
        assertEquals(RionFieldTypes.OBJECT << 4 | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(86, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 4, 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('m', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);

        assertEquals(RionFieldTypes.UTF_8_SHORT << 4 | 4, 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);


        assertEquals(RionFieldTypes.OBJECT << 4 | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(28, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 4, 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('m', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);

        assertEquals(RionFieldTypes.UTF_8_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals(RionFieldTypes.OBJECT << 4 | 0, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);

        assertEquals(RionFieldTypes.OBJECT << 4 | 0, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);

        assertEquals(RionFieldTypes.OBJECT << 4 | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(28, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 4, 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('m', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);

        assertEquals(RionFieldTypes.UTF_8_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals(RionFieldTypes.OBJECT << 4 | 0, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);

        assertEquals(RionFieldTypes.OBJECT << 4 | 0, 255 & dest[index++]);

    }


    @Test
    public void testWriteRecursiveArrayPojo() {
        RionObjectWriter rionObjectWriter = new RionObjectWriter(RecursiveArrayPojo.class);

        RionFieldWriterTable ionFieldWriterTable = (RionFieldWriterTable) rionObjectWriter.fieldWriters[1];

        assertEquals(RionFieldWriterString.class, rionObjectWriter.fieldWriters[0].getClass());
        assertEquals(RionFieldWriterTable.class, rionObjectWriter.fieldWriters[1].getClass());
        assertSame(rionObjectWriter.fieldWriters[1], ionFieldWriterTable.fieldWritersForArrayType[1]);

        RecursiveArrayPojo root   = new RecursiveArrayPojo();
        RecursiveArrayPojo child1 = new RecursiveArrayPojo();
        RecursiveArrayPojo child2 = new RecursiveArrayPojo();

        root.setChildren(new RecursiveArrayPojo[2]);
        root.getChildren()[0] = new RecursiveArrayPojo();
        root.getChildren()[1] = new RecursiveArrayPojo();

        root.setName("root");
        root.getChildren()[0].setName("child1");
        root.getChildren()[1].setName("child2");

        byte[] dest = new byte[1024];

        rionObjectWriter.writeObject(root, 2, dest, 0);

        int index = 0;
        assertEquals(RionFieldTypes.OBJECT << 4 | 2, 255 & dest[index++]);
        assertEquals(0, 255 & dest[index++]);
        assertEquals(54, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 4, 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('m', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);

        assertEquals(RionFieldTypes.UTF_8_SHORT << 4 | 4, 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('o', 255 & dest[index++]);
        assertEquals('t', 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 8, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);

        assertEquals(RionFieldTypes.TABLE << 4 | 2, 255 & dest[index++]);
        assertEquals( 0, 255 & dest[index++]);
        assertEquals(32, 255 & dest[index++]);

        assertEquals(RionFieldTypes.INT_POS << 4 | 1, 255 & dest[index++]);
        assertEquals( 2, 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 4, 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);
        assertEquals('a', 255 & dest[index++]);
        assertEquals('m', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);

        assertEquals(RionFieldTypes.KEY_SHORT << 4 | 8, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('r', 255 & dest[index++]);
        assertEquals('e', 255 & dest[index++]);
        assertEquals('n', 255 & dest[index++]);

        assertEquals(RionFieldTypes.UTF_8_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('1', 255 & dest[index++]);

        assertEquals(RionFieldTypes.TABLE << 4 | 0, 255 & dest[index++]);

        assertEquals(RionFieldTypes.UTF_8_SHORT << 4 | 6, 255 & dest[index++]);
        assertEquals('c', 255 & dest[index++]);
        assertEquals('h', 255 & dest[index++]);
        assertEquals('i', 255 & dest[index++]);
        assertEquals('l', 255 & dest[index++]);
        assertEquals('d', 255 & dest[index++]);
        assertEquals('2', 255 & dest[index++]);

        assertEquals(RionFieldTypes.TABLE << 4 | 0, 255 & dest[index++]);





    }


}
