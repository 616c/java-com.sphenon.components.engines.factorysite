package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class COCPIndices {
    static final public int COCPItem_BuildText                   =  0;
    static final public int COCPItem_Scaffold                    =  1;
    static final public int COCPItem_Accessor                    =  2;

    static final public String[] items = {
        "COCPItem_BuildText",
        "COCPItem_Scaffold",
        "COCPItem_Accessor"
    };

    static final public int COCPScaffold_Factory                 =  0;
    static final public int COCPScaffold_Retriever               =  1;

    static final public String[] scaffolds = {
        "COCPScaffold_Factory",
        "COCPScaffold_Retriever"
    };

    static final public int COCPBuildTextNull_String              =  0;
    static final public int COCPBuildTextSimple_String            =  1;
    static final public int COCPBuildTextDOM_Node                 =  2;
    static final public int COCPBuildTextJSONRaw_Node             =  3;
    static final public int COCPBuildTextYAMLRaw_Node             =  4;
    static final public int COCPBuildTextComplex_String           =  5;
    static final public int COCPBuildTextRef_String               =  6;
    static final public int COCPBuildTextParameter_String         =  7;
    static final public int COCPBuildTextOptionalParameter_String =  8;
    static final public int COCPBuildTextRefById_String           =  9;
    static final public int COCPBuildTextOptionalRefById_String   = 10;
    static final public int COCPBuildTextSwitch_String            = 11;

    static final public String[] classes = {
        "COCPBuildTextNull_String",
        "COCPBuildTextSimple_String",
        "COCPBuildTextDOM_Node",
        "COCPBuildTextJSONRaw_Node",
        "COCPBuildTextYAMLRaw_Node",
        "COCPBuildTextComplex_String",
        "COCPBuildTextRef_String",
        "COCPBuildTextParameter_String",
        "COCPBuildTextOptionalParameter_String",
        "COCPBuildTextRefById_String",
        "COCPBuildTextOptionalRefById_String",
        "COCPBuildTextSwitch_String"
    };

    static final public int BuildText_NodeName                    =  0;
    static final public int BuildText_OID                         =  1;
    static final public int BuildText_AssignTo                    =  2;
    static final public int BuildText_TypeName                    =  3;
    static final public int BuildText_FactoryName                 =  4;
    static final public int BuildText_RetrieverName               =  5;
    static final public int BuildText_MethodName                  =  6;
    static final public int BuildText_Alias                       =  7;
    static final public int BuildText_AllowDynamicTypeCheck       =  8;
    static final public int BuildText_AllowMissingArguments       =  9;
    static final public int BuildText_IsSingleton                 = 10;
    static final public int BuildText_HaveDynamicParameters       = 11;
    static final public int BuildText_NameAttribute               = 12;
    static final public int BuildText_IsExpression                = 13;
    static final public int BuildText_IfExpression                = 14;
    static final public int BuildText_ForeachExpression           = 15;
    static final public int BuildText_Signature                   = 16;
    static final public int BuildText_Define                      = 17;
    static final public int BuildText_AppliesTo                   = 18;
    static final public int BuildText_Listener                    = 19;
    static final public int BuildText_Catch                       = 20;
    static final public int BuildText_Pass                        = 21;
    static final public int BuildText_Override                    = 22;
    static final public int BuildText_NameSpace                   = 23;
    static final public int BuildText_SourceLocationInfo          = 24;
    static final public int BuildText_MetaData                    = 25;
    static final public int BuildText_PreConditions               = 26;
    static final public int BuildText_PostConditions              = 27;
    static final public int BuildText_PreBuildScripts             = 28;
    static final public int BuildText_PostBuildScripts            = 29;
    // BuildTextSimple
    static final public int BuildText_Text                        = 30;
    // BuildTextDOM
    static final public int BuildText_XMLNode                     = 31;
    static final public int BuildText_JSONNode                    = 32;
    static final public int BuildText_YAMLNode                    = 33;
    // BuildTextComplex
    static final public int BuildText_Items                       = 34;
    static final public int BuildText_ComponentType               = 35;
    // BuildTextRef
    static final public int BuildText_OIDRef                      = 36;
    // BuildTextParameter
    static final public int BuildText_Name                        = 37;
    // BuildTextOptionalParameter
    static final public int BuildText_Parameter                   = 38;
    static final public int BuildText_Fallback_Parameter          = 39;
    // BuildTextRefById
    static final public int BuildText_IdRef                       = 40;
    // BuildTextOptionalRefById
    static final public int BuildText_RefById                     = 41;
    static final public int BuildText_Fallback_RefById            = 42;
    // BuildTextSwitch
    static final public int BuildText_Cases                       = 43;
    //
    static final public int BuildText_ParameterToDeclare          = 44;
    static final public int BuildText_ScaffoldFactory             = 45;
    static final public int BuildText_IsExpanded                  = 46;
    //
    static final public int BuildText_Evaluator                   = 47;
    //
    static final public int BuildText_OCPId                       = 48;
    static final public int BuildText_Base                        = 49;
    static final public int BuildText_Polymorphic                 = 50;

    static final public String[] methods = {
        "BuildText_NodeName",
        "BuildText_OID",
        "BuildText_AssignTo",
        "BuildText_TypeName",
        "BuildText_FactoryName",
        "BuildText_RetrieverName",
        "BuildText_MethodName",
        "BuildText_Alias",
        "BuildText_AllowDynamicTypeCheck",
        "BuildText_AllowMissingArguments",
        "BuildText_IsSingleton",
        "BuildText_HaveDynamicParameters",
        "BuildText_NameAttribute",
        "BuildText_IsExpression",
        "BuildText_IfExpression",
        "BuildText_ForeachExpression",
        "BuildText_Signature",
        "BuildText_Define",
        "BuildText_AppliesTo",
        "BuildText_Listener",
        "BuildText_Catch",
        "BuildText_Pass",
        "BuildText_Override",
        "BuildText_NameSpace",
        "BuildText_SourceLocationInfo",
        "BuildText_MetaData",
        "BuildText_PreConditions",
        "BuildText_PostConditions",
        "BuildText_PreBuildScripts",
        "BuildText_PostBuildScripts",
        "BuildText_Text",
        "BuildText_XMLNode",
        "BuildText_JSONNode",
        "BuildText_YAMLNode",
        "BuildText_Items",
        "BuildText_ComponentType",
        "BuildText_OIDRef",
        "BuildText_Name",
        "BuildText_Parameter",
        "BuildText_Fallback_Parameter",
        "BuildText_IdRef",
        "BuildText_RefById",
        "BuildText_Fallback_RefById",
        "BuildText_Cases",
        "BuildText_ParameterToDeclare",
        "BuildText_ScaffoldFactory",
        "BuildText_IsExpanded",
        "BuildText_Evaluator",
        "BuildText_OCPId",
        "BuildText_Base",
        "BuildText_Polymorphic"
    };

    static final public int COCPScaffoldParameter              =  0;
    static final public int COCPDynamicScaffoldParameter       =  1;

    static final public String[] parameters = {
        "COCPScaffoldParameter",
        "COCPDynamicScaffoldParameter"
    };
}
