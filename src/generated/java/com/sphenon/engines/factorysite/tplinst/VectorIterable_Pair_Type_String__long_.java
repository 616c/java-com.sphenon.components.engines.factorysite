// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/VectorIterable.javatpl
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

public class VectorIterable_Pair_Type_String__long_ implements Iterable<Pair_Type_String_>
{
    protected java.util.Iterator<Pair_Type_String_> iterator;

    public VectorIterable_Pair_Type_String__long_ (CallContext context, Vector_Pair_Type_String__long_ vector) {
        this.iterator = (vector == null ? (new java.util.Vector<Pair_Type_String_>()).iterator() : vector.getIterator_Pair_Type_String__(context));
    }

    public java.util.Iterator<Pair_Type_String_> iterator () {
        return this.iterator;
    }
}

