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
}
