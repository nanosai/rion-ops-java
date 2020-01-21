package com.nanosai.rionops.rion.read.object;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class RionObjectReaderBuilder {

    protected Class typeClass = null;
    protected Map<RionKeyFieldKey, IRionFieldReader> fieldReaderMap = new HashMap<>();

    public Class getTypeClass() {
        return typeClass;
    }

    public Map<RionKeyFieldKey, IRionFieldReader> getFieldReaders() {
        return fieldReaderMap;
    }

    public RionObjectReaderBuilder setTypeClass(Class typeClass) {
        this.typeClass = typeClass;
        return this;
    }

    public RionObjectReaderBuilder addDeclaredFieldsForTypeClass() {
        return addDeclaredFieldsForTypeClass(new RionObjectReaderConfiguratorNopImpl());
    }

    public RionObjectReaderBuilder addDeclaredFieldsForTypeClass(IRionObjectReaderConfigurator configurator){
        Field[] fields = this.typeClass.getDeclaredFields();
        this.fieldReaderMap = RionFieldReaderUtil.createFieldReaders(fields, configurator, new HashMap<>());
        return this;
    }



    public RionObjectReader build()  {
        RionObjectReader reader = new RionObjectReader(this.typeClass, this.fieldReaderMap);

        return reader;
    }


}
