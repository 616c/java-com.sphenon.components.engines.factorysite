// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/Map.javatpl
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

public interface Map_FactorySite_String_
  extends ReadMap_FactorySite_String_,
          WriteMap_FactorySite_String_,
          Navigatable_IteratorItemIndex_FactorySite_String__,
          OfKnownSize
{
    public FactorySite                                 get             (CallContext context, String index) throws DoesNotExist;
    public FactorySite                                 tryGet          (CallContext context, String index);
    public boolean                                  canGet          (CallContext context, String index);

    public MapReferenceToMember_FactorySite_String_ getReference    (CallContext context, String index) throws DoesNotExist;
    public MapReferenceToMember_FactorySite_String_ tryGetReference (CallContext context, String index);

    public void                                     set             (CallContext context, String index, FactorySite item);
    public void                                     add             (CallContext context, String index, FactorySite item) throws AlreadyExists;
    public void                                     replace         (CallContext context, String index, FactorySite item) throws DoesNotExist;
    public void                                     unset           (CallContext context, String index);
    public void                                     remove          (CallContext context, String index) throws DoesNotExist;

    public IteratorItemIndex_FactorySite_String_    getNavigator    (CallContext context);

    public long                                     getSize         (CallContext context);
}

