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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.metadata.exceptions.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.factorysite.factories.*;

public class TypeImpl_Aggregate
  implements Type
{
    private String aggregate_class;
    protected Vector_Type_long_ supertypes = null;
    protected Factory_Aggregate factory_aggregate;


    public TypeImpl_Aggregate (CallContext context, String aggregate_class) throws NoSuchClass {
        this.aggregate_class = aggregate_class;

        this.factory_aggregate = new Factory_Aggregate(context);
        this.factory_aggregate.setAggregateClass(context, aggregate_class);
        if (this.factory_aggregate.exists(context) == false) {
            NoSuchClass.createAndThrow(context, aggregate_class);
            throw (NoSuchClass) null;
        }
    }

    public String getId (CallContext context) {
        return "Aggregate::" + this.aggregate_class;
    }

    public String getName (CallContext context) {
        return this.aggregate_class;
    }

    public String toString () {
        return this.aggregate_class;
    }

    public boolean equals (Object object) {
        if (object == null) return false;
        if (! (object instanceof TypeImpl_Aggregate)) return false;
        if (! ((TypeImpl_Aggregate) object).aggregate_class.equals(this.aggregate_class)) return false;
        return true;
    }

    public int hashCode () {
        return this.aggregate_class.hashCode();
    }

    public boolean equals (CallContext context, Object object) {
        if (object == null) return false;
        if (! (object instanceof TypeImpl_Aggregate)) return false;
        if (! ((TypeImpl_Aggregate) object).aggregate_class.equals(this.aggregate_class)) return false;
        return true;
    }

    public Vector_Type_long_ getSuperTypes (CallContext context) {
        if (this.supertypes == null) {
            Type return_type = this.factory_aggregate.getRootType(context); // ds.getType(context);
            this.factory_aggregate = null;

            this.supertypes = Factory_Vector_Type_long_.construct(context);
            this.supertypes.append(context, return_type);
        }
        return this.supertypes;
    }

    private Vector_Type_long_ all_interfaces;

    public Vector_Type_long_ getAllSuperInterfaces (CallContext context) {
        return (this.all_interfaces = TypeImpl.getOrBuildAllSuperInterfaces (context, this, this.all_interfaces));
    }

    private Vector_Type_long_ all_shortest_path_interfaces;

    public Vector_Type_long_ getAllShortestPathSuperInterfaces (CallContext context) {
        return (this.all_shortest_path_interfaces = TypeImpl.getOrBuildAllShortestPathSuperInterfaces (context, this, this.all_shortest_path_interfaces));
    }

    public boolean isA (CallContext context, Type type) {
        if (type == null) return false;
        if (this.equals(context, type)) return true;
        for (Iterator_Type_ it = this.getSuperTypes(context).getNavigator(context);
             it.canGetCurrent(context);
             it.next(context)
            ) {
            if (it.tryGetCurrent(context).isA(context, type)) return true;
        }
        return false;
    }
}
