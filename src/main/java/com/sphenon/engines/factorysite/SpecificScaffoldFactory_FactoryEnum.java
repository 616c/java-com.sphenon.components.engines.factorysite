package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.goal.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;

import java.util.Hashtable;
import java.util.Vector;

public class SpecificScaffoldFactory_FactoryEnum extends SpecificScaffoldFactory_Factory {

    public int getPriority(CallContext context) {
        return 3;
    }

    public String getBuildString(CallContext context) throws InvalidFactory {
        initialise(context);
        return "FactoryEnum|"
            + this.type.getId(context) + "|"
            + this.factory_type.getId(context) + "|"
            + (this.method_name == null ? "" : this.method_name) + "|"
            + this.allow_dynamic_type_check + "|"
            + this.type_context;
    }

    static public SpecificScaffoldFactory_FactoryEnum buildFromString(CallContext context, String build_string) {
        String[] args = build_string.split("\\|");
        Type type                        = TypeManager.tryGetById(context, args[1]);
        Type factory_type                = TypeManager.tryGetById(context, args[2]);
        String method_name               = (args[3] == null || args[3].length() == 0 ? null : args[3]);
        boolean allow_dynamic_type_check = new Boolean(args[4]);
        String type_context              = args[5];
        context = Context.create(context);
        TypeContext tc = TypeContext.create((Context)context);
        tc.setSearchPathContext(context, type_context);
        try {
            return new SpecificScaffoldFactory_FactoryEnum(context, type, factory_type, method_name, allow_dynamic_type_check, false, build_string, type_context);
        } catch (InvalidFactory ifac) { return null; /* cannot happen */ }
    }

    public String toString(CallContext context) {
        return "Scaffold factory (E) '" + factory_type.getName(context) + "'";
    }

    public SpecificScaffoldFactory_FactoryEnum (CallContext context, Type type, Type factory_type, String method_name, boolean allow_dynamic_type_check) throws InvalidFactory {
        this(context, type, factory_type, method_name, allow_dynamic_type_check, true, null, null);
    }

    protected SpecificScaffoldFactory_FactoryEnum (CallContext context, Type type, Type factory_type, String method_name, boolean allow_dynamic_type_check, boolean do_initialise, String build_string, String type_context) throws InvalidFactory {
        super(context, type, factory_type, method_name, allow_dynamic_type_check, do_initialise, build_string, type_context, null);
    }

    public Scaffold create (CallContext context, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory {
        return new ScaffoldGenericFactoryEnum(context, this, this.type, this.factoryclass, this.allow_dynamic_type_check, this.create_method, this.precreate_method, this.set_parameters_at_once, this.component_type, this.par_entries, this.new_instance_method, this.constructor, this.cons_context_par, parameters, match_result, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }
}
