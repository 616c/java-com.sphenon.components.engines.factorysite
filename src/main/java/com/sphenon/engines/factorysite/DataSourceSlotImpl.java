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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSourceSlotImpl
    implements DataSourceSlot
{
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.factorysite.DataSourceSlotImpl"); };


    private DataSource data_source;
    private Type type;

    public DataSourceSlotImpl(CallContext context, Type type, String source_location_info) {
        this.type = type;
        this.data_source = null;
        this.source_location_info = source_location_info;
    }

    public boolean isValid(CallContext context) {
        return (    this.data_source != null
                 && (    (this.data_source instanceof DataSourceSlot) == false
                      || ((DataSourceSlot)this.data_source).isValid(context)
                    )
               );
    }

    public Type getType(CallContext context) {
        return this.data_source != null ? this.data_source.getType(context) : (this.type != null ? this.type : null);
    }

    public void setType(CallContext call_context, Type type) {
        if (this.getType(call_context) != null && (type == null || ! type.isA(call_context, this.getType(call_context)))) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwPreConditionViolation(context, FactorySiteStringPool.get(context, "0.4.2" /* Cannot change DataSourceSlot type after type is already fixed */));
        }
        this.type = type;
    }

    public void setDataSource(CallContext call_context, DataSource data_source) throws TypeMismatch {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);

        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Plugging datasource '%(otherds)' (%(otherinfo)) of type '%(othertype)' into '%(ds)' (%(info)) of type '%(type)'", "otherds", data_source, "otherinfo", data_source == null ? "" : data_source.getSourceLocationInfo(context), "othertype", data_source == null ? "" : data_source.getType(context), "ds", this, "info", this.getSourceLocationInfo(context), "type", this.type); }

        if (data_source != null) {
            if (this.type != null) {
                if (data_source.getType(context) == null) {
                    cc.throwPreConditionViolation(context, "Cannot plug DataSource with unspecified type into DataSourceSlot of type '%(type)'", "type", this.type.getName(context));
                    throw (ExceptionPreConditionViolation) null;
                }

                if (    data_source.getType(context).isA(context, this.type) == false
                     && (    (data_source.getType(context) instanceof JavaType) == false
                          || (this.type instanceof TypeParametrised) == false
                          || (((TypeParametrised) this.type).getBaseType(context) instanceof JavaType) == false 
                          || ((JavaType) (((TypeParametrised) this.type).getBaseType(context))).getJavaClass(context).isAssignableFrom(((JavaType) data_source.getType(context)).getJavaClass(context)) == false
                        )
                   ) {
                    TypeMismatch.createAndThrow (context, FactorySiteStringPool.get(context, "0.4.1" /* DataSourceSlot of type '%(slottype)' rejects DataSource of type '%(type)' */), "slottype", this.type.getName(context), "type", data_source.getType(context).getName(context));
                }
            }
        }

        this.data_source = data_source;
    }

    public DataSource getDataSource(CallContext call_context) {
        if (this.data_source == null) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwProtocolViolation(context, FactorySiteStringPool.get(context, "0.4.0" /* DataSourceSlot contains no valid DataSource yet (null pointer) */));
        }
        return this.data_source;
    }

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }
}
