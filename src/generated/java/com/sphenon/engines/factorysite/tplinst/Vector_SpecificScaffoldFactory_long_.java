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
@UIClassifier("Vector_SpecificScaffoldFactory_")
@UIParts("js:instance.getIterable(context)")
public interface Vector_SpecificScaffoldFactory_long_
  extends ReadOnlyVector_SpecificScaffoldFactory_long_,
          WriteVector_SpecificScaffoldFactory_long_
          , GenericVector<SpecificScaffoldFactory>
          , GenericIterable<SpecificScaffoldFactory>
{
    public SpecificScaffoldFactory                                    get             (CallContext context, long index) throws DoesNotExist;
    public SpecificScaffoldFactory                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_SpecificScaffoldFactory_long_ReadOnlyVector_SpecificScaffoldFactory_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_SpecificScaffoldFactory_long_ReadOnlyVector_SpecificScaffoldFactory_long__  tryGetReference (CallContext context, long index);

    public SpecificScaffoldFactory                                    set             (CallContext context, long index, SpecificScaffoldFactory item);
    public void                                        add             (CallContext context, long index, SpecificScaffoldFactory item) throws AlreadyExists;
    public void                                        prepend         (CallContext context, SpecificScaffoldFactory item);
    public void                                        append          (CallContext context, SpecificScaffoldFactory item);
    public void                                        insertBefore    (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist;
    public void                                        insertBehind    (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist;
    public SpecificScaffoldFactory                                    replace         (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist;
    public SpecificScaffoldFactory                                    unset           (CallContext context, long index);
    public SpecificScaffoldFactory                                    remove          (CallContext context, long index) throws DoesNotExist;

    public IteratorItemIndex_SpecificScaffoldFactory_long_       getNavigator    (CallContext context);

    public long                                        getSize         (CallContext context);

    // for sake of Iterable's
    public java.util.Iterator<SpecificScaffoldFactory>              getIterator_SpecificScaffoldFactory_ (CallContext context);
    public java.util.Iterator                          getIterator (CallContext context);
    public VectorIterable_SpecificScaffoldFactory_long_          getIterable_SpecificScaffoldFactory_ (CallContext context);
    public Iterable<SpecificScaffoldFactory> getIterable (CallContext context);
}
