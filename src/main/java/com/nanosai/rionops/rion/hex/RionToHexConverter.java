package com.nanosai.rionops.rion.hex;

import com.nanosai.rionops.rion.RionFieldTypes;
import com.nanosai.rionops.rion.read.RionReader;

public class RionToHexConverter {

    private int indentationIncrease = 4;


    public StringBuilder convertFormatted(RionReader rionReader, StringBuilder dest){
        return convertFormatted(rionReader, dest, 0);
    }

    public StringBuilder convertFormatted(RionReader rionReader, StringBuilder dest, int indentationLevel){
        while(rionReader.hasNext()){
            rionReader.nextParse();

            convertField(rionReader, dest, indentationLevel);
        }

        return dest;

    }

    private void convertField(RionReader rionReader, StringBuilder dest, int indentationLevel) {
        appendIndentation(dest, indentationLevel);
        switch(rionReader.fieldType) {

            case RionFieldTypes.BOOLEAN : {
                convertTinyField(rionReader, dest);
                break;
            }


            case RionFieldTypes.INT_POS : ;
            case RionFieldTypes.INT_NEG : ;
            case RionFieldTypes.FLOAT : ;
            case RionFieldTypes.UTF_8_SHORT :
            case RionFieldTypes.UTC_DATE_TIME :
            case RionFieldTypes.KEY_SHORT : {
                convertShortField(rionReader, dest);
                break;
            }

            case RionFieldTypes.UTF_8 : ;
            case RionFieldTypes.KEY   : ;
            case RionFieldTypes.BYTES : {
                convertNormalField(rionReader, dest);
                break;
            }

            case RionFieldTypes.ARRAY : {
                convertArrayField(rionReader, dest, indentationLevel);
                break;
            }

            case RionFieldTypes.TABLE : {
                convertTableField(rionReader, dest, indentationLevel);
                break;
            }

            case RionFieldTypes.OBJECT : {
                convertObjectField(rionReader, dest, indentationLevel);
                break;
            }

        }
        if(rionReader.hasNext()){
            dest.append("\r\n");
        }
    }

    private void convertArrayField(RionReader rionReader, StringBuilder dest, int indentationLevel) {
        convertLeadByte(rionReader, dest);
        dest.append(' ');
        convertLengthBytes(rionReader, dest);
        dest.append("\r\n");

        rionReader.moveInto();
        rionReader.nextParse();

        //convert element count field
        convertField(rionReader, dest, indentationLevel + indentationIncrease);
        dest.append("\r\n");

        //convert nested elements
        while(rionReader.hasNext()) {
            rionReader.nextParse();
            convertField(rionReader, dest, indentationLevel + indentationIncrease);
        }
        rionReader.moveOutOf();
    }

    private void convertTableField(RionReader rionReader, StringBuilder dest, int indentationLevel) {
        convertLeadByte(rionReader, dest);
        dest.append(' ');
        convertLengthBytes(rionReader, dest);
        dest.append("\r\n");

        rionReader.moveInto();
        rionReader.nextParse();

        //convert row count field
        convertField(rionReader, dest, indentationLevel + indentationIncrease);
        dest.append("\r\n");

        //convert nested column elements
        int columnsPerRowCount = 0;
        while(rionReader.hasNext()) {
            rionReader.nextParse();
            if(rionReader.fieldType == RionFieldTypes.KEY || rionReader.fieldType == RionFieldTypes.KEY_SHORT){
                columnsPerRowCount++;
                convertField(rionReader, dest, indentationLevel + indentationIncrease);
            } else {
                break;
            }
        }
        dest.append("\r\n");

        //convert first non-Key nested field
        convertField(rionReader, dest, indentationLevel + indentationIncrease);
        int totalColumnCount = 1;

        //convert all following non-key nested fields
        while(rionReader.hasNext()) {
            rionReader.nextParse();
            convertField(rionReader, dest, indentationLevel + indentationIncrease);
            totalColumnCount++;
            if(totalColumnCount % columnsPerRowCount == 0 && rionReader.hasNext()) {
                dest.append("\r\n");
            }
        }

        rionReader.moveOutOf();
    }

    private void convertObjectField(RionReader rionReader, StringBuilder dest, int indentationLevel) {
        convertLeadByte(rionReader, dest);
        dest.append(' ');
        convertLengthBytes(rionReader, dest);
        dest.append("\r\n");

        rionReader.moveInto();
        while(rionReader.hasNext()) {
            rionReader.nextParse();
            convertField(rionReader, dest, indentationLevel + indentationIncrease);
        }
        rionReader.moveOutOf();
    }


    private void convertNormalField(RionReader rionReader, StringBuilder dest) {
        convertLeadByte(rionReader, dest);
        dest.append(' ');
        convertLengthBytes(rionReader, dest);
        dest.append(' ');
        convertValueBytes(rionReader, dest);
    }


    private void convertShortField(RionReader rionReader, StringBuilder dest) {
        convertLeadByte(rionReader, dest);
        dest.append(' ');
        convertValueBytes(rionReader, dest);
    }

    private void convertTinyField(RionReader rionReader, StringBuilder dest) {
        convertLeadByte(rionReader, dest);
    }

    private void convertLeadByte(RionReader rionReader, StringBuilder dest) {
        dest.append(toChar(rionReader.fieldType));
        dest.append(toChar(rionReader.fieldLengthLength));
    }

    private void convertLengthBytes(RionReader rionReader, StringBuilder dest) {
        for(int i=(rionReader.fieldLengthLength-1)*8; i >= 0; i-=8){
            int lengthByte = (byte) (255 & (rionReader.fieldLength >> i));
            convertByte(dest, lengthByte);
        }
    }

    private void convertValueBytes(RionReader rionReader, StringBuilder dest) {
        for (int i = rionReader.index; i < rionReader.index + rionReader.fieldLength; i++) {
            int byteValue = rionReader.source[i];
            convertByte(dest, byteValue);
        }
    }

    private void convertByte(StringBuilder dest, int byteValue) {
        int msn = (0xFF & byteValue) >>  4;
        int lsn = (0x0F & byteValue);

        dest.append(toChar(msn));
        dest.append(toChar(lsn));
    }


    private char toChar(int value) {

        switch(value) {
            case   0 : return '0';
            case   1 : return '1';
            case   2 : return '2';
            case   3 : return '3';
            case   4 : return '4';
            case   5 : return '5';
            case   6 : return '6';
            case   7 : return '7';
            case   8 : return '8';
            case   9 : return '9';
            case  10 : return 'A';
            case  11 : return 'B';
            case  12 : return 'C';
            case  13 : return 'D';
            case  14 : return 'E';
            case  15 : return 'F';

            default : return '?';
        }
    }



    private void appendIndentation(StringBuilder dest, int indentationLevel) {
        for(int i=0; i < indentationLevel; i++) {
            dest.append(' ');
        }
    }
}
