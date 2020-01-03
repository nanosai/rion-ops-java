package com.nanosai.rionops.rion;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by jjenkov on 19-11-2015.
 */
public class RionUtil {

    public static final long TWO_POW_8  = 256L;
    public static final long TWO_POW_16 = TWO_POW_8 * TWO_POW_8;
    public static final long TWO_POW_24 = TWO_POW_8 * TWO_POW_16;
    public static final long TWO_POW_32 = TWO_POW_8 * TWO_POW_24;
    public static final long TWO_POW_40 = TWO_POW_8 * TWO_POW_32;
    public static final long TWO_POW_48 = TWO_POW_8 * TWO_POW_40;
    public static final long TWO_POW_56 = TWO_POW_8 * TWO_POW_48;

    public static int byteLengthOfInt64Value(long value){
        if(value < TWO_POW_8)  return 1;
        if(value < TWO_POW_16) return 2;
        if(value < TWO_POW_24) return 3;
        if(value < TWO_POW_32) return 4;
        if(value < TWO_POW_40) return 5;
        if(value < TWO_POW_48) return 6;
        if(value < TWO_POW_56) return 7;
        return 8;
    }

    /*
    public static Map<RionKeyFieldKey, IRionFieldReader> createFieldReaders(
            Field[] fields,
            IRionObjectReaderConfigurator configurator,
            Map<Field, IRionFieldReader> existingFieldReaders) {

        RionFieldReaderConfiguration          fieldConfiguration = new RionFieldReaderConfiguration();
        Map<RionKeyFieldKey, IRionFieldReader> fieldReaderMap     = new HashMap<>();

        for(int i=0; i < fields.length; i++){

            fieldConfiguration.field     = fields[i];
            fieldConfiguration.fieldName = fields[i].getName();
            fieldConfiguration.alias     = fields[i].getName();
            fieldConfiguration.include   = true;

            configurator.configure(fieldConfiguration);

            if(existingFieldReaders.containsKey(fields[i])){
                IRionFieldReader fieldReader = existingFieldReaders.get(fields[i]);
                try {
                    fieldReaderMap.put(new RionKeyFieldKey(fieldConfiguration.alias.getBytes("UTF-8")), fieldReader); //todo this is wrong - should be RionKeyFieldKey - except those are not unique to classes...
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    //todo better exception handling, although UTF-8 is a known encoding.
                }
                continue;
            }


            if (fieldConfiguration.include) {
                IRionFieldReader fieldReader = IonUtil.createFieldReader(fields[i], configurator);

                existingFieldReaders.put(fields[i], fieldReader);

                if(fieldReader instanceof RionFieldReaderObject){
                    ((RionFieldReaderObject) fieldReader).generateFieldReaders(configurator, existingFieldReaders);
                } else if(fieldReader instanceof RionFieldReaderTable){
                    ((RionFieldReaderTable) fieldReader).generateFieldReaders(configurator, existingFieldReaders);
                }

                try {
                    fieldReaderMap.put(new RionKeyFieldKey(fieldConfiguration.alias.getBytes("UTF-8")), fieldReader);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    //todo throw exception - but will never happen since UTF-8 is a known encoding.
                }
            }
        }
        return fieldReaderMap;
    }


    public static IRionFieldWriter[] createFieldWriters(
            Field[] fields, IRionObjectWriterConfigurator configurator,
            Map<Field, IRionFieldWriter> existingFieldWriters) {
        List<IRionFieldWriter> fieldWritersTemp = new ArrayList<>();

        RionFieldWriterConfiguration fieldConfiguration = new RionFieldWriterConfiguration();

        for(int i=0; i < fields.length; i++){
            if(existingFieldWriters.containsKey(fields[i])){
                IRionFieldWriter fieldWriter = existingFieldWriters.get(fields[i]);
                fieldWritersTemp.add(fieldWriter);
                continue;
            }

            fieldConfiguration.field     = fields[i];
            fieldConfiguration.fieldName = fields[i].getName();
            fieldConfiguration.alias     = fields[i].getName();
            fieldConfiguration.include   = true;

            configurator.configure(fieldConfiguration);

            if(fieldConfiguration.include){
                IRionFieldWriter fieldWriter =
                        IonUtil.createFieldWriter(fields[i], fieldConfiguration.alias, configurator, existingFieldWriters);

                existingFieldWriters.put(fields[i], fieldWriter);

                if(fieldWriter instanceof RionFieldWriterObject){
                    ((RionFieldWriterObject) fieldWriter).generateFieldWriters(configurator, existingFieldWriters);
                } else if(fieldWriter instanceof RionFieldWriterTable){
                    ((RionFieldWriterTable) fieldWriter).generateFieldWriters(configurator, existingFieldWriters);
                }
                fieldWritersTemp.add(fieldWriter);
            }
        }

        IRionFieldWriter[] fieldWriters = new IRionFieldWriter[fieldWritersTemp.size()];

        for(int i=0, n=fieldWritersTemp.size(); i < n; i++){
            fieldWriters[i] = fieldWritersTemp.get(i);
        }

        return fieldWriters;
    }

    public static IRionFieldWriter createFieldWriter(Field field, String alias, IRionObjectWriterConfigurator configurator, Map<Field, IRionFieldWriter> existingFieldWriters){
        field.setAccessible(true); //allows access to private fields, and supposedly speeds up reflection...  ?
        Class fieldType = field.getType();

        if(boolean.class.equals(fieldType)){
            return new RionFieldWriterBoolean(field, alias);
        }
        if(byte.class.equals(fieldType)){
            return new RionFieldWriterByte(field, alias);
        }
        if(short.class.equals(fieldType)){
            return new RionFieldWriterShort(field, alias);
        }
        if(int.class.equals(fieldType)){
            return new RionFieldWriterInt(field, alias);
        }
        if(long.class.equals(fieldType)){
            return new RionFieldWriterLong(field, alias);
        }
        if(float.class.equals(fieldType)){
            return new RionFieldWriterFloat(field, alias);
        }
        if(double.class.equals(fieldType)){
            return new RionFieldWriterDouble(field, alias);
        }
        if(String.class.equals(fieldType)){
            return new RionFieldWriterString(field, alias);
        }
        if(Calendar.class.equals(fieldType)){
            return new RionFieldWriterCalendar(field, alias);
        }
        if(GregorianCalendar.class.equals(fieldType)){
            return new RionFieldWriterCalendar(field, alias);
        }
        if(fieldType.isArray()){
            if(byte.class.equals(fieldType.getComponentType())){
                return new RionFieldWriterArrayByte(field, alias);
            }
            if(short.class.equals(fieldType.getComponentType())){
                return new RionFieldWriterArrayShort(field, alias);
            }
            if(int.class.equals(fieldType.getComponentType())){
                return new RionFieldWriterArrayInt(field, alias);
            }
            if(long.class.equals(fieldType.getComponentType())){
                return new RionFieldWriterArrayLong(field, alias);
            }
            if(float.class.equals(fieldType.getComponentType())){
                return new RionFieldWriterArrayFloat(field, alias);
            }
            if(double.class.equals(fieldType.getComponentType())){
                return new RionFieldWriterArrayDouble(field, alias);
            }
            return new RionFieldWriterTable(field, alias);
        }

        return new RionFieldWriterObject(field, alias);
    }

     */


    //todo remove this ?
    /*
    public static IRionFieldReader createFieldReader(Field field){
        return createFieldReader(field, null);
    }
    */

    /*
    public static IRionFieldReader createFieldReader(Field field, IRionObjectReaderConfigurator configurator){

        field.setAccessible(true); //allows access to private fields, and supposedly speeds up reflection...  ?
        Class fieldType = field.getType();

        if(boolean.class.equals(fieldType)){
            return new RionFieldReaderBoolean(field);
        }
        if(byte.class.equals(fieldType)){
            return new RionFieldReaderByte(field);
        }
        if(short.class.equals(fieldType)){
            return new RionFieldReaderShort(field);
        }
        if(int.class.equals(fieldType)){
            return new RionFieldReaderInt(field);
        }
        if(long.class.equals(fieldType)){
            return new RionFieldReaderLong(field);
        }
        if(float.class.equals(fieldType)){
            return new RionFieldReaderFloat(field);
        }
        if(double.class.equals(fieldType)){
            return new RionFieldReaderDouble(field);
        }
        if(String.class.equals(fieldType)){
            return new RionFieldReaderString(field);
        }
        if(Calendar.class.equals(fieldType)){
            return new RionFieldReaderCalendar(field);
        }
        if(GregorianCalendar.class.equals(fieldType)){
            return new RionFieldReaderCalendar(field);
        }
        if(fieldType.isArray()){
            if(byte.class.equals(fieldType.getComponentType())){
                return new RionFieldReaderArrayByte(field);
            }
            if(short.class.equals(fieldType.getComponentType())){
                return new RionFieldReaderArrayShort(field);
            }
            if(int.class.equals(fieldType.getComponentType())){
                return new RionFieldReaderArrayInt(field);
            }
            if(long.class.equals(fieldType.getComponentType())){
                return new RionFieldReaderArrayLong(field);
            }
            if(float.class.equals(fieldType.getComponentType())){
                return new RionFieldReaderArrayFloat(field);
            }
            if(double.class.equals(fieldType.getComponentType())){
                return new RionFieldReaderArrayDouble(field);
            }
            return new RionFieldReaderTable(field, configurator);
        } else {
            return new RionFieldReaderObject(field, configurator);
        }

        //todo support object field writer

    }

    public static void writeLength(long length, int lengthLength, byte[] destination, int destinationOffset){
        switch(lengthLength){
            case 8 : { destination[destinationOffset++] = (byte) (255 & (length >> 56));}
            case 7 : { destination[destinationOffset++] = (byte) (255 & (length >> 48));}
            case 6 : { destination[destinationOffset++] = (byte) (255 & (length >> 40));}
            case 5 : { destination[destinationOffset++] = (byte) (255 & (length >> 32));}
            case 4 : { destination[destinationOffset++] = (byte) (255 & (length >> 24));}
            case 3 : { destination[destinationOffset++] = (byte) (255 & (length >> 16));}
            case 2 : { destination[destinationOffset++] = (byte) (255 & (length >>  8));}
            case 1 : { destination[destinationOffset++] = (byte) (255 &  length );}
            default : { }  //don't write anything - no length bytes to write, or invalid lengthLength (> 8)
        }
    }

    public static byte[] preGenerateKeyField(Field field) {
        return preGenerateKeyField(field.getName());
    }

    public static byte[] preGenerateKeyField(String fieldNameStr) {
        byte[] keyField  = null;
        try {
            byte[] fieldName = fieldNameStr.getBytes("UTF-8");

            int fieldNameLength = fieldName.length;
            if(fieldNameLength <= 15){
                keyField = new byte[1 + fieldName.length];
                keyField[0] = (byte) (255 & ((IonFieldTypes.KEY_SHORT << 4) | fieldName.length));
                System.arraycopy(fieldName, 0, keyField, 1, fieldName.length);
            } else {
                int length = fieldName.length;
                int lengthLength = IonUtil.byteLengthOfInt64Value(length);
                keyField = new byte[1 + lengthLength + fieldName.length];

                keyField[0] = (byte) (255 & ((IonFieldTypes.KEY << 4) | lengthLength));
                int destOffset = 1;
                for(int i=(lengthLength-1)*8; i >= 0; i-=8){
                    keyField[destOffset++] = (byte) (255 & (length >> i));
                }

                System.arraycopy(fieldName, 0, keyField, destOffset, fieldName.length);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //will never happen - UTF-8 is always supported.
        }

        return keyField;
    }

     */



}
