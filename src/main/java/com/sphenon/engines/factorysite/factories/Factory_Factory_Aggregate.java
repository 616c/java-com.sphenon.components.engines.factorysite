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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

public class Factory_Factory_Aggregate
{
    private String aggregate_class;
    private java.util.Hashtable parameters; 

    public Factory_Factory_Aggregate(CallContext context) {
    }

    public void setAggregateClass(CallContext context, String aggregate_class) {
        this.aggregate_class = aggregate_class;
    }

    public void setParameters(CallContext context, java.util.Hashtable parameters) {
        this.parameters = parameters;
    }

    public Factory_Aggregate create (CallContext call_context) {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        if (this.aggregate_class == null) {
            cc.throwPreConditionViolation(context, FactorySiteStringPool.get(context, "1.0.0" /* Factory_Factory_Aggregate: parameter not set: aggregate class */));
        }
        if (this.parameters == null) {
            cc.throwPreConditionViolation(context, FactorySiteStringPool.get(context, "1.0.1" /* Factory_Factory_Aggregate: parameter not set: parameters */));
        }

        Factory_Aggregate cf = new Factory_Aggregate(context);
        cf.setAggregateClass(context, this.aggregate_class);
        cf.setParameters(context, this.parameters);

        return cf;
    }
}


