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

    protected DataSource data_source;
    protected DataSource default_value_ds;
    protected Type       type;

    public DataSourceSlotImpl(CallContext context, Type type, String source_location_info) {
        this.type = type;
        this.data_source = null;
        this.source_location_info = source_location_info;
    }

    public boolean isValid(CallContext context) {
        return ( this.data_source != null
                  ? (    (this.data_source instanceof DataSourceSlot) == false
                      || ((DataSourceSlot) this.data_source).isValid(context)
                    )
                  : this.default_value_ds != null
                     ? (    (this.default_value_ds instanceof DataSourceSlot) == false
                         || ((DataSourceSlot) this.default_value_ds).isValid(context)
                       )
                     : false
               );
    }

    public Type getType(CallContext context) {
        return this.data_source != null ? this.data_source.getType(context) : this.default_value_ds != null ? this.default_value_ds.getType(context) : (this.type != null ? this.type : null);
    }

    public void setType(CallContext context, Type type) {
        if (this.getType(context) != null && (type == null || ! type.isA(context, this.getType(context)))) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, FactorySiteStringPool.get(context, "0.4.2" /* Cannot change DataSourceSlot type after type is already fixed */));
        }
        this.type = type;
    }

    public void setDataSource(CallContext context, DataSource data_source) throws TypeMismatch {
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Plugging datasource '%(otherds)' (%(otherinfo)) of type '%(othertype)' into '%(ds)' (%(info)) of type '%(type)'", "otherds", data_source, "otherinfo", data_source == null ? "" : data_source.getSourceLocationInfo(context), "othertype", data_source == null ? "" : data_source.getType(context), "ds", this, "info", this.getSourceLocationInfo(context), "type", this.type); }

        if (data_source != null) {
            if (this.getType(context) != null) {
                if (data_source.getType(context) == null) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot plug DataSource with unspecified type into DataSourceSlot of type '%(type)'", "type", this.type.getName(context));
                    throw (ExceptionPreConditionViolation) null;
                }

                if (    data_source.getType(context).isA(context, this.getType(context)) == false
                     && (    (data_source.getType(context) instanceof JavaType) == false
                          || (this.getType(context) instanceof TypeParametrised) == false
                          || (((TypeParametrised) this.getType(context)).getBaseType(context) instanceof JavaType) == false 
                          || ((JavaType) (((TypeParametrised) this.getType(context)).getBaseType(context))).getJavaClass(context).isAssignableFrom(((JavaType) data_source.getType(context)).getJavaClass(context)) == false
                        )
                   ) {
                    TypeMismatch.createAndThrow (context, FactorySiteStringPool.get(context, "0.4.1" /* DataSourceSlot of type '%(slottype)' rejects DataSource of type '%(type)' */), "slottype", this.getType(context).getName(context), "type", data_source.getType(context).getName(context));
                }
            }
        }

        this.data_source = data_source;
    }

    public DataSource getDataSource(CallContext context) {
        if (this.data_source == null) {
            CustomaryContext.create((Context)context).throwProtocolViolation(context, FactorySiteStringPool.get(context, "0.4.0" /* DataSourceSlot contains no valid DataSource yet (null pointer) */));
        }
        return this.data_source;
    }

    public void setDefaultValueDataSource(CallContext context, DataSource default_value_ds) throws TypeMismatch {
        if ((this.notification_level & Notifier.DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.DIAGNOSTICS, "Plugging default value datasource '%(otherds)' (%(otherinfo)) of type '%(othertype)' into '%(ds)' (%(info)) of type '%(type)'", "otherds", default_value_ds, "otherinfo", default_value_ds == null ? "" : default_value_ds.getSourceLocationInfo(context), "othertype", default_value_ds == null ? "" : default_value_ds.getType(context), "ds", this, "info", this.getSourceLocationInfo(context), "type", this.type); }

        if (default_value_ds != null) {
            if (this.getType(context) != null) {
                if (default_value_ds.getType(context) == null) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot plug dafault value DataSource with unspecified type into DataSourceSlot of type '%(type)'", "type", this.type.getName(context));
                    throw (ExceptionPreConditionViolation) null;
                }

                if (    default_value_ds.getType(context).isA(context, this.getType(context)) == false
                     && (    (default_value_ds.getType(context) instanceof JavaType) == false
                          || (this.getType(context) instanceof TypeParametrised) == false
                          || (((TypeParametrised) this.getType(context)).getBaseType(context) instanceof JavaType) == false 
                          || ((JavaType) (((TypeParametrised) this.getType(context)).getBaseType(context))).getJavaClass(context).isAssignableFrom(((JavaType) default_value_ds.getType(context)).getJavaClass(context)) == false
                        )
                   ) {
                    TypeMismatch.createAndThrow (context, FactorySiteStringPool.get(context, "0.4.1" /* DataSourceSlot of type '%(slottype)' rejects DataSource of type '%(type)' */), "slottype", this.getType(context).getName(context), "type", default_value_ds.getType(context).getName(context));
                }
            }
        }

        this.default_value_ds = default_value_ds;
    }

    public DataSource getDefaultValueDataSource(CallContext context) {
        return this.default_value_ds;
    }

    public DataSource getDataSourceOrDefault(CallContext context) {
        return (this.data_source != null ? this.data_source : this.default_value_ds);
    }

    protected String source_location_info;

    public String getSourceLocationInfo (CallContext context) {
        return this.source_location_info;
    }

    public void setSourceLocationInfo (CallContext context, String source_location_info) {
        this.source_location_info = source_location_info;
    }
}
