package com.sphenon.engines.factorysite.test;

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

import com.sphenon.basics.context.*;

public class Factory_StringConcatenator
{
    private String s1;
    private String s2;

    public void setString1(CallContext context, String s1) {
        this.s1 = s1;
    }

    public void setString2(CallContext context, String s2) {
        this.s2 = s2;
    }

    public String create (CallContext context) {
        return s1 + s2;
    }
}
