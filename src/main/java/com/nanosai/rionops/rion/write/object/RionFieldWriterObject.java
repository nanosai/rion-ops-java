package com.nanosai.rionops.rion.write.object;


import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.RionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class RionFieldWriterObject extends RionFieldWriterBase implements IRionFieldWriter {

    public Field[] fields    = null;

    public IRionFieldWriter[] fieldWriters = null;

    public RionFieldWriterObject(Field field, String alias) {
        super(field, alias);
    }

    public void generateFieldWriters(IRionObjectWriterConfigurator configurator, Map<Object, IRionFieldWriter> existingFieldWriters) {
        //generate field writers for this RionFieldWriterObject instance - fields in the class of this field.
        this.fieldWriters = RionFieldWriterUtil.createFieldWriters(field.getType().getDeclaredFields(), configurator, existingFieldWriters);
    }



    @Override
    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        try {
            Object fieldValue = this.field.get(sourceObject);
            if(fieldValue == null){
                destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.OBJECT << 4) | 0)); //marks a null with 0 lengthLength
                return 1;
            }

            destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.OBJECT << 4) | maxLengthLength));

            int lengthOffset   = destinationOffset; //store length start offset for later use
            destinationOffset += maxLengthLength;


            for(int i=0; i<fieldWriters.length; i++){
                if(fieldWriters[i] != null){
                    destinationOffset += fieldWriters[i].writeKeyField(destination, destinationOffset);
                    destinationOffset += fieldWriters[i].writeValueField(fieldValue, destination, destinationOffset, maxLengthLength);
                }
            }

            int fullFieldLength   = destinationOffset - (lengthOffset + maxLengthLength);

            switch(maxLengthLength){
                case 4 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 24));
                case 3 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 16));
                case 2 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >>  8));
                case 1 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength));
            }

            return 1 + maxLengthLength + fullFieldLength;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            //todo should never happen, as we set all Field instances to accessible.
        }

        return 0;
    }

    @Override
    public int writeValueFieldCyclic(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength, RionObjectWriter.CyclicObjectGraphWriteState state) {
        try {
            Object fieldValue = this.field.get(sourceObject);
            if(fieldValue == null){
                destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.OBJECT << 4) | 0)); //marks a null with 0 lengthLength
                return 1;
            }

            // Check if this object has already been written to the output before.
            // If no  (objectIndex == -1), write the full RION field out (implemented below already).
            // If yes (objectIndex >=0), write a RION Reference field (needs to be implemented).
            int objectIndex = state.getObjectIndex(fieldValue);
            if(objectIndex == -1){
                //write full RION Object field
                state.addObjectWritten(fieldValue);

                destination[destinationOffset++] = (byte) (255 & ((RionFieldTypes.OBJECT << 4) | maxLengthLength));

                int lengthOffset   = destinationOffset; //store length start offset for later use
                destinationOffset += maxLengthLength;

                for(int i=0; i<fieldWriters.length; i++){
                    if(fieldWriters[i] != null){
                        destinationOffset += fieldWriters[i].writeKeyField(destination, destinationOffset);

                        //Difference to writeValueFieldAcyclic.
                        destinationOffset += fieldWriters[i].writeValueFieldCyclic(fieldValue, destination, destinationOffset, maxLengthLength, state);
                    }
                }

                int fullFieldLength   = destinationOffset - (lengthOffset + maxLengthLength);

                switch(maxLengthLength){
                    case 4 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 24));
                    case 3 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >> 16));
                    case 2 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength >>  8));
                    case 1 : destination[lengthOffset++] = (byte) (255 & (fullFieldLength));
                }
                return 1 + maxLengthLength + fullFieldLength;

            } else {
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            //todo should never happen, as we set all Field instances to accessible.
        }

        return 0;
    }
}
