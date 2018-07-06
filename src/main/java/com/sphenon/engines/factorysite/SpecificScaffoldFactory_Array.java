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

public class SpecificScaffoldFactory_Array implements SpecificScaffoldFactory, ContextAware {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean goals_enabled;
    static {
        notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Array");
        goals_enabled = GoalLocationContext.getGoalsEnabled(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_Array");
    };

    protected Type type;
    protected Type component_type = null;
    protected String build_string;

    protected String type_context;

    public Vector<ParEntry> getFormalScaffoldParameters (CallContext context) {
        return null;
    }

    public Type getComponentTypeOfCollection(CallContext context) {
        return this.component_type;
    }

    public String getTypeContext(CallContext context) {
        return this.type_context;
    }

    public String getBuildString(CallContext context) {
        return "Array|"
            + this.type.getId(context) + "|"
            + this.type_context;
    }

    static public SpecificScaffoldFactory_Array buildFromString(CallContext context, String build_string) {
        String[] args = build_string.split("\\|");
        Type type                        = TypeManager.tryGetById(context, args[1]);
        String type_context              = args[2];
        try {
            context = Context.create(context);
            TypeContext tc = TypeContext.create((Context)context);
            tc.setSearchPathContext(context, type_context);
            return new SpecificScaffoldFactory_Array(context, type, false, build_string, type_context);
        } catch (InvalidFactory ifac) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ifac, "Could rebuild scaffold factory from string");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    public SpecificScaffoldFactory_Array (CallContext context, Type type) throws InvalidFactory {
        this(context, type, true, null, null);
    }

    protected SpecificScaffoldFactory_Array (CallContext call_context, Type type, boolean do_initialise, String build_string, String type_context) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        this.type = type;

        this.build_string = build_string;
        if (type_context != null) {
            this.type_context = type_context;
        } else {
            TypeContext tc = TypeContext.get((Context)context);
            this.type_context = tc.getSearchPathContext(context);
        }
        this.component_type = TypeManager.get(context, TypeManager.getJavaClass(context, this.type).getComponentType());
    }

    public String toString(CallContext context) {
        return "Scaffold factory (A) '" + type.getName(context) + "[]'";
    }

    public MatchResult isMatching (CallContext call_context, Type actual_matched_type, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_missing_arguments) {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Is matching? ('%(type)' [A])", "type", this.type); }
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Yes"); }

        return new MatchResult(actual_matched_type);
    }

    public Scaffold create (CallContext context, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory {
        return new ScaffoldGenericArray(context, this.type, this.component_type, parameters, match_result, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }
}
