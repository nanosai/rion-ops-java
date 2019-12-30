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
    public static Map<IonKeyFieldKey, IIonFieldReader> createFieldReaders(
            Field[] fields,
            IIonObjectReaderConfigurator configurator,
            Map<Field, IIonFieldReader> existingFieldReaders) {

        IonFieldReaderConfiguration          fieldConfiguration = new IonFieldReaderConfiguration();
        Map<IonKeyFieldKey, IIonFieldReader> fieldReaderMap     = new HashMap<>();

        for(int i=0; i < fields.length; i++){

            fieldConfiguration.field     = fields[i];
            fieldConfiguration.fieldName = fields[i].getName();
            fieldConfiguration.alias     = fields[i].getName();
            fieldConfiguration.include   = true;

            configurator.configure(fieldConfiguration);

            if(existingFieldReaders.containsKey(fields[i])){
                IIonFieldReader fieldReader = existingFieldReaders.get(fields[i]);
                try {
                    fieldReaderMap.put(new IonKeyFieldKey(fieldConfiguration.alias.getBytes("UTF-8")), fieldReader); //todo this is wrong - should be IonKeyFieldKey - except those are not unique to classes...
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    //todo better exception handling, although UTF-8 is a known encoding.
                }
                continue;
            }


            if (fieldConfiguration.include) {
                IIonFieldReader fieldReader = IonUtil.createFieldReader(fields[i], configurator);

                existingFieldReaders.put(fields[i], fieldReader);

                if(fieldReader instanceof IonFieldReaderObject){
                    ((IonFieldReaderObject) fieldReader).generateFieldReaders(configurator, existingFieldReaders);
                } else if(fieldReader instanceof IonFieldReaderTable){
                    ((IonFieldReaderTable) fieldReader).generateFieldReaders(configurator, existingFieldReaders);
                }

                try {
                    fieldReaderMap.put(new IonKeyFieldKey(fieldConfiguration.alias.getBytes("UTF-8")), fieldReader);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    //todo throw exception - but will never happen since UTF-8 is a known encoding.
                }
            }
        }
        return fieldReaderMap;
    }


    public static IIonFieldWriter[] createFieldWriters(
            Field[] fields, IIonObjectWriterConfigurator configurator,
            Map<Field, IIonFieldWriter> existingFieldWriters) {
        List<IIonFieldWriter> fieldWritersTemp = new ArrayList<>();

        IonFieldWriterConfiguration fieldConfiguration = new IonFieldWriterConfiguration();

        for(int i=0; i < fields.length; i++){
            if(existingFieldWriters.containsKey(fields[i])){
                IIonFieldWriter fieldWriter = existingFieldWriters.get(fields[i]);
                fieldWritersTemp.add(fieldWriter);
                continue;
            }

            fieldConfiguration.field     = fields[i];
            fieldConfiguration.fieldName = fields[i].getName();
            fieldConfiguration.alias     = fields[i].getName();
            fieldConfiguration.include   = true;

            configurator.configure(fieldConfiguration);

            if(fieldConfiguration.include){
                IIonFieldWriter fieldWriter =
                        IonUtil.createFieldWriter(fields[i], fieldConfiguration.alias, configurator, existingFieldWriters);

                existingFieldWriters.put(fields[i], fieldWriter);

                if(fieldWriter instanceof IonFieldWriterObject){
                    ((IonFieldWriterObject) fieldWriter).generateFieldWriters(configurator, existingFieldWriters);
                } else if(fieldWriter instanceof IonFieldWriterTable){
                    ((IonFieldWriterTable) fieldWriter).generateFieldWriters(configurator, existingFieldWriters);
                }
                fieldWritersTemp.add(fieldWriter);
            }
        }

        IIonFieldWriter[] fieldWriters = new IIonFieldWriter[fieldWritersTemp.size()];

        for(int i=0, n=fieldWritersTemp.size(); i < n; i++){
            fieldWriters[i] = fieldWritersTemp.get(i);
        }

        return fieldWriters;
    }

    public static IIonFieldWriter createFieldWriter(Field field, String alias, IIonObjectWriterConfigurator configurator, Map<Field, IIonFieldWriter> existingFieldWriters){
        field.setAccessible(true); //allows access to private fields, and supposedly speeds up reflection...  ?
        Class fieldType = field.getType();

        if(boolean.class.equals(fieldType)){
            return new IonFieldWriterBoolean(field, alias);
        }
        if(byte.class.equals(fieldType)){
            return new IonFieldWriterByte(field, alias);
        }
        if(short.class.equals(fieldType)){
            return new IonFieldWriterShort(field, alias);
        }
        if(int.class.equals(fieldType)){
            return new IonFieldWriterInt(field, alias);
        }
        if(long.class.equals(fieldType)){
            return new IonFieldWriterLong(field, alias);
        }
        if(float.class.equals(fieldType)){
            return new IonFieldWriterFloat(field, alias);
        }
        if(double.class.equals(fieldType)){
            return new IonFieldWriterDouble(field, alias);
        }
        if(String.class.equals(fieldType)){
            return new IonFieldWriterString(field, alias);
        }
        if(Calendar.class.equals(fieldType)){
            return new IonFieldWriterCalendar(field, alias);
        }
        if(GregorianCalendar.class.equals(fieldType)){
            return new IonFieldWriterCalendar(field, alias);
        }
        if(fieldType.isArray()){
            if(byte.class.equals(fieldType.getComponentType())){
                return new IonFieldWriterArrayByte(field, alias);
            }
            if(short.class.equals(fieldType.getComponentType())){
                return new IonFieldWriterArrayShort(field, alias);
            }
            if(int.class.equals(fieldType.getComponentType())){
                return new IonFieldWriterArrayInt(field, alias);
            }
            if(long.class.equals(fieldType.getComponentType())){
                return new IonFieldWriterArrayLong(field, alias);
            }
            if(float.class.equals(fieldType.getComponentType())){
                return new IonFieldWriterArrayFloat(field, alias);
            }
            if(double.class.equals(fieldType.getComponentType())){
                return new IonFieldWriterArrayDouble(field, alias);
            }
            return new IonFieldWriterTable(field, alias);
        }

        return new IonFieldWriterObject(field, alias);
    }

     */


    //todo remove this ?
    /*
    public static IIonFieldReader createFieldReader(Field field){
        return createFieldReader(field, null);
    }
    */

    /*
    public static IIonFieldReader createFieldReader(Field field, IIonObjectReaderConfigurator configurator){

        field.setAccessible(true); //allows access to private fields, and supposedly speeds up reflection...  ?
        Class fieldType = field.getType();

        if(boolean.class.equals(fieldType)){
            return new IonFieldReaderBoolean(field);
        }
        if(byte.class.equals(fieldType)){
            return new IonFieldReaderByte(field);
        }
        if(short.class.equals(fieldType)){
            return new IonFieldReaderShort(field);
        }
        if(int.class.equals(fieldType)){
            return new IonFieldReaderInt(field);
        }
        if(long.class.equals(fieldType)){
            return new IonFieldReaderLong(field);
        }
        if(float.class.equals(fieldType)){
            return new IonFieldReaderFloat(field);
        }
        if(double.class.equals(fieldType)){
            return new IonFieldReaderDouble(field);
        }
        if(String.class.equals(fieldType)){
            return new IonFieldReaderString(field);
        }
        if(Calendar.class.equals(fieldType)){
            return new IonFieldReaderCalendar(field);
        }
        if(GregorianCalendar.class.equals(fieldType)){
            return new IonFieldReaderCalendar(field);
        }
        if(fieldType.isArray()){
            if(byte.class.equals(fieldType.getComponentType())){
                return new IonFieldReaderArrayByte(field);
            }
            if(short.class.equals(fieldType.getComponentType())){
                return new IonFieldReaderArrayShort(field);
            }
            if(int.class.equals(fieldType.getComponentType())){
                return new IonFieldReaderArrayInt(field);
            }
            if(long.class.equals(fieldType.getComponentType())){
                return new IonFieldReaderArrayLong(field);
            }
            if(float.class.equals(fieldType.getComponentType())){
                return new IonFieldReaderArrayFloat(field);
            }
            if(double.class.equals(fieldType.getComponentType())){
                return new IonFieldReaderArrayDouble(field);
            }
            return new IonFieldReaderTable(field, configurator);
        } else {
            return new IonFieldReaderObject(field, configurator);
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
