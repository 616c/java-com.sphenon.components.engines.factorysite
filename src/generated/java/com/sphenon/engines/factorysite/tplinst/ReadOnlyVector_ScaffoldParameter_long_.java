// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/ReadOnlyVector.javatpl

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

import com.sphenon.basics.many.*;
import com.sphenon.basics.many.returncodes.*;

public interface ReadOnlyVector_ScaffoldParameter_long_
  extends ReadVector_ScaffoldParameter_long_,
          ReadOnlyVector<ScaffoldParameter>,
          OfKnownSize
{
    public ScaffoldParameter                                    get             (CallContext context, long index) throws DoesNotExist;
    public ScaffoldParameter                                    tryGet          (CallContext context, long index);
    public boolean                                     canGet          (CallContext context, long index);

    public ReferenceToMember_ScaffoldParameter_long_ReadOnlyVector_ScaffoldParameter_long__  getReference    (CallContext context, long index) throws DoesNotExist;
    public ReferenceToMember_ScaffoldParameter_long_ReadOnlyVector_ScaffoldParameter_long__  tryGetReference (CallContext context, long index);
}

