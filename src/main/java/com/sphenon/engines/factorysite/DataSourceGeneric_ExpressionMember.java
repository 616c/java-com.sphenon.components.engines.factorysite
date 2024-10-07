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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSourceGeneric_ExpressionMember
  implements DataSourceGeneric
{
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.DataSourceGeneric_ExpressionMember"); };

    private Expression expression;
    private FactorySite factory_site;

    public DataSourceGeneric_ExpressionMember (CallContext context, String expression, FactorySite factory_site, Type type, String source_location_info) {
        this.expression = new Expression(context, expression, factory_site.getDefaultEvaluator(context));
        this.factory_site = factory_site;
        this.type = type;
        this.source_location_info = source_location_info;
    }

    static protected Type string_type;

    public Object getValue (CallContext context) throws DataSourceUnavailable {
        try {
            Object value = this.expression.evaluate(context, this.factory_site.getCurrentScope(context));
            if (    this.type != null
                 && this.type.equals(string_type == null ? (string_type = TypeManager.get(context, String.class)) : string_type)
                 && value != null
                 && (value instanceof String) == false) {
                value = ContextAware.ToString.convert(context, value);
            }
            return value;
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of expression '%(expression)' failed", "expression", this.expression.getExpression(context));
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public Object getValueAsObject(CallContext context) throws DataSourceUnavailable {
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "DataSourceGeneric_ExpressionMember for '%(type)', getValueAsObject (%(info)) from dynamic string '%(dyns)'...", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "dyns", expression.getExpression(context)); }
        Object result = getValue(context);
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "DataSourceGeneric_ExpressionMember for '%(type)', getValueAsObject (%(info)) - done, result: '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context), "result", result); }
        return result;
    }

    protected Type type;

    public Type getType (CallContext context) {
        return this.type;
    }

    public void setType (CallContext context, Type type) {
        this.type = type;
    }

    public void compile(CallContext context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable {
        if (! do_not_append_par) {
            pars.append(context, ((TypeImpl) (this.getType(context))).getJavaClassName(context) + " " + var_prefix + "_o = null;");
        }
        code.print(indent + "        " + var_prefix + "_o = new Expression(context, \"");
        String dst = this.expression.getExpression(context);
        for (int i=0; i<dst.length(); i++) {
            switch (dst.charAt(i)) {
                case '"' : code.print("\\\""); break;
                case '\n': code.print("\\n"); break;
                default  : code.print(dst.charAt(i)); break;
            }
        }
        code.print("\", \"js\").get(context/*, der scope... */);");
    }

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }
}
