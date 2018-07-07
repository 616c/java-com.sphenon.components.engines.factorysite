// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/VectorImpl.javatpl
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
// import com.sphenon.basics.monitoring.*;
// @review:wm
// zusätzliche includes müssen beim instantiieren angegeben werden,
// sie dürfen nicht direkt ins template eingetragen werden
// anderfalls würde eine dependency von *jeder* instant zu diesem
// package entstehen
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.many.*;

import com.sphenon.basics.many.returncodes.*;

public class VectorImpl_Pair_BuildText_String__long_
  implements Vector_Pair_BuildText_String__long_,
             VectorOptimized<Pair_BuildText_String_>,
             Dumpable,
             ManagedResource
 {
    private java.util.Vector vector;

    protected VectorImpl_Pair_BuildText_String__long_ (CallContext context) {
        vector = new java.util.Vector(4);
    }

    static public VectorImpl_Pair_BuildText_String__long_ create (CallContext context) {
        return new VectorImpl_Pair_BuildText_String__long_(context);
    }

    protected VectorImpl_Pair_BuildText_String__long_ (CallContext context, java.util.Vector vector) {
        this.vector = vector;
    }

    static public VectorImpl_Pair_BuildText_String__long_ create (CallContext context, java.util.Vector vector) {
        return new VectorImpl_Pair_BuildText_String__long_(context, vector);
    }

    

    public Pair_BuildText_String_ get          (CallContext context, long index) throws DoesNotExist {
        try {
            return (Pair_BuildText_String_) vector.elementAt((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
    }

    public Pair_BuildText_String_ tryGet       (CallContext context, long index) {
        try {
            return (Pair_BuildText_String_) vector.elementAt((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean  canGet       (CallContext context, long index) {
        return (index >= 0 && index < vector.size()) ? true : false;
    }

    public VectorReferenceToMember_Pair_BuildText_String__long_ getReference    (CallContext context, long index) throws DoesNotExist {
        if ( ! canGet(context, index)) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
        return new VectorReferenceToMember_Pair_BuildText_String__long_(context, this, index);
    }

    public VectorReferenceToMember_Pair_BuildText_String__long_ tryGetReference (CallContext context, long index) {
        if ( ! canGet(context, index)) { return null; }
        return new VectorReferenceToMember_Pair_BuildText_String__long_(context, this, index);
    }

    public Pair_BuildText_String_ set          (CallContext context, long index, Pair_BuildText_String_ item) {
        if (index >= vector.size()) { vector.setSize((int) (index+1)); }
        return (Pair_BuildText_String_) vector.set((int) index, item);
    }

    public void     add          (CallContext context, long index, Pair_BuildText_String_ item) throws AlreadyExists {
        if (index < vector.size()) { AlreadyExists.createAndThrow (context); }
        vector.setSize((int) (index+1));
        vector.setElementAt(item, (int) index);
    }

    public void     prepend      (CallContext call_context, Pair_BuildText_String_ item) {
        if (vector.size() == 0) {
            vector.add(item);
        } else {
            try {
                vector.insertElementAt(item, 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                Context context = Context.create(call_context);
                CustomaryContext cc = CustomaryContext.create(context);
                cc.throwImpossibleState(context, ManyStringPool.get(context, "0.0.1" /* cannot insert element at position 0, java-lib says 'out of bounds' ??? */));
            }
        }
    }

    public void     append       (CallContext context, Pair_BuildText_String_ item) {
        vector.add(item);
    }

    public void     insertBefore (CallContext context, long index, Pair_BuildText_String_ item) throws DoesNotExist {
        try {
            vector.insertElementAt(item, (int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(context);
        }
    }

    public void     insertBehind (CallContext context, long index, Pair_BuildText_String_ item) throws DoesNotExist {
        if (index == vector.size() - 1) {
            vector.add(item);
        } else {
            try {
                vector.insertElementAt(item, (int) (index+1));
            } catch (ArrayIndexOutOfBoundsException e) {
                DoesNotExist.createAndThrow (context);
            }
        }
    }

    public Pair_BuildText_String_ replace      (CallContext call_context, long index, Pair_BuildText_String_ item) throws DoesNotExist {
        try {
            return (Pair_BuildText_String_) vector.set((int) index, item);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(call_context);
            throw (DoesNotExist) null;
        } catch (IllegalArgumentException e) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwImpossibleState (context, ManyStringPool.get(context, "0.0.2" /* An exception occured, with respect to which the java-lib documentation is unfortunately incorrect */));
            throw (ExceptionImpossibleState) null;
        }
    }

    public Pair_BuildText_String_ unset        (CallContext context, long index) {
        try {
            return (Pair_BuildText_String_) vector.remove((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            // we kindly ignore this exception
            return null;
        }
    }

    public Pair_BuildText_String_ remove       (CallContext context, long index) throws DoesNotExist {
        try {
            return (Pair_BuildText_String_) vector.remove((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null;
        }
    }

    public IteratorItemIndex_Pair_BuildText_String__long_ getNavigator (CallContext context) {
        return new VectorIteratorImpl_Pair_BuildText_String__long_ (context, this);
    }

    public long     getSize      (CallContext context) {
        return vector.size();
    }

    public java.util.Iterator<Pair_BuildText_String_> getIterator_Pair_BuildText_String__ (CallContext context) {
        return vector.iterator();
    }

    public java.util.Iterator getIterator (CallContext context) {
        return getIterator_Pair_BuildText_String__(context);
    }

    public VectorIterable_Pair_BuildText_String__long_ getIterable_Pair_BuildText_String__ (CallContext context) {
        return new VectorIterable_Pair_BuildText_String__long_(context, this);
    }

    public Iterable<Pair_BuildText_String_> getIterable (CallContext context) {
        return getIterable_Pair_BuildText_String__ (context);
    }

    public java.util.Vector getImplementationVector(CallContext context){
      return this.vector;
    }

    public void setImplementationVector(CallContext context, java.util.Vector vector){
      this.vector = vector;
    }

    public boolean contains(CallContext context, Pair_BuildText_String_ item) {
        return this.vector.contains(item);
    }

    public boolean removeFirst(CallContext context, Pair_BuildText_String_ item) {
        return this.vector.remove(item);
    }

    public void removeAll(CallContext context, Pair_BuildText_String_ item, VectorOptimized.Notifier<Pair_BuildText_String_> notifier) {
        java.util.Iterator i = this.vector.iterator();
        while (i.hasNext()) {
            if (i.next() == item) {
                i.remove();
                if (notifier != null) { notifier.onRemove(context, item); }
            }
        }
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
