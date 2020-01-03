package com.nanosai.rionops.rion.write.object;

/**
 * An IRionObjectWriterConfigurator can configure an RionObjectWriter. An implementation of this interface is
 * passed to the RionObjectWriter's constructor. The RionObjectWriter then calls the IRionObjectWriterConfigurator
 * for each field in the target class. The IRionObjectWriterConfigurator instance can then set a few configuration
 * options for the given field (exclude it from serialization, or use another field fieldName (alias))
 */
public interface IRionObjectWriterConfigurator {

    public void configure(RionFieldWriterConfiguration config);

}
