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
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;

public class SpecificScaffoldFactory_AggregateFactory implements SpecificScaffoldFactory, ContextAware {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean goals_enabled;
    static {
        notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_AggregateFactory");
        goals_enabled = GoalLocationContext.getGoalsEnabled(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.SpecificScaffoldFactory_AggregateFactory");
    };

    protected Type type;
    protected TypeImpl_Aggregate aggregate_type;
    protected boolean allow_dynamic_type_check;
    protected FactorySite fs;
    protected DataSource ds;
    protected String build_string;

    protected java.util.Hashtable par_entries;
    protected Vector<ParEntry> formal_scaffold_parameters;

    public Vector<ParEntry> getFormalScaffoldParameters (CallContext context) {
        if (this.formal_scaffold_parameters == null) {
            try {
                initialise(context);
            } catch (InvalidFactory ifac) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ifac, "Cannot retrieve formal parameters for scaffold, underlying factory is invalid");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }

            this.formal_scaffold_parameters = new Vector<ParEntry>();

            Map_DataSourceConnector_String_ mdscs = fs.getParameters(context);
            for (IteratorItemIndex_DataSourceConnector_String_ iiidscs = mdscs.getNavigator(context);
                 iiidscs.canGetCurrent(context);
                 iiidscs.next(context)) {
                DataSourceConnector dsc = iiidscs.tryGetCurrent(context);
                String parname = iiidscs.tryGetCurrentIndex(context);
                Type partype = dsc.getType(context);
                boolean has_default = (    dsc instanceof DataSourceConnector_Parameter
                                        && ((DataSourceConnector_Parameter) dsc).isOptional(context));
                boolean is_static   = (    dsc instanceof DataSourceConnector_Parameter
                                        && ((DataSourceConnector_Parameter) dsc).isStatic(context));
                if (is_static) {
                    continue;
                }

                ParEntry par_entry = new ParEntry(partype, parname, has_default);

                formal_scaffold_parameters.add(par_entry);
            }
        }
        return this.formal_scaffold_parameters;
    }

    public Type getComponentTypeOfCollection(CallContext context) {
        return null;
    }

    protected String type_context;

    public String getTypeContext(CallContext context) {
        return this.type_context;
    }

    public String getBuildString(CallContext context) {
        if (is_initialised == false) { return this.build_string; }
        return "AggregateFactory|"
            + this.type.getId(context) + "|"
            + this.aggregate_type.getId(context) + "|"
            + this.allow_dynamic_type_check + "|"
            + this.type_context;
    }

    static public SpecificScaffoldFactory_AggregateFactory buildFromString(CallContext context, String build_string) {
        String[] args = build_string.split("\\|");
        Type type                        = TypeManager.tryGetById(context, args[1]);
        Type aggregate_type              = TypeManager.tryGetById(context, args[2]);
        boolean allow_dynamic_type_check = new Boolean(args[3]);
        String type_context              = args[4];
        try {
            context = Context.create(context);
            TypeContext tc = TypeContext.create((Context)context);
            tc.setSearchPathContext(context, type_context);
            return new SpecificScaffoldFactory_AggregateFactory(context, type, (TypeImpl_Aggregate) aggregate_type, allow_dynamic_type_check, false, build_string, type_context);
        } catch (InvalidFactory ifac) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ifac, "Could rebuild scaffold factory from string");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    public SpecificScaffoldFactory_AggregateFactory (CallContext context, Type type, TypeImpl_Aggregate aggregate_type, boolean allow_dynamic_type_check) throws InvalidFactory {
        this(context, type, aggregate_type, allow_dynamic_type_check, true, null, null);
    }

    protected SpecificScaffoldFactory_AggregateFactory (CallContext context, Type type, TypeImpl_Aggregate aggregate_type, boolean allow_dynamic_type_check, boolean do_initialise, String build_string, String type_context) throws InvalidFactory {
        this.allow_dynamic_type_check = allow_dynamic_type_check;

        while (type instanceof TypeParametrised) {
            type = ((TypeParametrised) type).getBaseType(context);
        }
        this.type = type;

        this.aggregate_type = aggregate_type;

        this.build_string = build_string;
        if (type_context != null) {
            this.type_context = type_context;
        }
        if (do_initialise) {
            initialise(context);
        }
    }

    public String toString(CallContext context) {
        return "Scaffold factory (O) '" + aggregate_type.getName(context) + "'";
    }

    protected volatile boolean is_initialised;

    protected void initialise (CallContext call_context) throws InvalidFactory {
        if (is_initialised == false) {
            synchronized(this) {
                if (is_initialised == false) {
                    is_initialised = true;

                    Context context = Context.create(call_context);
                    CustomaryContext cc = CustomaryContext.create(context);

                    this.par_entries = new java.util.Hashtable();

                    TypeContext tc = TypeContext.get((Context)context);
                    this.type_context = tc.getSearchPathContext(context);

                    Factory_Aggregate fa = new Factory_Aggregate(context);
                    fa.setAggregateClass(context, aggregate_type.getName(context));
                    try {
                        fa.validateAggregateClass(context);
                    } catch (ValidationFailure vf) {
                        InvalidFactory.createAndThrow(context, vf, "Invalid aggregate factory for aggregate type '%(aggregate_type)'", "aggregate_type", aggregate_type.getName(context));
                        throw (InvalidFactory) null;
                    }
                    this.fs = fa.getFactorySite(context);
                    this.ds = fs.getMainDataSource (context);
                }
            }
        }
    }

    public MatchResult isMatching (CallContext call_context, Type actual_matched_type, Vector_ScaffoldParameter_long_ parameters, Map_TypeOrNull_String_ parameters_by_name, boolean allow_missing_arguments) throws InvalidFactory {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        initialise(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Is matching? ('%(aggregate_type)' [AF])", "aggregate_type", this.aggregate_type); }

        List actpars_info = null;
        List forpars_info = null;
        List defpars_info = null;
        List setpars_info = null;
        List ignpars_info = null;
        Vector_ParEntry_long_ parameters_to_be_defaulted = null;
        Vector_ParEntry_long_ parameters_to_be_set = null;

        Map_DataSourceConnector_String_ mdscs = fs.getParameters(context);
        Type return_type = ds.getType(context);

        int non_applicable_count = 0;
        Hashtable non_applicable_ones = null;
        List napars_info = null;
        for (ScaffoldParameter sp : parameters.getIterable_ScaffoldParameter_(context)) {
            if (sp.getAppliesTo(context) != null) {
                boolean does_apply = false;
                for (Type t : sp.getAppliesTo(context)) {
                    if (t.equals(this.aggregate_type)) {
                        does_apply = true;
                        break;
                    }
                }
                if (does_apply == false) {
                    non_applicable_count++;
                    if (non_applicable_ones == null) {
                        non_applicable_ones = new Hashtable();
                    }
                    String name = sp.getName(context);
                    non_applicable_ones.put(name, sp);
                    if (napars_info == null) {
                        napars_info = new LinkedList();
                    }
                    napars_info.add(name);
                }
            }
        }

        int unnamed_count = 0;

        for (IteratorItemIndex_DataSourceConnector_String_ iiidscs = mdscs.getNavigator(context);
             iiidscs.canGetCurrent(context);
             iiidscs.next(context)) {
            DataSourceConnector dsc = iiidscs.tryGetCurrent(context);
            String parname = iiidscs.tryGetCurrentIndex(context);
            Type partype = dsc.getType(context);
            boolean has_default = (    dsc instanceof DataSourceConnector_Parameter
                                    && ((DataSourceConnector_Parameter) dsc).isOptional(context));
            boolean is_static   = (    dsc instanceof DataSourceConnector_Parameter
                                    && ((DataSourceConnector_Parameter) dsc).isStatic(context));
            if (is_static) {
                continue;
            }

            ParEntry parentry = new ParEntry(partype, parname, has_default);

            if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                if (forpars_info == null) {
                    forpars_info = new LinkedList();
                }
                forpars_info.add(parname);
            }

            if (parameters_by_name == null) {
                MessageText mt = MessageText.create(context, "No, cannot identify parameters by name (due to duplicates)");
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                    cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                }
                return new MatchResult(mt, this);
            }

            boolean using_unnamed_entry = false;

            TypeOrNull actual_partype_entry = (    non_applicable_ones != null
                                                && non_applicable_ones.get(parname) != null
                                              ) ? null : parameters_by_name.tryGet(context, parname);
            if (    actual_partype_entry == null
                 && unnamed_count == 0
               ) {
                actual_partype_entry = parameters_by_name.tryGet(context, "");
                using_unnamed_entry = true;
            }
            if (actual_partype_entry == null) {
                if (has_default == false) {
                    if (allow_missing_arguments == false) {
                        MessageText mt = MessageText.create(context, "No, no actual parameter and no default value for '%(parname)'", "parname", parname);
                        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                            cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                        }
                        return new MatchResult(mt, this);
                    } else {
                        if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                            if (ignpars_info == null) {
                                ignpars_info = new LinkedList();
                            }
                            ignpars_info.add(parname);
                        }
                    }
                } else {
                    if (parameters_to_be_defaulted == null) {
                        parameters_to_be_defaulted = Factory_Vector_ParEntry_long_.construct(context);
                    }
                    parameters_to_be_defaulted.append(context, parentry);
                    if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                        if (defpars_info == null) {
                            defpars_info = new LinkedList();
                        }
                        defpars_info.add(parname);
                    }
                }
            } else {
                Type actual_partype = actual_partype_entry.type;

                if (parameters_to_be_set == null) {
                    parameters_to_be_set = Factory_Vector_ParEntry_long_.construct(context);
                }
                parameters_to_be_set.append(context, parentry);

                if (actual_partype != null && ! actual_partype.isA(context, partype)) {
                    MessageText mt = MessageText.create(context, "No, actual type ('%(acttype)') and formal type ('%(fortype)') of parameter '%(parname)' do not match", "parname", parname, "acttype", actual_partype, "fortype", partype);
                    if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                        cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
                    }
                    return new MatchResult(mt, this);
                }
                if ((this.notification_level & Notifier.OBSERVATION) != 0) {
                    if (setpars_info == null) {
                        setpars_info = new LinkedList();
                    }
                    setpars_info.add(parname);
                }

                if (using_unnamed_entry) { unnamed_count++; }
            }
        }
        if ((this.notification_level & Notifier.OBSERVATION) != 0) {
            for (Iterator_ScaffoldParameter_ isp = parameters.getNavigator(context);
                 isp.canGetCurrent(context);
                 isp.next(context)) {
                if (actpars_info == null) {
                    actpars_info = new LinkedList();
                }
                actpars_info.add(isp.tryGetCurrent(context).getName(context));
            }
        }
        if (     (parameters_to_be_set == null ? 0 : parameters_to_be_set.getSize(context))
              != (parameters.getSize(context) - non_applicable_count)) {
            MessageText mt = MessageText.create(context, "No, number of actual parameters %(nract):'(%(actpars))' minus number of not applicable ones %(nrna):'(%(napars))' plus number of parameters to be defaulted %(nrdef):'(%(defpars))' differs from formal ones %(nrfor):'(%(forpars))'", "actpars", actpars_info, "forpars", forpars_info, "napars", napars_info, "defpars", defpars_info, "nract", t.s(parameters.getSize(context)), "nrna", t.s(non_applicable_count), "nrdef", t.s(parameters_to_be_defaulted == null ? 0 : parameters_to_be_defaulted.getSize(context)), "nrfor", t.s(parameters_to_be_set == null ? 0 : parameters_to_be_set.getSize(context)));
            if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) {
                cc.sendTrace(context, Notifier.DIAGNOSTICS, mt);
            }
            return new MatchResult(mt, this);
        }

        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Actual parameters: (%(actpars))", "actpars", actpars_info); }
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Formal parameters: (%(forpars))", "forpars", forpars_info); }
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameters to be set: (%(setpars))", "setpars", setpars_info); }
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameters to be defaulted: (%(defpars))", "defpars", defpars_info); }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Parameters to be ignored: (%(ignpars))", "ignpars", ignpars_info); }
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.DIAGNOSTICS, "Yes"); }
        return new MatchResult(actual_matched_type, parameters_to_be_set, parameters_to_be_defaulted);
    }

    public Scaffold create (CallContext context, Vector_ScaffoldParameter_long_ parameters, MatchResult match_result, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) throws InvalidFactory {

        initialise(context);

        return new ScaffoldGenericFactory_Aggregate(context, this.type, this.aggregate_type, this.allow_dynamic_type_check, this.par_entries, parameters, match_result, listener, is_singleton, have_dynamic_parameters, factory_site, oid, pass, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, source_location_info, problem_monitor_oid);
    }
}
