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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSourceGeneric_Member
  implements DataSourceGeneric
{
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.DataSourceGeneric_Member"); };

    private Object value;
    private Type type;

    public DataSourceGeneric_Member (CallContext context, Object value, String source_location_info) {
        this.value = value;
        this.type = null;
        this.source_location_info = source_location_info;
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "DataSourceGeneric_Member created for value '%(value)' (%(info))", "value", value, "info", this.getSourceLocationInfo(context)); }
    }

    public Object getValue (CallContext context) {
        return value;
    }

    public void setValue (CallContext context, Object value) {
        if (this.type != null) {
            Type newtype = TypeManager.get(context, value == null ? null : value.getClass());
            if (! newtype.isA(context, this.type)) {
                CustomaryContext cc = CustomaryContext.create((Context) context);
                cc.throwPreConditionViolation(context, FactorySiteStringPool.get(context, "Cannot change DataSourceGeneric_Member type after type is already fixed (new value is not of same type as previous value)"));
            }
            this.type = newtype;
        }
        this.value = value;
    }

    public Object getValueAsObject(CallContext context) {
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "DataSourceGeneric_Member for '%(type)', getValueAsObject (%(info))... - done, result '%(result)'", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "result", value, "info", this.getSourceLocationInfo(context)); }
        return value;
    }

    public Type getType(CallContext context) {
        if (this.type == null) {
            this.type = TypeManager.get(context, value == null ? null : value.getClass());
        }
        return this.type;
    }

    public void compile(CallContext context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable {
        if (! do_not_append_par) {
            pars.append(context, ((TypeImpl) (this.getType(context))).getJavaClassName(context) + " " + var_prefix + "_o = null;");
        }
        code.print(indent + "        " + var_prefix + "_o = ");
        if (value instanceof java.lang.String) {
            code.print("\"");
            String v = (String) value;
            for (int i=0; i<v.length(); i++) {
                switch (v.charAt(i)) {
                    case '"' : code.print("\\\""); break;
                    case '\n': code.print("\\n"); break;
                    default  : code.print(v.charAt(i)); break;
                }
            }
            code.print("\"");
        } else {
            code.print("??? " + value.getClass() + " ???");
        }
        code.println(";");
    }

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }
}
