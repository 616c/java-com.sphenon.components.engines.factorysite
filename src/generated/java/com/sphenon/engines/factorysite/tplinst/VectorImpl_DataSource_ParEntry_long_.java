// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/VectorImpl_DataSource.javatpl

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

import java.util.Vector;
import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.basics.many.returncodes.*;

public class VectorImpl_DataSource_ParEntry_long_
  implements Vector_ParEntry_long_, ManagedResource {
    private com.sphenon.basics.data.DataSource<Vector> vector_source;

    public VectorImpl_DataSource_ParEntry_long_ (CallContext context, com.sphenon.basics.data.DataSource<Vector> vector_source) {
        this.vector_source = vector_source;
    }

    static public VectorImpl_DataSource_ParEntry_long_ create (CallContext context, com.sphenon.basics.data.DataSource<Vector> vector_source) {
        return new VectorImpl_DataSource_ParEntry_long_(context, vector_source);
    }

    public ParEntry get          (CallContext context, long index) throws DoesNotExist {
        try {
            return (ParEntry) vector_source.get(context).elementAt((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
    }

    public ParEntry tryGet       (CallContext context, long index) {
        try {
            return (ParEntry) vector_source.get(context).elementAt((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean  canGet       (CallContext context, long index) {
        return (index >= 0 && index < vector_source.get(context).size()) ? true : false;
    }

    public VectorReferenceToMember_ParEntry_long_ getReference    (CallContext context, long index) throws DoesNotExist {
        if ( ! canGet(context, index)) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
        return new VectorReferenceToMember_ParEntry_long_(context, this, index);
    }

    public VectorReferenceToMember_ParEntry_long_ tryGetReference (CallContext context, long index) {
        if ( ! canGet(context, index)) { return null; }
        return new VectorReferenceToMember_ParEntry_long_(context, this, index);
    }

    public ParEntry set          (CallContext context, long index, ParEntry item) {
        if (index >= vector_source.get(context).size()) { vector_source.get(context).setSize((int) (index+1)); }
        return (ParEntry) vector_source.get(context).set((int) index, item);
    }

    public void     add          (CallContext context, long index, ParEntry item) throws AlreadyExists {
        if (index < vector_source.get(context).size()) { AlreadyExists.createAndThrow (context); }
        vector_source.get(context).setSize((int) (index+1));
        vector_source.get(context).setElementAt(item, (int) index);
    }

    public void     prepend      (CallContext context, ParEntry item) {
        if (vector_source.get(context).size() == 0) {
            vector_source.get(context).add(item);
        } else {
            try {
                vector_source.get(context).insertElementAt(item, 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                CustomaryContext cc = CustomaryContext.create((Context)context);
                cc.throwImpossibleState(context, ManyStringPool.get(context, "0.0.1" /* cannot insert element at position 0, java-lib says 'out of bounds' ??? */));
            }
        }
    }

    public void     append       (CallContext context, ParEntry item) {
        vector_source.get(context).add(item);
    }

    public void     insertBefore (CallContext context, long index, ParEntry item) throws DoesNotExist {
        try {
            vector_source.get(context).insertElementAt(item, (int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(context);
        }
    }

    public void     insertBehind (CallContext context, long index, ParEntry item) throws DoesNotExist {
        if (index == vector_source.get(context).size() - 1) {
            vector_source.get(context).add(item);
        } else {
            try {
                vector_source.get(context).insertElementAt(item, (int) (index+1));
            } catch (ArrayIndexOutOfBoundsException e) {
                DoesNotExist.createAndThrow (context);
            }
        }
    }

    public ParEntry replace      (CallContext context, long index, ParEntry item) throws DoesNotExist {
        try {
            return (ParEntry) vector_source.get(context).set((int) index, item);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow(context);
            throw (DoesNotExist) null;
        } catch (IllegalArgumentException e) {
            CustomaryContext cc = CustomaryContext.create((Context)context);
            cc.throwImpossibleState (context, ManyStringPool.get(context, "0.0.2" /* An exception occured, with respect to which the java-lib documentation is unfortunately incorrect */));
            throw (ExceptionImpossibleState) null;
        }
    }

    public ParEntry unset        (CallContext context, long index) {
        try {
            return (ParEntry) vector_source.get(context).remove((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            // we kindly ignore this exception
            return null;
        }
    }

    public ParEntry remove       (CallContext context, long index) throws DoesNotExist {
        try {
            return (ParEntry) vector_source.get(context).remove((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null;
        }
    }

    public IteratorItemIndex_ParEntry_long_ getNavigator (CallContext context) {
        return new VectorIteratorImpl_ParEntry_long_ (context, this);
    }

    public long     getSize      (CallContext context) {
        return vector_source.get(context).size();
    }

    public java.util.Iterator<ParEntry> getIterator_ParEntry_ (CallContext context) {
        return vector_source.get(context).iterator();
    }

    public java.util.Iterator getIterator (CallContext context) {
        return getIterator_ParEntry_(context);
    }

    public VectorIterable_ParEntry_long_ getIterable_ParEntry_ (CallContext context) {
        return new VectorIterable_ParEntry_long_(context, this);
    }

    public Iterable<ParEntry> getIterable (CallContext context) {
        return getIterable_ParEntry_ (context);
    }

    public void release(CallContext context) {
        if (this.vector_source != null && this.vector_source instanceof ManagedResource) {
            ((ManagedResource)(this.vector_source)).release(context);
        }
    }
}
