package com.sphenon.engines.factorysite.factories;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

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
