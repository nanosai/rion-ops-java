package com.nanosai.rionops.rion.read.object;

import com.nanosai.rionops.rion.pojo.RecursiveArrayPojo;
import com.nanosai.rionops.rion.pojo.RecursivePojo;
import com.nanosai.rionops.rion.write.object.RionObjectWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by jjenkov on 25/03/2017.
 */
public class RionObjectReaderRecursivePojoTest {

    @Test
    public void testRecursiveObjectReading() {
        byte[] data = new byte[1024];
        RionObjectWriter ionObjectWriter = new RionObjectWriter(RecursivePojo.class);
        RionObjectReader rionObjectReader = new RionObjectReader(RecursivePojo.class);

        RecursivePojo pojoSource = new RecursivePojo();
        pojoSource.setName("root");

        RecursivePojo pojoSourceChild1 = new RecursivePojo();
        pojoSourceChild1.setName("child1");

        RecursivePojo pojoSourceChild2 = new RecursivePojo();
        pojoSourceChild2.setName("child2");

        pojoSource.setChild1(pojoSourceChild1);
        pojoSource.setChild2(pojoSourceChild2);

        ionObjectWriter.writeObject(pojoSource, 2, data, 0);

        RecursivePojo pojoDest = (RecursivePojo) rionObjectReader.read(data, 0);
        assertNotNull(pojoDest);
        assertEquals("root", pojoDest.getName());

        assertNotNull(pojoDest.getChild1());
        assertEquals("child1", pojoDest.getChild1().getName());
        assertNull(pojoDest.getChild1().getChild1());
        assertNull(pojoDest.getChild1().getChild2());

        assertNotNull(pojoDest.getChild2());
        assertEquals("child2", pojoDest.getChild2().getName());
        assertNull(pojoDest.getChild2().getChild1());
        assertNull(pojoDest.getChild2().getChild2());
    }


    @Test
    public void testRecursiveObjectTableReading() {
        byte[] data = new byte[1024];
        RionObjectWriter ionObjectWriter = new RionObjectWriter(RecursiveArrayPojo.class);
        RionObjectReader rionObjectReader = new RionObjectReader(RecursiveArrayPojo.class);

        RecursiveArrayPojo pojoSource = new RecursiveArrayPojo();
        pojoSource.setName("root");

        RecursiveArrayPojo child1 = new RecursiveArrayPojo();
        child1.setName("child1");

        RecursiveArrayPojo child2 = new RecursiveArrayPojo();
        child2.setName("child2");

        pojoSource.setChildren(new RecursiveArrayPojo[]{child1, child2});

        ionObjectWriter.writeObject(pojoSource, 2, data, 0);


        RecursiveArrayPojo pojoDest = (RecursiveArrayPojo) rionObjectReader.read(data, 0);

        assertNotNull(pojoDest);
        assertEquals("root", pojoDest.getName());

        assertNotNull(pojoDest.getChildren());
        assertEquals(2, pojoDest.getChildren().length);

        assertNotNull(pojoDest.getChildren()[0]);
        assertEquals("child1", pojoDest.getChildren()[0].getName());
        assertNull(pojoDest.getChildren()[0].getChildren());

        assertNotNull(pojoDest.getChildren()[1]);
        assertEquals("child2", pojoDest.getChildren()[1].getName());
        assertNull(pojoDest.getChildren()[1].getChildren());


    }
}
