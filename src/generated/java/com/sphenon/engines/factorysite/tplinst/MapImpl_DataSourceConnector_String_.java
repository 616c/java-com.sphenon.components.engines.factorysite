// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/MapImpl.javatpl

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
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.returncodes.*;

import java.util.Hashtable;

public class MapImpl_DataSourceConnector_String_
  implements Map_DataSourceConnector_String_
{
    private java.util.Hashtable map;

    public MapImpl_DataSourceConnector_String_ (CallContext context) {
        map = new java.util.Hashtable ();
    }

    public MapImpl_DataSourceConnector_String_ (CallContext context, java.util.Hashtable map ) {
        this.map = map;
    }

    public DataSourceConnector get     (CallContext context, String index) throws DoesNotExist {
        Object item = map.get(index);
        if (item == null) DoesNotExist.createAndThrow(context, "Map entry '%(index)'", "index", com.sphenon.basics.message.t.s(index));
        return (DataSourceConnector) item;
    }

    public DataSourceConnector tryGet  (CallContext context, String index) {
        DataSourceConnector typeditem = (DataSourceConnector) map.get(index);
        return typeditem;
    }

    public boolean  canGet  (CallContext context, String index) {
        return map.containsKey(index);
    }

    public MapReferenceToMember_DataSourceConnector_String_ getReference (CallContext context, String index) throws DoesNotExist {
        if ( ! canGet(context, index)) {
            DoesNotExist.createAndThrow (context);
            throw (DoesNotExist) null; // compiler insists
        }
        return new MapReferenceToMember_DataSourceConnector_String_(context, this, index);
    }

    public MapReferenceToMember_DataSourceConnector_String_ tryGetReference (CallContext context, String index) {
        if ( ! canGet(context, index)) { return null; }
        return new MapReferenceToMember_DataSourceConnector_String_(context, this, index);
    }

    public void     set     (CallContext context, String index, DataSourceConnector item) {
        map.put(index, item);
    }

    public void     add     (CallContext context, String index, DataSourceConnector item) throws AlreadyExists {
        if (map.containsKey(index)) AlreadyExists.createAndThrow (context);
        map.put(index, item);
    }

    public void     replace (CallContext context, String index, DataSourceConnector item) throws DoesNotExist {
        if (!map.containsKey(index)) DoesNotExist.createAndThrow (context);
        map.put(index, item);
    }

    public void     unset   (CallContext context, String index) {
        map.remove(index);
    }

    public void     remove  (CallContext context, String index) throws DoesNotExist {
        if (!map.containsKey(index)) DoesNotExist.createAndThrow (context);
        map.remove(index);
    }

    public IteratorItemIndex_DataSourceConnector_String_ getNavigator (CallContext context) {
        return new MapIteratorImpl_DataSourceConnector_String_ (context, map, this);
    }

    public long     getSize (CallContext context) {
        return map.size();
    }

    // to be used with care
    public java.util.Hashtable getImplementationMap (CallContext context) {
        return this.map;
    }
}

