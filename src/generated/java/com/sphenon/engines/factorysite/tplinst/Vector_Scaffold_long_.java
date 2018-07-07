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
@UIClassifier("Vector_Scaffold_")
@UIParts("js:instance.getIterable(context)")
public interface Vector_Scaffold_long_
  extends ReadOnlyVector_Scaffold_long_,
          WriteVector_Scaffold_long_
          , GenericVector<Scaffold>
          , GenericIterable<Scaffold>
{
    public Scaffold                                    get             (CallContext context, long index) throws DoesNotExist;
    public Scaffold                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_Scaffold_long_ReadOnlyVector_Scaffold_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_Scaffold_long_ReadOnlyVector_Scaffold_long__  tryGetReference (CallContext context, long index);

    public Scaffold                                    set             (CallContext context, long index, Scaffold item);
    public void                                        add             (CallContext context, long index, Scaffold item) throws AlreadyExists;
    public void                                        prepend         (CallContext context, Scaffold item);
    public void                                        append          (CallContext context, Scaffold item);
    public void                                        insertBefore    (CallContext context, long index, Scaffold item) throws DoesNotExist;
    public void                                        insertBehind    (CallContext context, long index, Scaffold item) throws DoesNotExist;
    public Scaffold                                    replace         (CallContext context, long index, Scaffold item) throws DoesNotExist;
    public Scaffold                                    unset           (CallContext context, long index);
    public Scaffold                                    remove          (CallContext context, long index) throws DoesNotExist;

    public IteratorItemIndex_Scaffold_long_       getNavigator    (CallContext context);

    public long                                        getSize         (CallContext context);

    // for sake of Iterable's
    public java.util.Iterator<Scaffold>              getIterator_Scaffold_ (CallContext context);
    public java.util.Iterator                          getIterator (CallContext context);
    public VectorIterable_Scaffold_long_          getIterable_Scaffold_ (CallContext context);
    public Iterable<Scaffold> getIterable (CallContext context);
}
