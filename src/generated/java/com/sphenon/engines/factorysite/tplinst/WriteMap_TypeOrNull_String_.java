// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/WriteMap.javatpl
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

public interface WriteMap_TypeOrNull_String_
{
    // associates index with item, previous association may already exist
    public void     set     (CallContext context, String index, TypeOrNull item);

    // associates index with item, previous association must not exist
    public void     add     (CallContext context, String index, TypeOrNull item) throws AlreadyExists;

    // associates index with item, previous association must exist
    public void     replace (CallContext context, String index, TypeOrNull item) throws DoesNotExist;

    // removes index entry, entry needs not exist
    public void     unset   (CallContext context, String index);

    // removes index entry, entry must exist
    public void     remove  (CallContext context, String index) throws DoesNotExist;
}

