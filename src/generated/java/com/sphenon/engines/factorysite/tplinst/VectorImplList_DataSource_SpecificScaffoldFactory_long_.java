// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/VectorImplList_DataSource.javatpl
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
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.event.tplinst.EventDispatcher_ChangeEvent_;
import com.sphenon.basics.event.Changing;

// java
import java.util.Date;

import com.sphenon.basics.many.returncodes.*;

public class VectorImplList_DataSource_SpecificScaffoldFactory_long_
  implements Vector_SpecificScaffoldFactory_long_, Changing, ManagedResource {
    protected DataSource_List_ vector_source;
    protected EventDispatcher_ChangeEvent_ dispatcher = null;

    public EventDispatcher_ChangeEvent_ 
    getChangeEventDispatcher(CallContext context) {
        if( this.dispatcher == null ){
          this.dispatcher = new EventDispatcher_ChangeEvent_(context);
        }
        return this.dispatcher;
    } // getChangeEventDispatcher
    
    public Date 
    getLastUpdate(CallContext call_context) {
        return new Date();
    } // getLastUpdate

    protected VectorImplList_DataSource_SpecificScaffoldFactory_long_ (CallContext context, DataSource_List_ vector_source) {
        this.vector_source = vector_source;
        if( this.vector_source instanceof Changing)  {
          Changing source_changing = (Changing)this.vector_source;
          source_changing.getChangeEventDispatcher(context).addListener(context,this.getChangeEventDispatcher(context));
        } 
    }

    static public VectorImplList_DataSource_SpecificScaffoldFactory_long_ create (CallContext context, DataSource_List_ vector_source) {
        return new VectorImplList_DataSource_SpecificScaffoldFactory_long_(context, vector_source);
    }

    public SpecificScaffoldFactory get          (CallContext context, long index) throws DoesNotExist {
        try {
            return (SpecificScaffoldFactory) vector_source.get(context).get((int) index);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
    }

    public SpecificScaffoldFactory tryGet       (CallContext context, long index) {
        try {
            return (SpecificScaffoldFactory) vector_source.get(context).get((int) index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean  canGet       (CallContext context, long index) {
        return (index >= 0 && index < vector_source.get(context).size()) ? true : false;
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
        while (index > vector_source.get(context).size()) { vector_source.get(context).add(null); }
        if( index == vector_source.get(context).size()) {
            vector_source.get(context).add(item);
            return null;
        } else {
            return (SpecificScaffoldFactory) vector_source.get(context).set((int) index, item);
        }
    }

    public void     add          (CallContext context, long index, SpecificScaffoldFactory item) throws AlreadyExists {
        if (index < vector_source.get(context).size()) { AlreadyExists.createAndThrow (context); }
        set(context, index, item);
    }

    public void     prepend      (CallContext context, SpecificScaffoldFactory item) {
        if (vector_source.get(context).size() == 0) {
            vector_source.get(context).add(item);
        } else {
            vector_source.get(context).add(0, item);
        }
    }

    public void     append       (CallContext context, SpecificScaffoldFactory item) {
        vector_source.get(context).add(item);
    }

    public void     insertBefore (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist {
        try {
            vector_source.get(context).add((int) index, item);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(context);
        }
    }

    public void     insertBehind (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist {
        if (index == vector_source.get(context).size() - 1) {
            vector_source.get(context).add(item);
        } else {
            try {
                vector_source.get(context).add((int) index + 1, item);
            } catch (IndexOutOfBoundsException e) {
                DoesNotExist.createAndThrow (context);
            }
        }
    }

    public SpecificScaffoldFactory replace      (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist {
        try {
            return (SpecificScaffoldFactory) vector_source.get(context).set((int) index, item);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(context);
            throw (DoesNotExist) null;
        }
    }

    public SpecificScaffoldFactory unset        (CallContext context, long index) {
        try {
            return (SpecificScaffoldFactory) vector_source.get(context).remove((int) index);
        } catch (IndexOutOfBoundsException e) {
            // we kindly ignore this exception
            return null;
        }
    }

    public SpecificScaffoldFactory remove       (CallContext context, long index) throws DoesNotExist {
        try {
            return (SpecificScaffoldFactory) vector_source.get(context).remove((int) index);
        } catch (IndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null;
        }
    }

    public IteratorItemIndex_SpecificScaffoldFactory_long_ getNavigator (CallContext context) {
        return new VectorIteratorImpl_SpecificScaffoldFactory_long_ (context, this);
    }

    public long     getSize      (CallContext context) {
        return vector_source.get(context).size();
    }

    // to be used with care
    public java.util.List getImplementationList (CallContext context) {
        return this.vector_source.get(context);
    }

    public java.util.Iterator<SpecificScaffoldFactory> getIterator_SpecificScaffoldFactory_ (CallContext context) {
        return vector_source.get(context).iterator();
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
        if (this.vector_source != null && this.vector_source instanceof ManagedResource) {
            ((ManagedResource)(this.vector_source)).release(context);
        }
    }
}
