package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.factories.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.factory.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;

public class FactorySitePreloader {
    static protected boolean initialised = false;

    static {
        initialise(RootContext.getRootContext());
    }

    static public void initialise (CallContext call_context) {
        if (initialised == false) {
            initialised = true;
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            TypeContext tc = TypeContext.create(context);
            tc.setSearchPathContext(context, "com.sphenon.engines.factorysite");

            ScaffoldFactory sf = FactorySiteContext.getScaffoldFactory(context);
            try {
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.variatives.VariativeString.class),
                                           TypeManager.get(context, com.sphenon.basics.variatives.factories.Factory_VariativeString.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.variatives.tplinst.Variative_String_.class),
                                           TypeManager.get(context, com.sphenon.basics.variatives.factories.Factory_VariativeString.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.variatives.VariativeString.class),
                                           TypeManager.get(context, com.sphenon.basics.variatives.factories.Factory_VariativeStringTrivial.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.variatives.tplinst.Variative_String_.class),
                                           TypeManager.get(context, com.sphenon.basics.variatives.factories.Factory_VariativeStringTrivial.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.variatives.VariativeString.class),
                                           TypeManager.get(context, com.sphenon.basics.variatives.factories.Factory_VariativeStringDynamic.class),
                                           false);                                                          
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.variatives.tplinst.Variative_String_.class),
                                           TypeManager.get(context, com.sphenon.basics.variatives.factories.Factory_VariativeStringDynamic.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.metadata.Type.class),
                                           TypeManager.get(context, com.sphenon.basics.metadata.factories.Factory_Type.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.locating.Locator.class),
                                           TypeManager.get(context, com.sphenon.basics.locating.factories.Factory_Locator.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.locating.Location.class),
                                           TypeManager.get(context, com.sphenon.basics.locating.factories.Factory_Location.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.locating.Location.class),
                                           TypeManager.get(context, com.sphenon.basics.locating.factories.Factory_LocationVolatile.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.locating.Location.class),
                                           TypeManager.get(context, com.sphenon.basics.locating.factories.Factory_Location_ByTextLocator.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.locating.Location.class),
                                           TypeManager.get(context, com.sphenon.basics.locating.factories.Factory_LocationVolatile_ByTextLocatorTemplate.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.get(context, com.sphenon.basics.locating.Location.class),
                                           TypeManager.get(context, com.sphenon.basics.locating.factories.Factory_Location_ByPrototype.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Boolean.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_boolean_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Byte.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_byte_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Short.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_short_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Integer.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_int_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Long.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_long_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Float.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_float_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Double.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_double_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, String.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_String_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, Class.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_Class_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, java.util.Date.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_Date_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, com.sphenon.basics.retriever.Time.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_Time_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, com.sphenon.basics.retriever.Meter.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_Meter_.class),
                                           false);
                sf.preloadScaffoldFactory (context,
                                           TypeManager.getParametrised(context, com.sphenon.basics.retriever.GenericFilter.class, TypeManager.get(context, com.sphenon.basics.retriever.TrafficLight.class)),
                                           TypeManager.get(context, com.sphenon.basics.retriever.tplinst.Factory_Filter_TrafficLight_.class),
                                           false);
            } catch (InvalidFactory ifac) {
                cc.throwConfigurationError(context, ifac, "Could not preload factory");
                throw (ExceptionConfigurationError) null;
            }
        }
    }
}
