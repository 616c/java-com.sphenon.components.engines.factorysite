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

public interface WriteVector_ScaffoldParameter_long_
{
    public ScaffoldParameter set          (CallContext context, long index, ScaffoldParameter item);
    public void     add          (CallContext context, long index, ScaffoldParameter item) throws AlreadyExists;
    public void     prepend      (CallContext context, ScaffoldParameter item);
    public void     append       (CallContext context, ScaffoldParameter item);
    public void     insertBefore (CallContext context, long index, ScaffoldParameter item) throws DoesNotExist;
    public void     insertBehind (CallContext context, long index, ScaffoldParameter item) throws DoesNotExist;
    public ScaffoldParameter replace      (CallContext context, long index, ScaffoldParameter item) throws DoesNotExist;
    public ScaffoldParameter unset        (CallContext context, long index);
    public ScaffoldParameter remove       (CallContext context, long index) throws DoesNotExist;
}

