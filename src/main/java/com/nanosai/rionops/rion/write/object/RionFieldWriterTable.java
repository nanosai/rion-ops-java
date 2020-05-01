package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.RionUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterTable extends RionFieldWriterBase implements IRionFieldWriter {

    protected byte[] allKeyFieldBytes = null;
    protected IRionFieldWriter[] fieldWritersForArrayType = null;


    public RionFieldWriterTable(Field field, String alias) {
        super(field, alias);
    }

    public void generateFieldWriters(IRionObjectWriterConfigurator configurator, Map<Object, IRionFieldWriter> existingFieldWriters) {
        this.fieldWritersForArrayType = RionFieldWriterUtil.createFieldWriters(
                this.field.getType().getComponentType().getDeclaredFields(), configurator, existingFieldWriters);

        preGenerateAllKeyFields();
    }

    private void preGenerateAllKeyFields() {
        int totalKeyFieldsLength = 0;

        for(int i=0; i < this.fieldWritersForArrayType.length; i++){
            totalKeyFieldsLength += this.fieldWritersForArrayType[i].getKeyFieldLength();
        }

        allKeyFieldBytes = new byte[totalKeyFieldsLength];

        int offset = 0;
        for(int i=0; i < this.fieldWritersForArrayType.length; i++){
            offset += this.fieldWritersForArrayType[i].writeKeyField(allKeyFieldBytes, offset);
        }

    }




    @Override
    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        try {
            Object array = (Object) field.get(sourceObject);

            if(array == null) {
                destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.TABLE << 4) | 0)); //marks a null with 0 lengthLength
                return 1;
            }
            int startIndex = destinationOffset;
            destination[destinationOffset] = (byte) (255 & (RionFieldTypes.TABLE << 4) | (maxLengthLength));
            destinationOffset += 1 + maxLengthLength ; // 1 for lead byte + make space for maxLengthLength length bytes.

            //write element count
            int elementCount = Array.getLength(array);
            int elementCountLengthLength = RionUtil.byteLengthOfInt64Value(elementCount);

            destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.INT_POS << 4) | elementCountLengthLength) );
            for(int i=(elementCountLengthLength-1)*8; i >= 0; i-=8){
                destination[destinationOffset++] = (byte) (255 & (elementCount >> i));
            }


            //write key fields
            System.arraycopy(this.allKeyFieldBytes, 0, destination, destinationOffset, this.allKeyFieldBytes.length);
            destinationOffset += this.allKeyFieldBytes.length;


            //write array elements
            for(int i=0; i<elementCount; i++){
                Object source = Array.get(array, i);

                //for each field in source write its field value out.
                for(int j=0; j < this.fieldWritersForArrayType.length; j++){
                    destinationOffset += this.fieldWritersForArrayType[j].writeValueField(source, destination, destinationOffset, maxLengthLength);
                }
            }

            int valueLength = destinationOffset - startIndex;

            RionFieldWriterUtil.writeLength(valueLength - 1 - maxLengthLength, maxLengthLength, destination, startIndex + 1);

            return valueLength;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int writeValueFieldCyclic(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength, RionObjectWriter.CyclicObjectGraphWriteState state) {
        try {
            Object array = (Object) field.get(sourceObject);

            if(array == null) {
                destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.TABLE << 4) | 0)); //marks a null with 0 lengthLength
                return 1;
            }

            // Check if this array has already been written to the output before.
            // If no  (objectIndex == -1), write the full RION field out (implemented below already).
            // If yes (objectIndex >=0), write a RION Reference field (needs to be implemented).
            int objectIndex = state.getObjectIndex(array);
            if(objectIndex != -1){
                //write Reference field instead.
                int lengthOfObjectIndex = RionUtil.byteLengthOfInt64Value(objectIndex);
                destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.EXTENDED << 4) | lengthOfObjectIndex));
                destination[destinationOffset++] = (byte) (255 & RionFieldTypes.EXT_REFERENCE);

                switch(lengthOfObjectIndex){
                    case 4 : destination[destinationOffset++] = (byte) (255 & (objectIndex >> 24));
                    case 3 : destination[destinationOffset++] = (byte) (255 & (objectIndex >> 16));
                    case 2 : destination[destinationOffset++] = (byte) (255 & (objectIndex >>  8));
                    case 1 : destination[destinationOffset++] = (byte) (255 & (objectIndex));
                }

                return 2 + lengthOfObjectIndex;
            }
            state.addObjectWritten(array);

            int startIndex = destinationOffset;
            destination[destinationOffset] = (byte) (255 & (RionFieldTypes.TABLE << 4) | (maxLengthLength));
            destinationOffset += 1 + maxLengthLength ; // 1 for lead byte + make space for maxLengthLength length bytes.

            //write element count
            int elementCount = Array.getLength(array);
            int elementCountLengthLength = RionUtil.byteLengthOfInt64Value(elementCount);

            destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.INT_POS << 4) | elementCountLengthLength) );
            for(int i=(elementCountLengthLength-1)*8; i >= 0; i-=8){
                destination[destinationOffset++] = (byte) (255 & (elementCount >> i));
            }


            //write key fields
            System.arraycopy(this.allKeyFieldBytes, 0, destination, destinationOffset, this.allKeyFieldBytes.length);
            destinationOffset += this.allKeyFieldBytes.length;


            //write array elements
            for(int i=0; i<elementCount; i++){
                Object arrayElement = Array.get(array, i);

                //todo check if this source object has already been written to the graph before.
                int arrayElementIndex = state.getObjectIndex(arrayElement);
                if(arrayElementIndex == -1){
                    state.addObjectWritten(arrayElement);
                    //for each field in source write its field value out.
                    for(int j=0; j < this.fieldWritersForArrayType.length; j++){
                        destinationOffset += this.fieldWritersForArrayType[j].writeValueFieldCyclic(arrayElement, destination, destinationOffset, maxLengthLength,state);
                    }
                } else {
                    //todo write array as RION Ref
                    //write Reference field instead.
                    int lengthOfArrayElementIndex = RionUtil.byteLengthOfInt64Value(arrayElementIndex);
                    destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.EXTENDED << 4) | lengthOfArrayElementIndex));
                    destination[destinationOffset++] = (byte) (255 & RionFieldTypes.EXT_ROW_REFERENCE);

                    switch(lengthOfArrayElementIndex){
                        case 4 : destination[destinationOffset++] = (byte) (255 & (arrayElementIndex >> 24));
                        case 3 : destination[destinationOffset++] = (byte) (255 & (arrayElementIndex >> 16));
                        case 2 : destination[destinationOffset++] = (byte) (255 & (arrayElementIndex >>  8));
                        case 1 : destination[destinationOffset++] = (byte) (255 & (arrayElementIndex));
                    }
                    //return 2 + lengthOfObjectIndex;
                    //destinationOffset += 2 + lengthOfArrayElementIndex;
                }
            }

            int valueLength = destinationOffset - startIndex;

            RionFieldWriterUtil.writeLength(valueLength - 1 - maxLengthLength, maxLengthLength, destination, startIndex + 1);

            return valueLength;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0;

    }
}
