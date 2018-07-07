// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/ReadVector.javatpl
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

public interface ReadVector_BuildTextXMLPreprocessor_long_
{
    public BuildTextXMLPreprocessor                                    get             (CallContext context, long index) throws DoesNotExist;
    public BuildTextXMLPreprocessor                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_BuildTextXMLPreprocessor_long_ReadOnlyVector_BuildTextXMLPreprocessor_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_BuildTextXMLPreprocessor_long_ReadOnlyVector_BuildTextXMLPreprocessor_long__  tryGetReference (CallContext context, long index);
}

