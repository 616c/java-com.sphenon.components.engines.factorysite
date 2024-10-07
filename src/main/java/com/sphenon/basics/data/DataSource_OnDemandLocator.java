package com.sphenon.basics.data;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.engines.factorysite.factories.*;

import java.util.Hashtable;

public class DataSource_OnDemandLocator<T>  extends DataSource_OnDemand<T> {
    public DataSource_OnDemandLocator(CallContext context) {
        super(context);
    }

    protected String locator;

    public String getLocator (CallContext context) {
        return this.locator;
    }

    public void setLocator (CallContext context, String locator) {
        this.locator = locator;
    }

    protected Object base_object;

    public Object getBaseObject (CallContext context) {
        return this.base_object;
    }

    public Object defaultBaseObject (CallContext context) {
        return null;
    }

    public void setBaseObject (CallContext context, Object base_object) {
        this.base_object = base_object;
    }

    protected T createOnDemandInstance(CallContext context) {
        try {
            return (T) Locator.resolve(context, this.locator, this.base_object);
        } catch (InvalidLocator il) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, il, "Could not deliver ondemand locator '%(locator)'", "locator", this.locator);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    protected String getErrorInfo(CallContext context) {
        return "Locator ('" + this.locator + "')";
    }
} 
