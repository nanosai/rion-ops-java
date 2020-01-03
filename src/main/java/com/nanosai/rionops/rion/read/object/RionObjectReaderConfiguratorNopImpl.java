package com.nanosai.rionops.rion.read.object;

/**
 * Created by jjenkov on 10-02-2016.
 */
public class RionObjectReaderConfiguratorNopImpl implements IRionObjectReaderConfigurator {

    public static final RionObjectReaderConfiguratorNopImpl DEFAULT_INSTANCE = new RionObjectReaderConfiguratorNopImpl();

    @Override
    public void configure(RionFieldReaderConfiguration config) {
        //do nothing.
    }
}
