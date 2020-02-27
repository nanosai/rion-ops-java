package com.nanosai.rionops.rion.write.object;

/**
 * Created by jjenkov on 04-11-2015.
 */
public interface IRionFieldWriter {

    public int getKeyFieldLength();
    public int writeKeyField(byte[] destination, int destinationOffset);
    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength);

    public int writeValueFieldAcyclic(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength);
    public int writeValueFieldCyclic(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength, RionObjectWriter.CyclicObjectGraphWriteState state);

    

}
