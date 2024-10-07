

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
package com.sphenon.engines.factorysite;

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSourceConnector
    extends DataSourceSlotImpl
    implements DataSource
{
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.DataSourceConnector"); };

    public DataSourceConnector (CallContext context, Type type, String source_location_info) {
        super(context, type, source_location_info);
    }
    
    public Object getValueAsObject(CallContext context) throws DataSourceUnavailable, IgnoreErraneousDataSource {
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.DIAGNOSTICS, "DataSourceConnector for '%(type)', getValueAsObject (%(info)), delegating to data source...", "type", this.getType(context) == null ? "(null)" : this.getType(context).getName(context), "info", this.getSourceLocationInfo(context)); }
        return this.getDataSourceOrDefault(context).getValueAsObject(context);
    }

//     public void compile(CallContext context, java.io.PrintStream code, String var_prefix, String indent, Vector_String_long_ pars, boolean do_not_append_par) throws DataSourceUnavailable {
//         this.getDataSource(context).compile(context, code, var_prefix, indent, pars, do_not_append_par);
//     }
}
