package com.nanosai.rionops.rion.read.object;

/**
 * Created by jjenkov on 05-11-2015.
 */
public interface IRionFieldReader {

    public void setNull(Object destination);

    @Deprecated //use readAcyclic() instead
    public int read(byte[] source, int sourceOffset, Object destination);

    public int readAcyclic(byte[] source, int sourceOffset, Object destination);

    public int readCyclic(byte[] source, int sourceOffset, Object destination, RionObjectReader.CyclicObjectGraphReadState readState);





}
