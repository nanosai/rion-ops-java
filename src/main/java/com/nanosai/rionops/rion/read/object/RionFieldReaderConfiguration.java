package com.nanosai.rionops.rion.read.object;

import java.lang.reflect.Field;

/**
 * A configuration for a single IRionFieldReader (used internally by the RionObjectReader).
 *
 * An IRionObjectReaderConfigurator can read the <code>field</code> field to see what field (field reader)
 * this configuration is for.
 *
 * The fieldName of the field is stored in the <code>fieldName</code> field (but is also accessible
 * via field.getName()).
 *
 * The <code>include</code> field defaults to true, but can be set to false. The RionObjectReader will then not
 * include read values for this field from the ION data, even if that field is present.
 *
 * The <code>alias</code> field can be used if the field has a different fieldName in the ION data than the
 * field has in the Java object.
 */
public class RionFieldReaderConfiguration {
    public Field   field   = null;
    public String fieldName = null;


    public boolean include = true;
    public String  alias   = null;


}
