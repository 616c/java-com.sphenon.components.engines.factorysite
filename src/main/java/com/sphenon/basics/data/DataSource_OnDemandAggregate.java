package com.sphenon.basics.data;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.engines.factorysite.factories.*;

import java.util.Hashtable;

public class DataSource_OnDemandAggregate<T>  extends DataSource_OnDemand<T> {
    public DataSource_OnDemandAggregate(CallContext context) {
        super(context);
    }

    protected String aggregate_class;

    public String getAggregateClass (CallContext context) {
        return this.aggregate_class;
    }

    public void setAggregateClass (CallContext context, String aggregate_class) {
        this.aggregate_class = aggregate_class;
    }

    protected TreeNode aggregate_tree_node;

    public TreeNode getAggregateTreeNode (CallContext context) {
        return this.aggregate_tree_node;
    }

    public TreeNode defaultAggregateTreeNode (CallContext context) {
        return null;
    }

    public void setAggregateTreeNode (CallContext context, TreeNode aggregate_tree_node) {
        this.aggregate_tree_node = aggregate_tree_node;
    }

    protected String aggregate_locator;

    public String getAggregateLocator (CallContext context) {
        return this.aggregate_locator;
    }

    public String defaultAggregateLocator (CallContext context) {
        return null;
    }

    public void setAggregateLocator (CallContext context, String aggregate_locator) {
        this.aggregate_locator = aggregate_locator;
    }

    protected Hashtable arguments;

    public Hashtable getArguments (CallContext context) {
        return this.arguments;
    }

    public void setArguments (CallContext context, Hashtable arguments) {
        this.arguments = arguments;
    }

    public Hashtable defaultArguments( CallContext context ){
        return null;
    }
    
    protected T createOnDemandInstance(CallContext context) {
        if (this.aggregate_class != null) {
            return (T) Factory_Aggregate.construct(context, this.aggregate_class, this.arguments);
        } else {
            if (this.aggregate_tree_node == null && this.aggregate_locator != null) {
                try {
                    this.aggregate_tree_node = Factory_TreeNode.construct(context, this.aggregate_locator);
                } catch (ValidationFailure vf) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Aggregate locator '%(locator)' in OnDemandAggregate ('%(class)', '(treenode)') is invalid", "class", this.aggregate_class, "treenode", this.aggregate_tree_node, "locator", this.aggregate_locator);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
            }
            return (T) Factory_Aggregate.construct(context, this.aggregate_tree_node, this.arguments);
        }
    }

    protected String getErrorInfo(CallContext context) {
        return "Aggregate ('" + this.aggregate_class + "', '" + this.aggregate_tree_node + "')";
    }
} 
