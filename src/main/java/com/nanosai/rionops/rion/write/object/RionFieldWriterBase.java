package com.nanosai.rionops.rion.write.object;


import java.lang.reflect.Field;

/**
 * This class is a base class for IRionFieldWriter implementations.
 *
 */
public abstract class RionFieldWriterBase implements IRionFieldWriter {

    protected Field  field    = null;
    protected byte[] keyField = null;

    public RionFieldWriterBase(Field field, String alias) {
        this.field = field;
        this.keyField = RionFieldWriterUtil.preGenerateKeyField(alias);
    }

    @Override
    public int getKeyFieldLength() {
        return this.keyField.length;
    }

    @Override
    public int writeKeyField(byte[] destination, int destinationOffset) {
        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        return this.keyField.length;
    }

    @Override
    public abstract int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength);

    @Override
    public int writeValueFieldAcyclic(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {
        //todo make writeValueField call this method instead - when preparing to deprecate writeValueField()
        return writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);
    }

    @Override
    public int writeValueFieldCyclic(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength, RionObjectWriter.CyclicObjectGraphWriteState state) {
        return writeValueFieldAcyclic(sourceObject, destination, destinationOffset, maxLengthLength);
    }


}
