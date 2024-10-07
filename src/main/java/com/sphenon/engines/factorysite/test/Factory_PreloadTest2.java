package com.sphenon.engines.factorysite.test;

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

public class Factory_PreloadTest2
{
    public void setZwei(CallContext context, String zwei) {
        System.err.println("PreloadTest, Factory ZWEI, Parameter " + zwei);
    }

    public PreloadTest create (CallContext context) {
        System.err.println("PreloadTest, Factory ZWEI");
        return new PreloadTest(context);
    }
}
