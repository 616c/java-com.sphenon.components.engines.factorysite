package com.sphenon.engines.factorysite.factories;

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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;

import java.util.Vector;
import java.util.Map;

public class AggregateRegistry_Class implements AggregateRegistry {

    public AggregateRegistry_Class(CallContext context) {
    }

    protected OMap_String_Type_ oo_registry;

    public void registerFactoryAggregate(CallContext context, Factory_Aggregate factory_aggregate) {
        if (oo_registry == null) {
            oo_registry = Factory_OMap_String_Type_.construct(context);
        }

        FactorySite fs = factory_aggregate.getFactorySite(context);
        Vector_String_long_ ra = fs.getRootArguments(context);

        if (ra == null || ra.getSize(context) == 0) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Polymorphic ocp does not declare a root signature");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        DataSourceConnector dsc = fs.getParameters(context).tryGet(context, ra.tryGet(context, 0));
        Type argument_type = dsc.getType(context);

        if (argument_type == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Polymorphic ocp's first parameter does not declare a Type");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        oo_registry.set(context, argument_type, factory_aggregate.getAggregateClass(context));
    }

    public Factory_Aggregate tryGet(CallContext context, FactorySite factory_site, Map<String,Object> parameters) {
        if (oo_registry == null) { return null; }

        Vector_String_long_ ra = factory_site.getRootArguments(context);

        if (ra == null || ra.getSize(context) == 0) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Polymorphic ocp does not declare a root signature");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        Object object = parameters.get(ra.tryGet(context, 0));
        if (object == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Polymorphic ocp is called with first parameter null");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        String aggregate_class = oo_registry.tryGet(context, TypeManager.get(context, object.getClass()));
        if (aggregate_class == null) {
            return null;
        }

        Factory_Aggregate factory_aggregate = new Factory_Aggregate(context);
        factory_aggregate.setAggregateClass(context, aggregate_class);
        return factory_aggregate;
    }
}
