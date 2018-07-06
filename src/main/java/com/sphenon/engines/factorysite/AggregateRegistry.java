package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.gates.*;
import com.sphenon.engines.factorysite.returncodes.*;

public class AggregateRegistry {
    static protected AggregateRegistry registry;
    protected OOMap_String_Type_Type_ oomap;

    static {
        registry = new AggregateRegistry(RootContext.getRootContext());
    }

    protected AggregateRegistry (CallContext context) {
        oomap = new OOMapImpl_String_Type_Type_(context);
    }

    static protected AggregateRegistry getRegistry (CallContext context) {
        return registry;
    }

    static public void register (CallContext context, String ctn, Type handled_type, Type context_type) {
        getRegistry(context).register_(context, ctn, handled_type, context_type);
    }

    static public void register (CallContext context, String ctn, Class handled_type, Class context_type) {
        getRegistry(context).register_(context, ctn, TypeManager.get(context, handled_type), TypeManager.get(context, context_type));
    }

    protected void register_ (CallContext context, String ctn, Type handled_type, Type context_type) {
        oomap.set(context, context_type, handled_type, ctn);
    }

    static public void deregister (CallContext context, Type handled_type, Type context_type) {
        getRegistry(context).deregister_(context, handled_type, context_type);
    }

    static public void deregister (CallContext context, Class handled_type, Class context_type) {
        getRegistry(context).deregister_(context, TypeManager.get(context, handled_type), TypeManager.get(context, context_type));
    }

    protected void deregister_ (CallContext context, Type handled_type, Type context_type) {
        oomap.unset(context, context_type, handled_type);
    }

    static public Object get (CallContext context, Type handled_type, Type context_type, java.util.Hashtable parameters) throws DoesNotExist {
        return getRegistry(context).get_(context, handled_type, context_type, parameters);
    }

    static public Object tryGet (CallContext context, Type handled_type, Type context_type, java.util.Hashtable parameters) {
        return getRegistry(context).tryGet_(context, handled_type, context_type, parameters);
    }


    static protected java.util.Hashtable makeParameters (CallContext context, Object... arguments) {
        java.util.Hashtable parameters = new java.util.Hashtable();

        if (arguments != null && arguments.length > 0) {
            if (arguments.length % 2 != 0) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Creation of aggregate in AggregateRegistry with variable arguments failed, number of arguments is uneven");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            for (int a=0; a<arguments.length; a+=2) {
                if ( ! (arguments[a] instanceof String)) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Creation of aggregate in AggregateRegistry with variable arguments failed, argument '%(index)' is not a string", "index", a);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
                String name  = (String) (arguments[a]);
                Object value = arguments[a+1];
                parameters.put(name, value != null ? value : new FactorySiteNullParameter(context, null));
            }
        }

        return parameters;
    }

    static public Object get (CallContext context, Type handled_type, Type context_type, Object... arguments) throws DoesNotExist {
        return getRegistry(context).get_(context, handled_type, context_type, makeParameters(context, arguments));
    }

    static public Object tryGet (CallContext context, Type handled_type, Type context_type, Object... arguments) {
        return getRegistry(context).tryGet_(context, handled_type, context_type, makeParameters(context, arguments));
    }

    static public String getAggregate (CallContext context, Type handled_type, Type context_type) throws DoesNotExist {
        return getRegistry(context).getAggregate_(context, handled_type, context_type);
    }

    static public String tryGetAggregate (CallContext context, Type handled_type, Type context_type) {
        return getRegistry(context).tryGetAggregate_(context, handled_type, context_type);
    }

    protected String getAggregate_ (CallContext context, Type handled_type, Type context_type) throws DoesNotExist {
        return oomap.get(context, context_type, handled_type);
    }

    protected String tryGetAggregate_ (CallContext context, Type handled_type, Type context_type) {
        return oomap.tryGet(context, context_type, handled_type);
    }

    protected Object get_ (CallContext context, Type handled_type, Type context_type, java.util.Hashtable parameters) throws DoesNotExist {
        String ctn =  oomap.get(context, context_type, handled_type);
        return createAggregate(context, ctn, parameters);
    }

    protected Object tryGet_ (CallContext context, Type handled_type, Type context_type, java.util.Hashtable parameters) {
        String ctn =  oomap.tryGet(context, context_type, handled_type);
        if (ctn == null) { return null; }
        return createAggregate(context, ctn, parameters);
    }

    protected Object createAggregate(CallContext context, String ctn, java.util.Hashtable parameters) {
        try {
            return FactorySiteGate.createObject(context, ctn, parameters);
        } catch (InvalidCTN ictn) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, ictn, "Aggregate Registry contains invalid ctn '%(ctn)'", "ctn", ctn);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    static public void registerAggregates (CallContext call_context, Configuration config) {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        String entry;
        int entry_number = 0;
        while ((entry = config.get(context, "Aggregate." + ++entry_number, (String) null)) != null) {
            String[] sa = entry.split("\\|", 3);

            try {
                register (context, sa[2], TypeManager.get(context, sa[1]), TypeManager.get(context, sa[0]));
            } catch (com.sphenon.basics.metadata.returncodes.NoSuchClass nsc) {
                cc.throwConfigurationError(context, nsc, "While registering aggregates for '%(configclient)', in entry '%(entry)', one of the types does not exist", "configclient", config.getClientId(context), "entry", entry);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        }
    }

    static public void dump(CallContext context) {
        // ((OOMapImpl_String_Type_Type_)(getRegistry(context).oomap)).dump(context);
    }
}
