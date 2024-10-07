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

import java.beans.*;

public class BuildTextYAMLMetainfoBeanInfo extends SimpleBeanInfo {

    public BuildTextYAMLMetainfoBeanInfo() throws IntrospectionException {
        BeanInfo original_bean_info = Introspector.getBeanInfo(com.sphenon.engines.factorysite.yaml.BuildTextYAMLMetainfo.class, Introspector.IGNORE_IMMEDIATE_BEANINFO);
        PropertyDescriptor[] original_descriptors = original_bean_info.getPropertyDescriptors();
        this.property_descriptors = new PropertyDescriptor[original_descriptors.length - 1];
        for (int i=0, j=0; i<original_descriptors.length; i++) {
            if (original_descriptors[i].getName().equals("class")) {
                // skipped
            } else if (original_descriptors[i].getName().equals("JClass")) {
                this.property_descriptors[j++] = new PropertyDescriptor("class", BuildTextYAMLMetainfo.class, "getJClass", "setJClass");
            } else {
                this.property_descriptors[j++] = original_descriptors[i];
            }
        }
    }

    protected PropertyDescriptor[] property_descriptors;

    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.property_descriptors;
    }
}
