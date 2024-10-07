package com.sphenon.engines.factorysite.yaml;

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
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class BuildTextYAMLDatalocator extends BuildTextYAMLDatainfo {

    public BuildTextYAMLDatalocator (String locator) {
        this.locator = locator;
    }

    public BuildTextYAMLDatalocator (String locator, String locator_base) {
        this.locator = locator;
        if (locator_base.charAt(0) == '#') {
            this.locator_base_o_id_ref = locator_base.substring(1);
        } else if (locator_base.charAt(0) == '$') {
            this.locator_base_parameter = locator_base.substring(1);
        } else {
            this.locator_base = locator_base;
        }
    }
}
