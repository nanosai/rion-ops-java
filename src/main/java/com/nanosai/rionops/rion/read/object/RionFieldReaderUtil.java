package com.nanosai.rionops.rion.read.object;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RionFieldReaderUtil {


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
                    fieldReaderMap.put(new RionKeyFieldKey(fieldConfiguration.alias.getBytes("UTF-8")), fieldReader); //todo this is wrong - should be IonKeyFieldKey - except those are not unique to classes...
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    //todo better exception handling, although UTF-8 is a known encoding.
                }
                continue;
            }


            if (fieldConfiguration.include) {
                IRionFieldReader fieldReader = RionFieldReaderUtil.createFieldReader(fields[i], configurator);

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

}
