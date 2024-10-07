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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.Vector;

public class DataSource_Switch
    implements DataSource
{
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.DataSource_Switch"); };

    protected Vector<DataSource> cases;
    protected Vector<Expression> conditions;
    protected Type type;
    protected FactorySite factory_site;

    public DataSource_Switch (CallContext context, Type type, String source_location_info, FactorySite factory_site) {
        this.source_location_info = source_location_info;
        this.type = type;
        this.cases = new Vector<DataSource>();
        this.conditions = new Vector<Expression>();
        this.factory_site = factory_site;
    }

    public void addCase(CallContext context, DataSource ds, String condition) {
        if (this.getType(context) != null && ds.getType(context) != null && ! ds.getType(context).isA(context, this.getType(context))) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Type '%(casetype)' of CASE in OCP SWITCH does not correspond to SWITCH type '%(switchtype)'", "casetype", ds.getType(context).getName(context), "switchtype", this.getType(context).getName(context));
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        this.cases.add(ds);
        this.conditions.add(new Expression(context, condition == null ? "true" : condition, "jspp"));
    }
    
    public Type getType(CallContext context) {
        return type;
    }

    protected boolean o2b(CallContext context, Object o) {
        try {
            return (Boolean) o;
        } catch (ClassCastException cce) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, cce, "If condition in OCP SWITCH does not evaluate to a Boolean instance, but to a '%(got)'", "got", o.getClass());
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    public Object getValueAsObject(CallContext context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "DataSource_Switch for '%(type)', getValueAsObject (%(info))...", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context)); }

        int casenbr;
        Object result = null;
        for (casenbr=0; casenbr<cases.size(); casenbr++) {
            boolean condition_is_true = false;
            try {
                condition_is_true = o2b(context, conditions.get(casenbr).evaluate(context, this.factory_site.getCurrentScope(context)));
            } catch (EvaluationFailure ef) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed", "expression", conditions.get(casenbr).getExpression(context));
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            if (condition_is_true) {
                result = cases.get(casenbr).getValueAsObject(context);
                if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "DataSource_Switch for '%(type)', getValueAsObject (%(info)) - done, result from case %(case): '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "case", casenbr, "result", result, "info", this.getSourceLocationInfo(context)); }
                return result;
            }
        }

        CustomaryContext.create((Context)context).throwConfigurationError(context, "In OCP SWITCH, no CASE applied - '%(type)', getValueAsObject (%(info))", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context));
        throw (ExceptionConfigurationError) null; // compiler insists
    }

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }
}
