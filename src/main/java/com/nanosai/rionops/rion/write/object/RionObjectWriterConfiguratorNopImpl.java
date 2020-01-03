package com.nanosai.rionops.rion.write.object;

/**
 * Created by jjenkov on 10-02-2016.
 */
public class RionObjectWriterConfiguratorNopImpl implements IRionObjectWriterConfigurator {

    public static final RionObjectWriterConfiguratorNopImpl DEFAULT_INSTANCE = new RionObjectWriterConfiguratorNopImpl();

    @Override
    public void configure(RionFieldWriterConfiguration config) {
        //do nothing.
    }
}
