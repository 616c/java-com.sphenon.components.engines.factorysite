package com.sphenon.engines.factorysite.factories;

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_DynamicCOCPAccessorRetriever implements com.sphenon.engines.factorysite.factories.DynamicCOCPAccessorRetriever.Config {

    protected Configuration configuration;

    protected Configuration_DynamicCOCPAccessorRetriever (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.factorysite.factories.DynamicCOCPAccessorRetriever");
    }

    static public Configuration_DynamicCOCPAccessorRetriever get (CallContext context) {
        return new Configuration_DynamicCOCPAccessorRetriever(context);
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
