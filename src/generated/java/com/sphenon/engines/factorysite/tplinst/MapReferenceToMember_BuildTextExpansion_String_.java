// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/MapReferenceToMember.javatpl
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
import com.sphenon.basics.reference.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.monitoring.*;

import com.sphenon.basics.many.returncodes.*;

public class MapReferenceToMember_BuildTextExpansion_String_
    implements ReferenceToMember_BuildTextExpansion_String_Map_BuildTextExpansion_String__
{
    private Map_BuildTextExpansion_String_    map;
    private String                  index;

    public MapReferenceToMember_BuildTextExpansion_String_ (CallContext context, Map_BuildTextExpansion_String_ map, String index) {
        this.map    = map;
        this.index  = index;
        assert map.canGet(context, this.index) : SystemStateMessage.create(context, MessageText.create(context, "MapIndex created with invalid index '%(index)'", "index", index), ProblemState.ERROR);
    }

    public Map_BuildTextExpansion_String_ getContainer (CallContext context) {
        return this.map;
    }

    public String getIndex (CallContext context) {
        return this.index;
    }

    public BuildTextExpansion get (CallContext context) {
        try {
            return map.get(context, this.index);
        } catch (DoesNotExist dne) {
            CustomaryContext.create(Context.create(context)).throwPreConditionViolation(context, dne, "MapIndex contains invalid index '%(index)'", "index", index);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public boolean equals (Object object) {
        if (object == null) return false;
        if (! (object instanceof MapReferenceToMember_BuildTextExpansion_String_)) return false;
        if (((MapReferenceToMember_BuildTextExpansion_String_) object).map != this.map) return false;
        if (((MapReferenceToMember_BuildTextExpansion_String_) object).index  != this.index ) return false;
        return true;
    }

    public int hashCode () {
        return (this.map.hashCode() ^ this.index.hashCode());
    }
}
