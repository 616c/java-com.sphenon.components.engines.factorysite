// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/MapReferenceToMember.javatpl

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/
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

public class MapReferenceToMember_ScaffoldParameter_String_
    implements ReferenceToMember_ScaffoldParameter_String_Map_ScaffoldParameter_String__
{
    private Map_ScaffoldParameter_String_    map;
    private String                  index;

    public MapReferenceToMember_ScaffoldParameter_String_ (CallContext context, Map_ScaffoldParameter_String_ map, String index) {
        this.map    = map;
        this.index  = index;
        assert map.canGet(context, this.index) : SystemStateMessage.create(context, MessageText.create(context, "MapIndex created with invalid index '%(index)'", "index", index), ProblemState.ERROR);
    }

    public Map_ScaffoldParameter_String_ getContainer (CallContext context) {
        return this.map;
    }

    public String getIndex (CallContext context) {
        return this.index;
    }

    public ScaffoldParameter get (CallContext context) {
        try {
            return map.get(context, this.index);
        } catch (DoesNotExist dne) {
            CustomaryContext.create(Context.create(context)).throwPreConditionViolation(context, dne, "MapIndex contains invalid index '%(index)'", "index", index);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public boolean equals (Object object) {
        if (object == null) return false;
        if (! (object instanceof MapReferenceToMember_ScaffoldParameter_String_)) return false;
        if (((MapReferenceToMember_ScaffoldParameter_String_) object).map != this.map) return false;
        if (((MapReferenceToMember_ScaffoldParameter_String_) object).index  != this.index ) return false;
        return true;
    }

    public int hashCode () {
        return (this.map.hashCode() ^ this.index.hashCode());
    }
}
