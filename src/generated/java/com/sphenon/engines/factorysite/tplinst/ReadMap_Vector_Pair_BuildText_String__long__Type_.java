// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/ReadMap.javatpl
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

public interface ReadMap_Vector_Pair_BuildText_String__long__Type_
{
    // retrieves item at index; item must exist
    public Vector_Pair_BuildText_String__long_ get     (CallContext context, Type index) throws DoesNotExist;

    // retrieves item at index; returns null if item does not exist
    public Vector_Pair_BuildText_String__long_ tryGet  (CallContext context, Type index);

    // returns true if item at index exists, otherwise false
    public boolean  canGet  (CallContext context, Type index);
}

