// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/Pair.javatpl
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
import com.sphenon.ui.annotations.*;

import com.sphenon.basics.many.returncodes.*;

 public class Pair_Type_String_ {
    private Type item1;
    private String item2;

    public Pair_Type_String_ (CallContext context, Type item1, String item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public Type getItem1(CallContext context) { return item1; }
    
    public void setItem1(CallContext context, Type item1) { this.item1 = item1; }

    public String getItem2(CallContext context) { return item2; }
    
    public void setItem2(CallContext context, String item2) { this.item2 = item2; }
}
