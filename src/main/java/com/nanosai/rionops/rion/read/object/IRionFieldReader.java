package com.nanosai.rionops.rion.read.object;

/**
 * Created by jjenkov on 05-11-2015.
 */
public interface IRionFieldReader {

    public int read(byte[] source, int sourceOffset, Object destination);

    public void setNull(Object destination);


}
