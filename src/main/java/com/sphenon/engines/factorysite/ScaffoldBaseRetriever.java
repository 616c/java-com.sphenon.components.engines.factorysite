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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.tplinst.*;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Vector;

abstract public class ScaffoldBaseRetriever
  extends ScaffoldGeneric_BaseImpl
{
    static final public Class _class = ScaffoldBaseRetriever.class;

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    protected Vector_ScaffoldParameter_long_ parameters;

    protected Class retrieverclass;
    protected boolean allow_dynamic_type_check;

    protected boolean is_singleton;
    protected boolean have_dynamic_parameters;
    protected Object singleton = null;
    protected Object cached_result;
    protected String cached_result_var;
    protected String cached_retriever_var;
    protected boolean already_visited;
    protected boolean retrieval_done;

    protected Object retrieverinstance;

    protected FactorySiteListener listener;
    protected String oid;
    protected int pass;

    public ScaffoldBaseRetriever (CallContext context, Type type, Class retrieverclass, boolean allow_dynamic_type_check, Type component_type, Vector_ScaffoldParameter_long_ parameters, FactorySiteListener listener, boolean is_singleton, boolean have_dynamic_parameters, FactorySite factory_site, String oid, int pass, Vector<String[]> pre_conditions, Vector<String[]> post_conditions, Vector<String[]> pre_build_scripts, Vector<String[]> post_build_scripts, String source_location_info, String problem_monitor_oid) {
        super(context, factory_site, pre_conditions, post_conditions, pre_build_scripts, post_build_scripts, problem_monitor_oid);

        this.parameters = parameters; // Factory_Vector_ScaffoldParameter_long_ .construct(context);

        this.type = type;
        this.retrieverclass = retrieverclass;
        this.allow_dynamic_type_check = allow_dynamic_type_check;
        this.component_type = component_type;
        this.listener = listener;
        this.is_singleton = is_singleton;
        this.have_dynamic_parameters = have_dynamic_parameters;
        this.oid = oid;
        this.pass = pass;

        this.source_location_info = source_location_info;

        this.reset(context);
    }

    public Class getConstructionClass(CallContext context) {
        return this.retrieverclass;
    }

    public String getOID(CallContext context) {
        return this.oid;
    }

    public void reset(CallContext context) {
        super.reset(context);
        this.cached_result = null;
        this.cached_result_var = null;
        this.cached_retriever_var = null;
        this.already_visited = false;
        this.retrieverinstance = null;
        this.retrieval_done = false;
    }

    abstract public boolean hasVariableSignature (CallContext context);

    public Vector_ScaffoldParameter_long_ getParameters (CallContext context) {
        return this.parameters;
    }

    public Type getType (CallContext context) {
        return this.type;
    }

    public int getPass(CallContext context) {
        return this.pass;
    }

    abstract protected Object getRetrieverInstance(CallContext context);

    abstract protected Object preretrieveInstance(CallContext context);

    abstract protected Object retrieveInstance(CallContext context);

    abstract protected String getScaffoldId(CallContext context);

    abstract protected boolean canPreretrieve(CallContext context);

    public Object getValueAsObject (CallContext call_context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        if (this.skip) { return null; }

        Context context = Context.create(call_context);
        RuntimeStep runtime_step = null;

        try {
            if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Building subaggregate at '%(info)' using retriever '%(retriever)'", "info", source_location_info, "retriever", retrieverclass.getName()); }

            boolean first_invocation = true;
            
            if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "'%(scaffoldid)', getValueAsObject (%(info)), cache '%(cache)', visited '%(visited)', retrieved '%(retrieved)', current pass '%(currentpass)', scaffold pass '%(scaffoldpass)'...", "scaffoldid", this.getScaffoldId(context), "cache", this.cached_result, "visited", this.already_visited, "retrieved", this.retrieval_done, "currentpass", this.factory_site.getCurrentPass(context), "scaffoldpass", this.pass, "info", this.getSourceLocationInfo(context)); }
            
            boolean just_preretrieve = (this.pass > this.factory_site.getCurrentPass(context));
            
            if (this.cached_result != null && (this.already_visited || this.retrieval_done || just_preretrieve)) {
                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "'%(scaffoldid)', getValueAsObject (%(info)) - done, result (cached): '%(result)'", "scaffoldid", this.getScaffoldId(context), "info", this.getSourceLocationInfo(context), "result", this.cached_result); }
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                this.optionallyAttachProblemsToInstance(context, this.cached_result);
                return this.cached_result;
            }
            if (this.already_visited || just_preretrieve) {
                if (this.already_visited) { first_invocation = false; }
                if (this.canPreretrieve(context) == false) {
                    DataSourceUnavailable.createAndThrow(context, FactorySiteStringPool.get(context, "0.8.7" /* Object of type '%(type)' cannot be delivered, object aggregate contains a cyclic reference and factory does not provide a 'preretrieve' method */), "type", this.type.getName(context));
                    throw (DataSourceUnavailable) null; // compiler insists
                }
                
                Object result = preretrieveInstance(context);
                this.cached_result = result;
                
                if (    this.allow_dynamic_type_check
                     && result != null
                     && TypeManager.get(context, result.getClass()).isA(context, this.type) == false
                   ) {
                    DataSourceUnavailable.createAndThrow(context, "Dynamic type check failed in '%(scaffoldid)', expected '%(expected)', got '%(got)'", "scaffoldid", this.getScaffoldId(context), "expected", this.type.getName(context), "got", result.getClass().getName());
                    throw (DataSourceUnavailable) null; // compiler insists
                }

                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "'%(scaffoldid)', getValueAsObject (%(info)) - done, result (preretrieved): '%(result)'", "scaffoldid", this.getScaffoldId(context), "info", this.getSourceLocationInfo(context), "result", result); }
                
                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                this.optionallyAttachProblemsToInstance(context, result);
                return result;
            } else {
                if (this.is_singleton && this.singleton != null) {
                    this.already_visited = true;
                    if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "'%(scaffoldid)', getValueAsObject (%(info)) - done, result (singleton): '%(result)'", "scaffoldid", this.getScaffoldId(context), "info", this.getSourceLocationInfo(context), "result", this.singleton); }
                    if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                    this.optionallyAttachProblemsToInstance(context, this.singleton);
                    return this.singleton;
                }
                if (listener != null) listener.notifyBeforeCreation(context, this);
                this.already_visited = true;
                
                this.getRetrieverInstance(context);
                
                this.setParameters(context);
                
                this.performPreBuildActions(context);
                
                Object result = retrieveInstance(context);
                
                this.performPostBuildActions(context, result);
                
                if (this.is_singleton) { this.singleton = result; }
                this.cached_result = result;
                this.retrieval_done = true;
                if (listener != null) listener.notifyAfterCreation(context, this, result);
                
                if (    this.allow_dynamic_type_check
                     && result != null
                     && TypeManager.get(context, result.getClass()).isA(context, this.type) == false
                   ) {
                    DataSourceUnavailable.createAndThrow(context, "Dynamic type check failed in '%(scaffoldid)', expected '%(expected)', got '%(got)'", "scaffoldid", this.getScaffoldId(context), "expected", this.type.getName(context), "got", result.getClass().getName());
                    throw (DataSourceUnavailable) null; // compiler insists
                }

                if ((notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendDiagnostics(context, "'%(scaffoldid)', getValueAsObject (%(info)) - done, result: '%(result)'", "scaffoldid", this.getScaffoldId(context), "info", this.getSourceLocationInfo(context), "result", result); }

                if (runtime_step != null) { runtime_step.setCompleted(context, "Aggregate successfully built"); runtime_step = null; }

                this.optionallyAttachProblemsToInstance(context, result);
                return result;
            }
        } catch(DataSourceUnavailable exception) {
            if (runtime_step != null) { runtime_step.setFailed(context, exception, "Aggregate build failed"); runtime_step = null; }
            this.handleProblem(context, exception);
            throw (DataSourceUnavailable) null; // compiler insists
        } catch(ExceptionError exception) {
            if (runtime_step != null) { runtime_step.setFailed(context, exception, "Aggregate build failed"); runtime_step = null; }
            this.handleProblem(context, exception);
            throw (ExceptionError) null; // compiler insists
        } catch(Error exception) {
            if (runtime_step != null) { runtime_step.setFailed(context, exception, "Aggregate build failed"); runtime_step = null; }
            this.handleProblem(context, exception);
            throw (ExceptionError) null; // compiler insists
        } catch(RuntimeException exception) {
            if (runtime_step != null) { runtime_step.setFailed(context, exception, "Aggregate build failed"); runtime_step = null; }
            this.handleProblem(context, exception);
            throw (ExceptionError) null; // compiler insists
        }
    }

    abstract public void setParameters(CallContext context) throws DataSourceUnavailable;
}
