package com.sphenon.engines.factorysite.converters;

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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

import java.util.HashMap;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class ConverterToAggregate implements Converter {

    public ConverterToAggregate (CallContext context) {
    }

    public ConverterToAggregate (CallContext context, String aggregate_class) {
        this.aggregate_class = aggregate_class;
    }

    public Object convert(CallContext context, Object source) {
        java.util.Hashtable parameters = new java.util.Hashtable();

        if (this.additional_arguments != null) {
            Set<Map.Entry> entries = this.additional_arguments.entrySet();
            int idx = 2;
            for (Map.Entry me : entries) {
                parameters.put(me.getKey(), me.getValue());
            }
        }

        parameters.put("Source", source);

        Factory_Aggregate cf = new Factory_Aggregate(context);
        cf.setAggregateClass(context, this.aggregate_class);
        cf.setParameters(context, parameters);
        cf.setUseCache(context, true);
        cf.setAggregateCache(context, this.aggregate_cache);
        cf.setAggregateCacheParameter(context, this.aggregate_cache_parameter);

        return cf.create(context);
    }

    protected String aggregate_class;

    public String getAggregateClass (CallContext context) {
        return this.aggregate_class;
    }

    public void setAggregateClass (CallContext context, String aggregate_class) {
        this.aggregate_class = aggregate_class;
    }

    protected Hashtable additional_arguments;

    public Hashtable getAdditionalArguments (CallContext context) {
        return this.additional_arguments;
    }

    public Hashtable defaultAdditionalArguments (CallContext context) {
        return null;
    }

    public void setAdditionalArguments (CallContext context, Hashtable additional_arguments) {
        this.additional_arguments = additional_arguments;
    }

    protected Map aggregate_cache;

    public Map getAggregateCache (CallContext context) {
        return this.aggregate_cache;
    }

    public Map defaultAggregateCache (CallContext context) {
        return null;
    }

    public void setAggregateCache (CallContext context, Map aggregate_cache) {
        this.aggregate_cache = aggregate_cache;
    }

    protected String aggregate_cache_parameter;

    public String getAggregateCacheParameter (CallContext context) {
        return this.aggregate_cache_parameter;
    }

    public String defaultAggregateCacheParameter (CallContext context) {
        return null;
    }

    public void setAggregateCacheParameter (CallContext context, String aggregate_cache_parameter) {
        this.aggregate_cache_parameter = aggregate_cache_parameter;
    }
}

