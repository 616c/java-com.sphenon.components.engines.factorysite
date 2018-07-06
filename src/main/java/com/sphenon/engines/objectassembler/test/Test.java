package com.sphenon.engines.objectassembler.test;

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

import com.sphenon.engines.factorysite.factories.Factory_Aggregate;

public class Test {

    public static void main(String[] args) {

        Planet p = (Planet) Factory_Aggregate.construct("file:example1.ocp");

        System.out.println("Hello " + p.getName() + "!\n");
    }
}
