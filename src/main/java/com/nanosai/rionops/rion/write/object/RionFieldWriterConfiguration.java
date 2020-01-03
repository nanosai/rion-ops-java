package com.nanosai.rionops.rion.write.object;

import java.lang.reflect.Field;

/**
 * A configuration for a single IRionFieldWriter (used internally by the RionObjectWriter).
 *
 * An IRionObjectWriterConfigurator can read the <code>field</code> field to see what field (field writer)
 * this configuration is for.
 *
 * The fieldName of the field is stored in the <code>fieldName</code> field (but is also accessible
 * via field.getName()).
 *
 * The <code>include</code> field defaults to true, but can be set to false. The RionObjectWriter will then not
 * include a field writer for this field, meaning that field will not be included in the written ION data.
 *
 * The <code>alias</code> field can be used to give the field a different fieldName in the written ION data than the
 * field has in the Java object.
 */
public class RionFieldWriterConfiguration {
    public Field   field    = null;
    public String fieldName = null;


    public boolean include = true;
    public String  alias   = null;


}
