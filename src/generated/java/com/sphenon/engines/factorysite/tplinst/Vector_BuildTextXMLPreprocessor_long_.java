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
@UIClassifier("Vector_BuildTextXMLPreprocessor_")
@UIParts("js:instance.getIterable(context)")
public interface Vector_BuildTextXMLPreprocessor_long_
  extends ReadOnlyVector_BuildTextXMLPreprocessor_long_,
          WriteVector_BuildTextXMLPreprocessor_long_
          , GenericVector<BuildTextXMLPreprocessor>
          , GenericIterable<BuildTextXMLPreprocessor>
{
    public BuildTextXMLPreprocessor                                    get             (CallContext context, long index) throws DoesNotExist;
    public BuildTextXMLPreprocessor                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_BuildTextXMLPreprocessor_long_ReadOnlyVector_BuildTextXMLPreprocessor_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_BuildTextXMLPreprocessor_long_ReadOnlyVector_BuildTextXMLPreprocessor_long__  tryGetReference (CallContext context, long index);

    public BuildTextXMLPreprocessor                                    set             (CallContext context, long index, BuildTextXMLPreprocessor item);
    public void                                        add             (CallContext context, long index, BuildTextXMLPreprocessor item) throws AlreadyExists;
    public void                                        prepend         (CallContext context, BuildTextXMLPreprocessor item);
    public void                                        append          (CallContext context, BuildTextXMLPreprocessor item);
    public void                                        insertBefore    (CallContext context, long index, BuildTextXMLPreprocessor item) throws DoesNotExist;
    public void                                        insertBehind    (CallContext context, long index, BuildTextXMLPreprocessor item) throws DoesNotExist;
    public BuildTextXMLPreprocessor                                    replace         (CallContext context, long index, BuildTextXMLPreprocessor item) throws DoesNotExist;
    public BuildTextXMLPreprocessor                                    unset           (CallContext context, long index);
    public BuildTextXMLPreprocessor                                    remove          (CallContext context, long index) throws DoesNotExist;

    public IteratorItemIndex_BuildTextXMLPreprocessor_long_       getNavigator    (CallContext context);

    public long                                        getSize         (CallContext context);

    // for sake of Iterable's
    public java.util.Iterator<BuildTextXMLPreprocessor>              getIterator_BuildTextXMLPreprocessor_ (CallContext context);
    public java.util.Iterator                          getIterator (CallContext context);
    public VectorIterable_BuildTextXMLPreprocessor_long_          getIterable_BuildTextXMLPreprocessor_ (CallContext context);
    public Iterable<BuildTextXMLPreprocessor> getIterable (CallContext context);
}
