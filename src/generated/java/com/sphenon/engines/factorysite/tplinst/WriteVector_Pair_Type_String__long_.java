// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/WriteVector.javatpl
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

import com.sphenon.basics.many.returncodes.*;

public interface WriteVector_Pair_Type_String__long_
{
    public Pair_Type_String_ set          (CallContext context, long index, Pair_Type_String_ item);
    public void     add          (CallContext context, long index, Pair_Type_String_ item) throws AlreadyExists;
    public void     prepend      (CallContext context, Pair_Type_String_ item);
    public void     append       (CallContext context, Pair_Type_String_ item);
    public void     insertBefore (CallContext context, long index, Pair_Type_String_ item) throws DoesNotExist;
    public void     insertBehind (CallContext context, long index, Pair_Type_String_ item) throws DoesNotExist;
    public Pair_Type_String_ replace      (CallContext context, long index, Pair_Type_String_ item) throws DoesNotExist;
    public Pair_Type_String_ unset        (CallContext context, long index);
    public Pair_Type_String_ remove       (CallContext context, long index) throws DoesNotExist;
}

