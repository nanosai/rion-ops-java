package com.nanosai.rionops.rion.read.object;

import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 05-11-2015.
 */
public class RionFieldReaderArrayShort implements IRionFieldReader {

    private Field field = null;

    public RionFieldReaderArrayShort(Field field) {
        this.field = field;
    }


    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int leadByte     = 255 & source[sourceOffset++];
        int lengthLength = leadByte & 15;

        if(lengthLength == 0){
            return 1; //string field (UTF-8) with null value is always 1 byte long
        }

        //int fieldType    = leadByte >> 4;   //todo use field type for validation?

        //read array field length (in bytes)
        int length = 255 & source[sourceOffset++];
        for(int i=1; i<lengthLength; i++){
            length <<= 8;
            length |= 255 & source[sourceOffset++];
        }


        //read array field element count
        int elementCountLeadByte = source[sourceOffset++];
        int elementCountLength   = elementCountLeadByte & 15;


        int elementCount = 0;
        for(int i=0; i<elementCountLength; i++){
            elementCount <<= 8;
            elementCount |= 255 & source[sourceOffset++];
        }


        //read array elements
        short[] values = new short[elementCount];

        for(int i=0; i<elementCount; i++){
            int elementLeadByte = 255 & source[sourceOffset++];
            int elementLength   = elementLeadByte & 15;
            int elementValue   = 0;
            for(int j=0; j<elementLength; j++){
                elementValue <<= 8;
                elementValue |= 255 & source[sourceOffset++];
            }
            if( (elementLeadByte >> 4) == RionFieldTypes.INT_POS){
                values[i] = (short) elementValue;
            } else {
                values[i] = (short) ((-elementValue)-1);
            }
        }


        try {
            field.set(destination, values);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 1 + lengthLength + length;

    }

    @Override
    public int readAcyclic(byte[] source, int sourceOffset, Object destination) {
        return read(source, sourceOffset, destination);
    }

    @Override
    public int readCyclic(byte[] source, int sourceOffset, Object destination, RionObjectReader.CyclicObjectGraphReadState readState) {
        return read(source, sourceOffset, destination);
    }

    @Override
    public void setNull(Object destination) {
        try {
            field.set(destination, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
