package com.nanosai.rionops.rion.write.object;

import com.nanosai.rionops.rion.pojo.PojoArrayCyclic;
import com.nanosai.rionops.rion.pojo.PojoCyclic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RionObjectWriterCyclicTest {




    @Test
    public void testWriteCyclicObjectGraphWithArray() {
        byte[] dest = new byte[1024];

        RionObjectWriterBuilder builder = new RionObjectWriterBuilder();
        builder.addDeclaredFields(PojoArrayCyclic.class);

        RionObjectWriter writer = builder.build();

        PojoArrayCyclic root = new PojoArrayCyclic();
        PojoArrayCyclic child1 = new PojoArrayCyclic();
        PojoArrayCyclic child2 = new PojoArrayCyclic();
        PojoArrayCyclic child3 = new PojoArrayCyclic();

        PojoArrayCyclic[] rootChildList = new PojoArrayCyclic[] {child1, child2, child1, child2, root};
        PojoArrayCyclic[] child1_2_list = new PojoArrayCyclic[] {child3};

        root.setChildren(rootChildList);
        child1.setChildren(child1_2_list);
        child2.setChildren(child1_2_list);

        int bytesWritten = writer.writeCyclic(root, 2, dest, 0);
        assertEquals(53, bytesWritten);

        int index = 0;
        assertEquals(0xC2, 0xFF & dest[index++]); // root PojoArrayCyclic
        assertEquals(0x00, 0xFF & dest[index++]);
        assertEquals(0x32, 0xFF & dest[index++]); // length 50 (hex: 0x32)

        assertEquals(0xE8, 0xFF & dest[index++]);
        assertEquals('c', 0xFF & dest[index++]);
        assertEquals('h', 0xFF & dest[index++]);
        assertEquals('i', 0xFF & dest[index++]);
        assertEquals('l', 0xFF & dest[index++]);
        assertEquals('d', 0xFF & dest[index++]);
        assertEquals('r', 0xFF & dest[index++]);
        assertEquals('e', 0xFF & dest[index++]);
        assertEquals('n', 0xFF & dest[index++]);

        assertEquals( 0xB2, 0xFF & dest[index++]);  // child1 PojoArrayCyclic (as a Table - not an Object)
        assertEquals( 0x00, 0xFF & dest[index++]);
        assertEquals( 0x26, 0xFF & dest[index++]);  // length 38 (hex: 0x26)

        assertEquals( 0x21, 0xFF & dest[index++]);
        assertEquals( 0x05, 0xFF & dest[index++]);

        assertEquals(0xE8, 0xFF & dest[index++]);
        assertEquals('c', 0xFF & dest[index++]);
        assertEquals('h', 0xFF & dest[index++]);
        assertEquals('i', 0xFF & dest[index++]);
        assertEquals('l', 0xFF & dest[index++]);
        assertEquals('d', 0xFF & dest[index++]);
        assertEquals('r', 0xFF & dest[index++]);
        assertEquals('e', 0xFF & dest[index++]);
        assertEquals('n', 0xFF & dest[index++]);

        assertEquals( 0xB2, 0xFF & dest[index++]);  // child3 PojoArrayCyclic (as a Table - not an Object)
        assertEquals( 0x00, 0xFF & dest[index++]);
        assertEquals( 0x0C, 0xFF & dest[index++]);  //length 12 (hex: 0x34)

        assertEquals( 0x21, 0xFF & dest[index++]);
        assertEquals( 0x01, 0xFF & dest[index++]);

        assertEquals(0xE8, 0xFF & dest[index++]);
        assertEquals('c', 0xFF & dest[index++]);
        assertEquals('h', 0xFF & dest[index++]);
        assertEquals('i', 0xFF & dest[index++]);
        assertEquals('l', 0xFF & dest[index++]);
        assertEquals('d', 0xFF & dest[index++]);
        assertEquals('r', 0xFF & dest[index++]);
        assertEquals('e', 0xFF & dest[index++]);
        assertEquals('n', 0xFF & dest[index++]);

        assertEquals(0xB0, 0xFF & dest[index++]);  //null Table in child3 children (todo error?)

        assertEquals(0xF1, 255 & dest[index++]);  //Extended field type - length of value = 1
        assertEquals(0x00, 255 & dest[index++]);  //Row Reference type (extended type)
        assertEquals(   3, 255 & dest[index++]);  //Referencing RION field with index 2 (child1 array / child2 array)

        int fieldType    = (0xF0 & dest[index]) >> 4;
        int lengthLength = 0x0F & dest[index];

        assertEquals(0xF1, 255 & dest[index++]);  //Extended field type - length of value = 1
        assertEquals(0x01, 255 & dest[index++]);  //Row Reference type (extended type)
        assertEquals(   2, 255 & dest[index++]);  //Referencing RION field with index 2 (child3)

        //int fieldType    = (0xF0 & dest[index]) >> 4;
        //int lengthLength = 0x0F & dest[index];

        assertEquals(0xF1, 255 & dest[index++]);  //Extended field type - length of value = 1
        assertEquals(0x01, 255 & dest[index++]);  //Row Reference type (extended type)
        assertEquals(   5, 255 & dest[index++]);  //Referencing RION field with index 0

        assertEquals(0xF1, 255 & dest[index++]);  //Extended field type - length of value = 1
        assertEquals(0x01, 255 & dest[index++]);  //Row Reference type (extended type)
        assertEquals(   0, 255 & dest[index++]);  //Referencing RION field with index 0

    }


    @Test
    public void testWriteCyclicObjectGraph() {
        byte[] dest = new byte[1024];

        RionObjectWriterBuilder builder = new RionObjectWriterBuilder();
        builder.addDeclaredFields(PojoCyclic.class);

        RionObjectWriter writer = builder.build();

        PojoCyclic pojoParent = new PojoCyclic();
        PojoCyclic pojoChild  = new PojoCyclic();

        pojoParent.child = pojoChild;
        pojoChild.parent = pojoParent;

        int bytesWritten = writer.writeCyclic(pojoParent, 2, dest, 0);
        assertEquals(37, bytesWritten);

        int index = 0;

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
