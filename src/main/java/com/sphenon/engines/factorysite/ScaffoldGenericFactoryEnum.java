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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.factories.*;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Vector;

public class ScaffoldGenericFactoryEnum
  extends ScaffoldGenericFactory
{
    public ScaffoldGenericFactoryEnum (CallContext call_context, SpecificScaffoldFactory_FactoryEnum scaffold_factory, Type type, Class factoryclass, boolean allow_dynamic_type_check, Method create_method, Method precreate_method, Method set_parameters_at_once, Type component_type, java.util.Hashtable par_entries, Method new_instance_method, Constructor constructor, boolean cons_context_par, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory {
        super(call_context, scaffold_factory, type, factoryclass, allow_dynamic_type_check, create_method, precreate_method, set_parameters_at_once, null, component_type, par_entries, new_instance_method, constructor, cons_context_par, parameters, match_result, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }

    protected Object getFactoryInstance(CallContext context) {
        FactoryEnum factory = (FactoryEnum) super.getFactoryInstance(context);

        TypeImpl ti;
        try {
            ti = (TypeImpl) type;
        } catch (ClassCastException cce) {
            CustomaryContext.create((Context)context).throwInvalidState(context, "Enumeration type in factorysite was not represented by TypeManager Java wrapper (TypeImpl)");
            throw (ExceptionInvalidState) null; // compiler insists
        }

        factory.attachEnumClass(context, ti.getJavaClass(context));

        return factory;
    }
}
