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

public class VectorIterable_SpecificScaffoldFactory_long_ implements Iterable<SpecificScaffoldFactory>
{
    protected java.util.Iterator<SpecificScaffoldFactory> iterator;

    public VectorIterable_SpecificScaffoldFactory_long_ (CallContext context, Vector_SpecificScaffoldFactory_long_ vector) {
        this.iterator = (vector == null ? (new java.util.Vector<SpecificScaffoldFactory>()).iterator() : vector.getIterator_SpecificScaffoldFactory_(context));
    }

    public java.util.Iterator<SpecificScaffoldFactory> iterator () {
        return this.iterator;
    }
}

