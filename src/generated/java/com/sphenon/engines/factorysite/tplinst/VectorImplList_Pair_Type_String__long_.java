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

public class VectorImplList_Pair_Type_String__long_
  implements Vector_Pair_Type_String__long_, VectorImplList, Dumpable, ManagedResource {
    protected java.util.List vector;

    protected VectorImplList_Pair_Type_String__long_ (CallContext context) {
        vector = new java.util.ArrayList ();
    }

    static public VectorImplList_Pair_Type_String__long_ create (CallContext context) {
        return new VectorImplList_Pair_Type_String__long_(context);
    }

    protected VectorImplList_Pair_Type_String__long_ (CallContext context, java.util.List vector) {
        this.vector = vector;
    }

    static public VectorImplList_Pair_Type_String__long_ create (CallContext context, java.util.List vector) {
        return new VectorImplList_Pair_Type_String__long_(context, vector);
    }

    public Pair_Type_String_ get          (CallContext context, long index) throws DoesNotExist {
        try {
            return (Pair_Type_String_) vector.get((int) index);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
    }

    public Pair_Type_String_ tryGet       (CallContext context, long index) {
        if (index < 0 || index >= vector.size()) {
            return null;
        }
        return (Pair_Type_String_) vector.get((int) index);
    }

    public boolean  canGet       (CallContext context, long index) {
        return (index >= 0 && index < vector.size()) ? true : false;
    }

    public VectorReferenceToMember_Pair_Type_String__long_ getReference    (CallContext context, long index) throws DoesNotExist {
        if ( ! canGet(context, index)) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
        return new VectorReferenceToMember_Pair_Type_String__long_(context, this, index);
    }

    public VectorReferenceToMember_Pair_Type_String__long_ tryGetReference (CallContext context, long index) {
        if ( ! canGet(context, index)) { return null; }
        return new VectorReferenceToMember_Pair_Type_String__long_(context, this, index);
    }

    public Pair_Type_String_ set          (CallContext context, long index, Pair_Type_String_ item) {
        while (index > vector.size()) { vector.add(null); }
        if( index == vector.size()) {
            vector.add(item);
            return null;
        } else {
            return (Pair_Type_String_) vector.set((int) index, item);
        }
    }

    public void     add          (CallContext context, long index, Pair_Type_String_ item) throws AlreadyExists {
        if (index < vector.size()) { AlreadyExists.createAndThrow (context); }
        set(context, index, item);
    }

    public void     prepend      (CallContext call_context, Pair_Type_String_ item) {
        if (vector.size() == 0) {
            vector.add(item);
        } else {
            vector.add(0, item);
        }
    }

    public void     append       (CallContext context, Pair_Type_String_ item) {
        vector.add(item);
    }

    public void     insertBefore (CallContext context, long index, Pair_Type_String_ item) throws DoesNotExist {
        try {
            vector.add((int) index, item);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(context);
        }
    }

    public void     insertBehind (CallContext context, long index, Pair_Type_String_ item) throws DoesNotExist {
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

    public Pair_Type_String_ replace      (CallContext call_context, long index, Pair_Type_String_ item) throws DoesNotExist {
        try {
            return (Pair_Type_String_) vector.set((int) index, item);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(call_context);
            throw (DoesNotExist) null;
        }
    }

    public Pair_Type_String_ unset        (CallContext context, long index) {
        try {
            return (Pair_Type_String_) vector.remove((int) index);
        } catch (IndexOutOfBoundsException e) {
            // we kindly ignore this exception
            return null;
        }
    }

    public Pair_Type_String_ remove       (CallContext context, long index) throws DoesNotExist {
        try {
            return (Pair_Type_String_) vector.remove((int) index);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null;
        }
    }

    public IteratorItemIndex_Pair_Type_String__long_ getNavigator (CallContext context) {
        return new VectorIteratorImpl_Pair_Type_String__long_ (context, this);
    }

    public long     getSize      (CallContext context) {
        return vector.size();
    }

    // to be used with care
    public java.util.List getImplementationList (CallContext context) {
        return this.vector;
    }

    public java.util.Iterator<Pair_Type_String_> getIterator_Pair_Type_String__ (CallContext context) {
        return vector.iterator();
    }

    public java.util.Iterator getIterator (CallContext context) {
        return getIterator_Pair_Type_String__(context);
    }

    public VectorIterable_Pair_Type_String__long_ getIterable_Pair_Type_String__ (CallContext context) {
        return new VectorIterable_Pair_Type_String__long_(context, this);
    }

    public Iterable<Pair_Type_String_> getIterable (CallContext context) {
        return getIterable_Pair_Type_String__ (context);
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
