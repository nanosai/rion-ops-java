package com.nanosai.rionops.rion.write.object;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RionObjectWriterBuilder<T> {

    protected List<IRionFieldWriter>        fieldWriters         = new ArrayList<>();
    protected Map<Object, IRionFieldWriter> existingFieldWriters = new HashMap<>();

    public List<IRionFieldWriter> getFieldWriters() {
        return fieldWriters;
    }

    public RionObjectWriterBuilder addFields(Field[] fields, IRionObjectWriterConfigurator configurator){
        IRionFieldWriter[] fieldWriters = RionFieldWriterUtil.createFieldWriters(fields, configurator, this.existingFieldWriters);

        for(int i=0; i<fieldWriters.length; i++) {
            this.fieldWriters.add(fieldWriters[i]);
        }
        return this;
    }

    public RionObjectWriterBuilder addFields(Field[] fields){
        return addFields(fields, RionObjectWriterConfiguratorNopImpl.DEFAULT_INSTANCE);
    }

    public RionObjectWriterBuilder addDeclaredFields(Class<T> forClass){
        return addFields(forClass.getDeclaredFields());
    }

    public RionObjectWriterBuilder addFields(Class<T> forClass){
        return addFields(forClass.getFields());
    }


    public RionObjectWriter<T> build() {
        IRionFieldWriter[] finalFieldWriters = new IRionFieldWriter[this.fieldWriters.size()];
        finalFieldWriters = this.fieldWriters.toArray(finalFieldWriters);
        return new RionObjectWriter(finalFieldWriters);
    }


}
