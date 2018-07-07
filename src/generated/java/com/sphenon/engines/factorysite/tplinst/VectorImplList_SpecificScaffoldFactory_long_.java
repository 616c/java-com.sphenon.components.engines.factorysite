// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/VectorImplList.javatpl
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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;

import com.sphenon.basics.many.returncodes.*;

public class VectorImplList_SpecificScaffoldFactory_long_
  implements Vector_SpecificScaffoldFactory_long_, VectorImplList, Dumpable, ManagedResource {
    protected java.util.List vector;

    protected VectorImplList_SpecificScaffoldFactory_long_ (CallContext context) {
        vector = new java.util.ArrayList ();
    }

    static public VectorImplList_SpecificScaffoldFactory_long_ create (CallContext context) {
        return new VectorImplList_SpecificScaffoldFactory_long_(context);
    }

    protected VectorImplList_SpecificScaffoldFactory_long_ (CallContext context, java.util.List vector) {
        this.vector = vector;
    }

    static public VectorImplList_SpecificScaffoldFactory_long_ create (CallContext context, java.util.List vector) {
        return new VectorImplList_SpecificScaffoldFactory_long_(context, vector);
    }

    public SpecificScaffoldFactory get          (CallContext context, long index) throws DoesNotExist {
        try {
            return (SpecificScaffoldFactory) vector.get((int) index);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
    }

    public SpecificScaffoldFactory tryGet       (CallContext context, long index) {
        if (index < 0 || index >= vector.size()) {
            return null;
        }
        return (SpecificScaffoldFactory) vector.get((int) index);
    }

    public boolean  canGet       (CallContext context, long index) {
        return (index >= 0 && index < vector.size()) ? true : false;
    }

    public VectorReferenceToMember_SpecificScaffoldFactory_long_ getReference    (CallContext context, long index) throws DoesNotExist {
        if ( ! canGet(context, index)) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
        return new VectorReferenceToMember_SpecificScaffoldFactory_long_(context, this, index);
    }

    public VectorReferenceToMember_SpecificScaffoldFactory_long_ tryGetReference (CallContext context, long index) {
        if ( ! canGet(context, index)) { return null; }
        return new VectorReferenceToMember_SpecificScaffoldFactory_long_(context, this, index);
    }

    public SpecificScaffoldFactory set          (CallContext context, long index, SpecificScaffoldFactory item) {
        while (index > vector.size()) { vector.add(null); }
        if( index == vector.size()) {
            vector.add(item);
            return null;
        } else {
            return (SpecificScaffoldFactory) vector.set((int) index, item);
        }
    }

    public void     add          (CallContext context, long index, SpecificScaffoldFactory item) throws AlreadyExists {
        if (index < vector.size()) { AlreadyExists.createAndThrow (context); }
        set(context, index, item);
    }

    public void     prepend      (CallContext call_context, SpecificScaffoldFactory item) {
        if (vector.size() == 0) {
            vector.add(item);
        } else {
            vector.add(0, item);
        }
    }

    public void     append       (CallContext context, SpecificScaffoldFactory item) {
        vector.add(item);
    }

    public void     insertBefore (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist {
        try {
            vector.add((int) index, item);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(context);
        }
    }

    public void     insertBehind (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist {
        if (index == vector.size() - 1) {
            vector.add(item);
        } else {
            try {
                vector.add((int) index + 1, item);
            } catch (IndexOutOfBoundsException e) {
                DoesNotExist.createAndThrow (context);
            }
        }
    }

    public SpecificScaffoldFactory replace      (CallContext call_context, long index, SpecificScaffoldFactory item) throws DoesNotExist {
        try {
            return (SpecificScaffoldFactory) vector.set((int) index, item);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(call_context);
            throw (DoesNotExist) null;
        }
    }

    public SpecificScaffoldFactory unset        (CallContext context, long index) {
        try {
            return (SpecificScaffoldFactory) vector.remove((int) index);
        } catch (IndexOutOfBoundsException e) {
            // we kindly ignore this exception
            return null;
        }
    }

    public SpecificScaffoldFactory remove       (CallContext context, long index) throws DoesNotExist {
        try {
            return (SpecificScaffoldFactory) vector.remove((int) index);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null;
        }
    }

    public IteratorItemIndex_SpecificScaffoldFactory_long_ getNavigator (CallContext context) {
        return new VectorIteratorImpl_SpecificScaffoldFactory_long_ (context, this);
    }

    public long     getSize      (CallContext context) {
        return vector.size();
    }

    // to be used with care
    public java.util.List getImplementationList (CallContext context) {
        return this.vector;
    }

    public java.util.Iterator<SpecificScaffoldFactory> getIterator_SpecificScaffoldFactory_ (CallContext context) {
        return vector.iterator();
    }

    public java.util.Iterator getIterator (CallContext context) {
        return getIterator_SpecificScaffoldFactory_(context);
    }

    public VectorIterable_SpecificScaffoldFactory_long_ getIterable_SpecificScaffoldFactory_ (CallContext context) {
        return new VectorIterable_SpecificScaffoldFactory_long_(context, this);
    }

    public Iterable<SpecificScaffoldFactory> getIterable (CallContext context) {
        return getIterable_SpecificScaffoldFactory_ (context);
    }


    public void release(CallContext context) {
        if (this.vector != null && this.vector instanceof ManagedResource) {
            ((ManagedResource)(this.vector)).release(context);
        }
    }

    public void dump(CallContext context, DumpNode dump_node) {
        int i=1;
        for (Object o : vector) {
            dump_node.dump(context, (new Integer(i++)).toString(), o);
        }
    }
}
