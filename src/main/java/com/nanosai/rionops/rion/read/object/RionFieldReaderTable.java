package com.nanosai.rionops.rion.read.object;


import com.nanosai.rionops.rion.RionFieldTypes;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jjenkov on 11-11-2015.
 */
public class RionFieldReaderTable implements IRionFieldReader {

    private Field field       = null;
    private Class typeInTable = null;
    private Field[] typeInTableFields = null;
    private Map<RionKeyFieldKey, IRionFieldReader> fieldReaderMap = null;
    private RionFieldReaderNop nopFieldReader = new RionFieldReaderNop();

    private List tempList = new ArrayList();


    //This array is first used when reading field values from a table. First the key fields are used to obtain
    //IIapFieldReader instances from the fieldReaderMap. The IIapFieldReader instances are inserted into this
    //array according to the order their corresponding key fields appear in the beginning of the table.
    //Afterwards the value fields in the table are read using the IIapFieldReader instances in this array,
    //cycling through the fieldReaderArray until there are no more value fields in the table.
    private IRionFieldReader[] fieldReaderArray = null;

    private RionKeyFieldKey tempKeyFieldKey = new RionKeyFieldKey();

    public RionFieldReaderTable(Field field, IRionObjectReaderConfigurator configurator) {
        this.field             = field;
        this.typeInTable       = field.getType().getComponentType();  // field type is an array, so we need to get to the component type of the array.
        this.typeInTableFields = typeInTable.getDeclaredFields();
        this.fieldReaderArray  = new IRionFieldReader[typeInTableFields.length];
    }

    public void generateFieldReaders(IRionObjectReaderConfigurator configurator, Map<Field, IRionFieldReader> existingFieldReaders) {
        this.fieldReaderMap = RionFieldReaderUtil.createFieldReaders(this.typeInTableFields, configurator, existingFieldReaders);
    }


    @Override
    public int read(byte[] source, int sourceOffset, Object destination) {
        int tableStartIndex = sourceOffset;

        int tableLeadByte = 255 & source[sourceOffset++];
        int tableLengthLength  = tableLeadByte & 15;

        if(tableLengthLength == 0){
            return 1; //table field with null as values is always 1 byte long (has 0 keys and 0 values).
        }

        int tableLength = 255 & source[sourceOffset++];
        for(int i=1; i < tableLengthLength; i++){
            tableLength <<= 8;
            tableLength |= 255 & source[sourceOffset++];
        }

        int tableEndIndex = tableStartIndex + 1 + tableLengthLength + tableLength;

        //read table field element count (row count)
        int elementCountLeadByte = source[sourceOffset++];
        int elementCountLength   = elementCountLeadByte & 15;

        int elementCount = 0;
        for(int i=0; i<elementCountLength; i++){
            elementCount <<= 8;
            elementCount |= 255 & source[sourceOffset++];
        }


        //read the key fields of the table
        tempKeyFieldKey.setSource(source);
        int fieldReadersInArray = 0;

        IRionFieldReader tempFieldReader = null;
        boolean endOfKeyFieldsFound = false;
        while(!endOfKeyFieldsFound){
            int fieldLeadByte = 255 & source[sourceOffset++];
            int fieldType     = fieldLeadByte >> 4;

            switch(fieldType){
                case RionFieldTypes.KEY_SHORT:  {
                    int keyLength = fieldLeadByte & 15;
                    tempKeyFieldKey.setOffsets(sourceOffset, keyLength);
                    tempFieldReader = this.fieldReaderMap.get(tempKeyFieldKey);
                    if(tempFieldReader == null){
                        tempFieldReader = this.nopFieldReader;
                    }
                    this.fieldReaderArray[fieldReadersInArray++] = tempFieldReader;
                    sourceOffset += keyLength;
                    break;
                }
                case RionFieldTypes.KEY : {
                    int keyLengthLength = fieldLeadByte & 15;
                    int keyLength = 0;
                    for(int i=0; i < keyLengthLength; i++){
                        keyLength <<= 8;
                        keyLength |= 255 & source[sourceOffset++];
                    }
                    tempKeyFieldKey.setOffsets(sourceOffset, keyLength);
                    tempFieldReader = this.fieldReaderMap.get(tempKeyFieldKey);
                    if(tempFieldReader == null){
                        tempFieldReader = this.nopFieldReader;
                    }
                    this.fieldReaderArray[fieldReadersInArray++] = tempFieldReader;
                    sourceOffset += keyLength;
                    break;
                }

                default : {
                    endOfKeyFieldsFound = true;
                }
            }
        }

        //start reading the value fields.
        sourceOffset--; //will have skipped over the lead byte of first value field during search for key fields.

        Object arrayInstance = Array.newInstance(this.typeInTable, elementCount);
        int fieldReaderIndex = 0;
        Object objectInTable = null;
        int arrayIndex = 0;
        while(sourceOffset < tableEndIndex){
            if(fieldReaderIndex == 0) {
                try {
                    objectInTable = this.typeInTable.newInstance();
                    Array.set(arrayInstance, arrayIndex++, objectInTable);
                    //this.tempList.add(objectInTable);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            sourceOffset += this.fieldReaderArray[fieldReaderIndex++]
                                .read(source, sourceOffset, objectInTable);

            if(fieldReaderIndex == fieldReadersInArray){
                fieldReaderIndex = 0; // cycle back to first field reader.
            }
        }

        try {
            this.field.set(destination, arrayInstance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.tempList.clear();

        return tableEndIndex - tableStartIndex; //is this correct? I think so.
    }

    @Override
    public int readAcyclic(byte[] source, int sourceOffset, Object destination) {
        return read(source, sourceOffset, destination);
    }

    @Override
    public int readCyclic(byte[] source, int sourceOffset, Object finalDestination, RionObjectReader.CyclicObjectGraphReadState readState) {
        int tableStartIndex = sourceOffset;


        int leadByte     = 255 & source[sourceOffset++];
        int fieldType    = leadByte >> 4;
        int extFieldType = -1;
        int lengthLength = leadByte & 15;  // 15 = binary 00001111 - filters out 4 top bits

        //null check (lengthLength == 0) ?
        if(lengthLength == 0) {
            try {
                this.field.set(finalDestination, null);
            } catch (IllegalAccessException e) {
                //todo do something more intelligent here!
                e.printStackTrace();
            }
            return 1; // null fields are always only 1 byte long
        }

        if(fieldType == RionFieldTypes.EXTENDED) {
            extFieldType = 255 & source[sourceOffset++];
        }

        if(fieldType == RionFieldTypes.EXTENDED && extFieldType == RionFieldTypes.EXT_REFERENCE) {
            // The whole Table (array / list) is a reference to an already read table.
            // Get that Table from the readState and set it on finalDestination

            //read reference field.
            int referencedObjectIndex = 255 & source[sourceOffset++];
            for(int i=1;i<lengthLength; i++){
                referencedObjectIndex <<= 8;
                referencedObjectIndex |= 255 & source[sourceOffset++];
            }

            Object destination = readState.getObject(referencedObjectIndex);
            try {
                this.field.set(finalDestination, destination);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return 2 + lengthLength;
        } else {
            //original code below

            //int tableStartIndex = sourceOffset;

            //int tableLeadByte = 255 & source[sourceOffset++];
            //int tableLengthLength  = tableLeadByte & 15;
            int tableLeadByte      = leadByte;
            int tableLengthLength  = lengthLength;

            int tableLength = 255 & source[sourceOffset++];
            for(int i=1; i < tableLengthLength; i++){
                tableLength <<= 8;
                tableLength |= 255 & source[sourceOffset++];
            }

            int tableEndIndex = tableStartIndex + 1 + tableLengthLength + tableLength;

            //read table field element count (row count)
            int elementCountLeadByte = source[sourceOffset++];
            int elementCountLength   = elementCountLeadByte & 15;

            int elementCount = 0;
            for(int i=0; i<elementCountLength; i++){
                elementCount <<= 8;
                elementCount |= 255 & source[sourceOffset++];
            }


            //read the key fields of the table
            tempKeyFieldKey.setSource(source);
            int fieldReadersInArray = 0;

            IRionFieldReader tempFieldReader = null;
            boolean endOfKeyFieldsFound = false;
            while(!endOfKeyFieldsFound){
                int keyFieldLeadByte = 255 & source[sourceOffset++];
                int keyFieldType     = keyFieldLeadByte >> 4;

                switch(keyFieldType){
                    case RionFieldTypes.KEY_SHORT:  {
                        int keyLength = keyFieldLeadByte & 15;
                        tempKeyFieldKey.setOffsets(sourceOffset, keyLength);
                        tempFieldReader = this.fieldReaderMap.get(tempKeyFieldKey);
                        if(tempFieldReader == null){
                            tempFieldReader = this.nopFieldReader;
                        }
                        this.fieldReaderArray[fieldReadersInArray++] = tempFieldReader;
                        sourceOffset += keyLength;
                        break;
                    }
                    case RionFieldTypes.KEY : {
                        int keyLengthLength = keyFieldLeadByte & 15;
                        int keyLength = 0;
                        for(int i=0; i < keyLengthLength; i++){
                            keyLength <<= 8;
                            keyLength |= 255 & source[sourceOffset++];
                        }
                        tempKeyFieldKey.setOffsets(sourceOffset, keyLength);
                        tempFieldReader = this.fieldReaderMap.get(tempKeyFieldKey);
                        if(tempFieldReader == null){
                            tempFieldReader = this.nopFieldReader;
                        }
                        this.fieldReaderArray[fieldReadersInArray++] = tempFieldReader;
                        sourceOffset += keyLength;
                        break;
                    }

                    default : {
                        endOfKeyFieldsFound = true;
                    }
                }
            }

            //start reading the value fields.
            sourceOffset--; //will have skipped over the lead byte of first value field during search for key fields.

            Object arrayInstance = Array.newInstance(this.typeInTable, elementCount);
            readState.addObjectAsRead(arrayInstance);

            int fieldReaderIndex = 0;
            Object objectInTable = null;
            int arrayIndex = 0;
            while(sourceOffset < tableEndIndex){
                if(fieldReaderIndex == 0) {

                    //Check if first field for this Table row is a Row Reference field. If yes, read the row object.
                    //If no, read row as normally - using readCyclic() method of the field readers for row.
                    int rowLeadByte  = 0xFF & source[sourceOffset];
                    int rowFieldType = rowLeadByte >> 4;
                    int rowLengthLength = 0xF & rowLeadByte;

                    if(rowFieldType == RionFieldTypes.EXTENDED && source[sourceOffset+1] == RionFieldTypes.EXT_ROW_REFERENCE) {
                        sourceOffset += 2; //skip over row lead byte and extended field type

                        //read reference index
                        int rowReferenceLength = 0xF & rowLeadByte;
                        int rowReferenceIndex  = 255 & source[sourceOffset++];
                        for(int i=1;i<rowReferenceLength; i++){
                            rowReferenceIndex <<= 8;
                            rowReferenceIndex |= 255 & source[sourceOffset++];
                        }

                        objectInTable = readState.getObject(rowReferenceIndex);
                        Array.set(arrayInstance, arrayIndex++, objectInTable);

                        // jump to next iteration in while loop.
                        // Do NOT let field readers read values for this row - as all values for this row are
                        // already contained in referenced object.
                        continue;
                    } else {
                        try {
                            objectInTable = this.typeInTable.newInstance();
                            readState.addObjectAsRead(objectInTable);
                            Array.set(arrayInstance, arrayIndex++, objectInTable);
                            //this.tempList.add(objectInTable);
                        } catch (InstantiationException e) {
                            e.printStackTrace();  //todo do something more intelligent here
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //todo do something more intelligent here
                        }
                    }
                }

                sourceOffset += this.fieldReaderArray[fieldReaderIndex++]
                        .readCyclic(source, sourceOffset, objectInTable, readState);

                if(fieldReaderIndex == fieldReadersInArray){
                    fieldReaderIndex = 0; // cycle back to first field reader.
                }


            }

            try {
                this.field.set(finalDestination, arrayInstance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            this.tempList.clear();

            return tableEndIndex - tableStartIndex; //is this correct? I think so.

        }
    }

    @Override
    public void setNull(Object destination) {

    }
}
