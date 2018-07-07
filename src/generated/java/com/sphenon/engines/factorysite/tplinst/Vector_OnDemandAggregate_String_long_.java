// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/Vector_OnDemandAggregate.javatpl
// please do not modify this file directly
package com.sphenon.engines.factorysite.tplinst;

import com.sphenon.basics.many.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;
import java.lang.reflect.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.event.*;
import com.sphenon.basics.event.tplinst.*;
import com.sphenon.engines.factorysite.factories.*;

import java.util.Hashtable;

public class Vector_OnDemandAggregate_String_long_
  extends VectorProxy_String_long_
{
    protected Vector_String_long_ vector;

    public Vector_OnDemandAggregate_String_long_ (CallContext context) {
        super(context, null);
    }

    public Vector_String_long_ defaultVector(CallContext context) {
        return null;
    }

    public Vector_String_long_ getVector(CallContext context) {
        if (this.vector == null) {
            Factory_Aggregate cf = new Factory_Aggregate(context);
            cf.setAggregateClass(context, this.aggregate_class);
            cf.setParameters(context, this.arguments);
            this.vector = (Vector_String_long_) cf.create(context);
        }
        return this.vector;
    }

    protected String aggregate_class;

    public String getAggregateClass (CallContext context) {
        return this.aggregate_class;
    }

    public void setAggregateClass (CallContext context, String aggregate_class) {
        this.aggregate_class = aggregate_class;
    }

    protected Hashtable arguments;

    public Hashtable getArguments (CallContext context) {
        return this.arguments;
    }

    public void setArguments (CallContext context, Hashtable arguments) {
        this.arguments = arguments;
    }
        
}

