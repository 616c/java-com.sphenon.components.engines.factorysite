// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/ReadOnlyVector.javatpl
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

public interface ReadOnlyVector_Pair_Type_String__long_
  extends ReadVector_Pair_Type_String__long_,
          ReadOnlyVector<Pair_Type_String_>,
          OfKnownSize
{
    public Pair_Type_String_                                    get             (CallContext context, long index) throws DoesNotExist;
    public Pair_Type_String_                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_Pair_Type_String__long_ReadOnlyVector_Pair_Type_String__long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_Pair_Type_String__long_ReadOnlyVector_Pair_Type_String__long__  tryGetReference (CallContext context, long index);
}

