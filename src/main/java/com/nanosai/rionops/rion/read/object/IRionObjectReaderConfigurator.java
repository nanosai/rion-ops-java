package com.nanosai.rionops.rion.read.object;

/**
 * An IRionObjectReaderConfigurator can configure the individual field readers of an RionObjectReader. An implementation
 * of this interface is passed to the constructor of the RionObjectReader. The RionObjectReader then calls the implementation
 * of this interface to obtain configuration for each field in the class the RionObjectReader is targeted at.
 *
 */
public interface IRionObjectReaderConfigurator {

    public void configure(RionFieldReaderConfiguration config);

}
