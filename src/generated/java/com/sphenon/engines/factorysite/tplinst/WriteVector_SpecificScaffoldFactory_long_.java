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

public interface WriteVector_SpecificScaffoldFactory_long_
{
    public SpecificScaffoldFactory set          (CallContext context, long index, SpecificScaffoldFactory item);
    public void     add          (CallContext context, long index, SpecificScaffoldFactory item) throws AlreadyExists;
    public void     prepend      (CallContext context, SpecificScaffoldFactory item);
    public void     append       (CallContext context, SpecificScaffoldFactory item);
    public void     insertBefore (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist;
    public void     insertBehind (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist;
    public SpecificScaffoldFactory replace      (CallContext context, long index, SpecificScaffoldFactory item) throws DoesNotExist;
    public SpecificScaffoldFactory unset        (CallContext context, long index);
    public SpecificScaffoldFactory remove       (CallContext context, long index) throws DoesNotExist;
}

