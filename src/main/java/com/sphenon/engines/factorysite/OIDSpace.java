package com.sphenon.engines.factorysite;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.many.returncodes.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.returncodes.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.engines.aggregator.*;

import java.util.Map;
import java.util.HashMap;

public class OIDSpace {
    public OIDSpace (CallContext context) {
    }

    protected Map<Object,String> defined_oids;
    protected Map<Object,String> referenced_oids;
    protected int next_oid = 0;

    public boolean isDefined(CallContext context, Object object) {
        return (this.defined_oids != null && this.defined_oids.get(object) != null) ? true : false;
    }

    public String define(CallContext context, Object object) {
        if (this.defined_oids == null) {
            this.defined_oids = new HashMap<Object,String>();
        }
        String oid = this.defined_oids.get(object);
        if (oid == null && this.referenced_oids != null) {
            oid = this.referenced_oids.get(object);
        }
        if (oid == null) {
            oid = (new Integer(next_oid++)).toString();
            this.defined_oids.put(object, oid);
        }
        return oid;
    }

    public String getReference(CallContext context, Object object) {
        String oid = this.defined_oids == null ? null : this.defined_oids.get(object);
        if (oid == null) {
            if (this.referenced_oids == null) {
                this.referenced_oids = new HashMap<Object,String>();
            }
            oid = this.referenced_oids.get(object);
        }
        if (oid == null && isExternal(context, object) == false) {
            oid = (new Integer(next_oid++)).toString();
            this.referenced_oids.put(object, oid);
        }
        return oid;
    }

    public String getDefinedReference(CallContext context, Object object) {
        String oid = this.defined_oids == null ? null : this.defined_oids.get(object);
        return oid;
    }

    protected RegularExpression define_only_explicitly_include;
    protected RegularExpression define_only_explicitly_exclude;

    public boolean defineOnlyExplicitly(CallContext context, Object object) {
        return (    (    define_only_explicitly_include != null
                      && define_only_explicitly_include.matches(context, object.getClass().getName()) == true
                    )
                 && (    define_only_explicitly_exclude == null
                      || define_only_explicitly_exclude.matches(context, object.getClass().getName()) == false
                    )
               );
    }

    public void setDefineOnOccurenceFilter(CallContext context, String define_only_explicitly_include, String define_only_explicitly_exclude) {
        this.define_only_explicitly_include = define_only_explicitly_include == null ? null : new RegularExpression(context, define_only_explicitly_include);
        this.define_only_explicitly_exclude = define_only_explicitly_exclude == null ? null : new RegularExpression(context, define_only_explicitly_exclude);
    }

    protected boolean isExternal(CallContext context, Object object) {
        return false;
    }
}
