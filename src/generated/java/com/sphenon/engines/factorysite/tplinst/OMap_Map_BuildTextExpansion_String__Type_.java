// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/OMap.javatpl
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

/**
    An OMap<ItemType,IndexType> is a mapping from IndexType indices to values
    of type ItemType. It is much like a simple
    Map<IndexType,ItemType>: you can put values into it, associated with a
    key, and you can later retrieve those values using that same key.

    The difference is the behaviour of the get method when there was no value
    stored under the given key. Then, the OMap relies on properties of the
    index, assuming it provides the interface {@link Type}. It looks in it's
    internal mapping whether there is an entry for some direct or indirect
    supertype of the index, and if so, returns that value.

    Therefore the OMap is called 'O', which stands for "object oriented". You
    can store values into it, and retrieve those values even for derived
    indices.

    Actually, the OMap does not search through the supertype hierarchy each
    time. It keeps a cache of already retrieved values, builds up an internal
    entry hierarchy which is a subset of the supertype hierarchy, and
    maintains that subset.

    As an Example: given a base class B, a derived class D and some unrelated
    class U, you may store into an omap of type OMap<String,Type>:

    omap.set(type_of_b, "I'm B");

    Then, the following entries are retrievable:

    omap.tryGet(type_of_b) returns "I'm B"
    omap.tryGet(type_of_d) returns "I'm B"
    omap.tryGet(type_of_u) returns null

*/
public interface OMap_Map_BuildTextExpansion_String__Type_
  extends ReadMap_Map_BuildTextExpansion_String__Type_,
          WriteMap_Map_BuildTextExpansion_String__Type_
{
    public Map_BuildTextExpansion_String_      get     (CallContext context, Type index) throws DoesNotExist;
    public Map_BuildTextExpansion_String_      tryGet  (CallContext context, Type index);
    public boolean       canGet  (CallContext context, Type index);

    public void          set     (CallContext context, Type index, Map_BuildTextExpansion_String_ item);
    public void          add     (CallContext context, Type index, Map_BuildTextExpansion_String_ item) throws AlreadyExists;
    public void          replace (CallContext context, Type index, Map_BuildTextExpansion_String_ item) throws DoesNotExist;
    public void          unset   (CallContext context, Type index);
    public void          remove  (CallContext context, Type index) throws DoesNotExist;

    public boolean       canGetExactMatch (CallContext context, Type index);
}

