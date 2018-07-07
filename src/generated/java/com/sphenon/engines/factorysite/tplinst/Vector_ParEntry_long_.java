// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/Vector.javatpl
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

import com.sphenon.ui.annotations.*;

@UIId("")
@UIName("")
@UIClassifier("Vector_ParEntry_")
@UIParts("js:instance.getIterable(context)")
public interface Vector_ParEntry_long_
  extends ReadOnlyVector_ParEntry_long_,
          WriteVector_ParEntry_long_
          , GenericVector<ParEntry>
          , GenericIterable<ParEntry>
{
    public ParEntry                                    get             (CallContext context, long index) throws DoesNotExist;
    public ParEntry                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_ParEntry_long_ReadOnlyVector_ParEntry_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_ParEntry_long_ReadOnlyVector_ParEntry_long__  tryGetReference (CallContext context, long index);

    public ParEntry                                    set             (CallContext context, long index, ParEntry item);
    public void                                        add             (CallContext context, long index, ParEntry item) throws AlreadyExists;
    public void                                        prepend         (CallContext context, ParEntry item);
    public void                                        append          (CallContext context, ParEntry item);
    public void                                        insertBefore    (CallContext context, long index, ParEntry item) throws DoesNotExist;
    public void                                        insertBehind    (CallContext context, long index, ParEntry item) throws DoesNotExist;
    public ParEntry                                    replace         (CallContext context, long index, ParEntry item) throws DoesNotExist;
    public ParEntry                                    unset           (CallContext context, long index);
    public ParEntry                                    remove          (CallContext context, long index) throws DoesNotExist;

    public IteratorItemIndex_ParEntry_long_       getNavigator    (CallContext context);

    public long                                        getSize         (CallContext context);

    // for sake of Iterable's
    public java.util.Iterator<ParEntry>              getIterator_ParEntry_ (CallContext context);
    public java.util.Iterator                          getIterator (CallContext context);
    public VectorIterable_ParEntry_long_          getIterable_ParEntry_ (CallContext context);
    public Iterable<ParEntry> getIterable (CallContext context);
}
