package com.sphenon.engines.factorysite.factories;

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_DynamicCOCPAccessorFactory implements com.sphenon.engines.factorysite.factories.DynamicCOCPAccessorFactory.Config {

    protected Configuration configuration;

    protected Configuration_DynamicCOCPAccessorFactory (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.factorysite.factories.DynamicCOCPAccessorFactory");
    }

    static public Configuration_DynamicCOCPAccessorFactory get (CallContext context) {
        return new Configuration_DynamicCOCPAccessorFactory(context);
    }

    public boolean getDoGeneration(CallContext context) {
        String entry = "DoGeneration";
        return configuration.get(context, entry, true);
    }

    public boolean getDoCompilation(CallContext context) {
        String entry = "DoCompilation";
        return configuration.get(context, entry, true);
    }

    public boolean getTryToLoadAsResource(CallContext context) {
        String entry = "TryToLoadAsResource";
        return configuration.get(context, entry, false);
    }

    public boolean getUseExistingResourceUnconditionally(CallContext context) {
        String entry = "UseExistingResourceUnconditionally";
        return configuration.get(context, entry, false);
    }
}
