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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.expression.*;

import com.sphenon.engines.factorysite.*;

/* ok - Variante mit "C·" funktioniert in XML, not yet in JSON,
   looks like BuildText...JSON... scans for "@" in general;
   should be easy to solve; anyway, could worth a thought
   to not use ugly "C·" in json, but nicer "Ⓒ" variant... */

/* https://www.w3.org/TR/REC-xml/#NT-Name */

/* these SHOULD work, but don't (xerces bug???)
   (careful, these are 'fullwidth' characters - uh, oh)
           FF01 ！ FF02 ＂ FF03 ＃ FF04 ＄ FF05 ％ FF06 ＆ FF07 ＇
   FF08 （ FF09 ） FF0A ＊ FF0B ＋ FF0C ， FF0D － FF0E ． FF0F ／
   FF10 ０ FF11 １ FF12 ２ FF13 ３ FF14 ４ FF15 ５ FF16 ６ FF17 ７
   FF18 ８ FF19 ９ FF1A ： FF1B ； FF1C ＜ FF1D ＝ FF1E ＞ FF1F ？
   FF20 ＠ FF21 Ａ FF22 Ｂ FF23 Ｃ FF24 Ｄ FF25 Ｅ FF26 Ｆ FF27 Ｇ
   FF28 Ｈ FF29 Ｉ FF2A Ｊ FF2B Ｋ FF2C Ｌ FF2D Ｍ FF2E Ｎ FF2F Ｏ
   FF30 Ｐ FF31 Ｑ FF32 Ｒ FF33 Ｓ FF34 Ｔ FF35 Ｕ FF36 Ｖ FF37 Ｗ
   FF38 Ｘ FF39 Ｙ FF3A Ｚ FF3B ［ FF3C ＼ FF3D ］ FF3E ＾ FF3F ＿
   FF40 ｀ FF41 ａ FF42 ｂ FF43 ｃ FF44 ｄ FF45 ｅ FF46 ｆ FF47 ｇ
   FF48 ｈ FF49 ｉ FF4A ｊ FF4B ｋ FF4C ｌ FF4D ｍ FF4E ｎ FF4F ｏ
   FF50 ｐ FF51 ｑ FF52 ｒ FF53 ｓ FF54 ｔ FF55 ｕ FF56 ｖ FF57 ｗ
   FF58 ｘ FF59 ｙ FF5A ｚ FF5B ｛ FF5C ｜ FF5D ｝ FF5E ～ FF5F ｟ */

/* these are not allowed as xml attribute names:
   24B6 Ⓐ  24B7 Ⓑ  24B8 Ⓒ  24B9 Ⓓ  24BA Ⓔ  24BB Ⓕ  24BC Ⓖ  24BD Ⓗ
   24BE Ⓘ  24BF Ⓙ  24C0 Ⓚ  24C1 Ⓛ  24C2 Ⓜ  24C3 Ⓝ  24C4 Ⓞ  24C5 Ⓟ
   24C6 Ⓠ  24C7 Ⓡ  24C8 Ⓢ  24C9 Ⓣ  24CA Ⓤ  24CB Ⓥ  24CC Ⓦ  24CD Ⓧ
   24CE Ⓨ  24CF Ⓩ  24D0 ⓐ  24D1 ⓑ  24D2 ⓒ  24D3 ⓓ  24D4 ⓔ  24D5 ⓕ
   24D6 ⓖ  24D7 ⓗ  24D8 ⓘ  24D9 ⓙ  24DA ⓚ  24DB ⓛ  24DC ⓜ  24DD ⓝ
   24DE ⓞ  24DF ⓟ  24E0 ⓠ  24E1 ⓡ  24E2 ⓢ  24E3 ⓣ  24E4 ⓤ  24E5 ⓥ
   24E6 ⓦ  24E7 ⓧ  24E8 ⓨ  24E9 ⓩ  24EA ⓪  24EB ⓫  24EC ⓬  24ED ⓭
   24EE ⓮  24EF ⓯  24F0 ⓰  24F1 ⓱  24F2 ⓲  24F3 ⓳  24F4 ⓴  24F5 ⓵
   24F6 ⓶  24F7 ⓷  24F8 ⓸  24F9 ⓹  24FA ⓺  24FB ⓻  24FC ⓼  24FD ⓽
   24FE ⓾  24FF ⓿  2047 ⁇  2200 ∀ */

public class BuildTextKeywords {

    final static public String META                   = "META";
    final static public String Meta                   = "Meta";
    final static public String NAMESPACE              = "NAMESPACE";
    final static public String NameSpace              = "NameSpace";
    final static public String OCPID                  = "OCPID";
    final static public String OCPId                  = "OCPId";
    final static public String OPTIONALPARAMETER      = "OPTIONALPARAMETER";
    final static public String OptionalParameter      = "OptionalParameter";
    final static public String OIDREF                 = "OIDREF";
    final static public String OIdRef                 = "OIdRef";
    final static public String IDREF                  = "IDREF";
    final static public String IdRef                  = "IdRef";
    final static public String OPTIONALIDREF          = "OPTIONALIDREF";
    final static public String OptionalIdRef          = "OptionalIdRef";
    final static public String NAME                   = "NAME";
    final static public String Name                   = "Name";
    final static public String PARAMETER              = "PARAMETER";
    final static public String Parameter              = "Parameter";
    final static public String PARAMETER_UC           = "P\u00B7"; // 24C5 FF30 P·
    final static public String OID                    = "OID";
    final static public String OId                    = "OId";
    final static public String OID_UC                 = "I\u00B7"; // 24BE FF29 I·
    final static public String ASSIGNTO               = "ASSIGNTO";
    final static public String AssignTo               = "AssignTo";
    final static public String CLASS                  = "CLASS";
    final static public String Class                  = "Class";
    final static public String CLASS_UC               = "C\u00B7"; // 24B8 FF23 C·
    final static public String OUT                    = "OUT";
    final static public String Out                    = "Out";
    final static public String FACTORY                = "FACTORY";
    final static public String Factory                = "Factory";
    final static public String FACTORY_UC             = "F\u00B7"; // 24BB FF26 F·
    final static public String RETRIEVER              = "RETRIEVER";
    final static public String Retriever              = "Retriever";
    final static public String RETRIEVER_UC           = "R\u00B7"; // 24C7 FF32 R·
    final static public String METHOD                 = "METHOD";
    final static public String Method                 = "Method";
    final static public String TYPECHECK              = "TYPECHECK";
    final static public String TypeCheck              = "TypeCheck";
    final static public String ARGUMENTCHECK          = "ARGUMENTCHECK";
    final static public String ArgumentCheck          = "ArgumentCheck";
    final static public String SINGLETON              = "SINGLETON";
    final static public String Singleton              = "Singleton";
    final static public String DYNAMICPARAMETERS      = "DYNAMICPARAMETERS";
    final static public String DynamicParameters      = "DynamicParameters";
    final static public String LISTENER               = "LISTENER";
    final static public String Listener               = "Listener";
    final static public String CONTENT                = "CONTENT";
    final static public String Content                = "Content";
    final static public String COMPONENTTYPE          = "COMPONENTTYPE";
    final static public String ComponentType          = "ComponentType";
    final static public String ALIAS                  = "ALIAS";
    final static public String Alias                  = "Alias";
    final static public String IF                     = "IF";
    final static public String If                     = "If";
    final static public String IF_UC                  = "?\u00B7"; // 2047 FF1F ?· ⁇
    final static public String FOREACH                = "FOREACH";
    final static public String ForEach                = "ForEach";
    final static public String FOREACH_UC             = "*\u00B7"; // 2200 FF0A *· ∀
    final static public String EXPRESSION             = "EXPRESSION";
    final static public String Expression             = "Expression";
    final static public String EXPRESSION_UC          = "E\u00B7"; // 24BA FF25 E·
    final static public String EXPRESSION_TEXT        = "EXPRESSION-TEXT";
    final static public String ExpressionText         = "ExpressionText";
    final static public String EXPRESSION_TEXT_UC     = "ET\u00B7"; // 24BA 24C9 FF25 FF34 ET·
    final static public String EXPRESSION_VALUE       = "EXPRESSION-VALUE";
    final static public String ExpressionValue        = "ExpressionValue";
    final static public String EXPRESSION_VALUE_UC    = "EV\u00B7"; // 24BA 24CB FF25 FF36 EV·
    final static public String SIGNATURE              = "SIGNATURE";
    final static public String Signature              = "Signature";
    final static public String SIGNATURE_UC           = "S\u00B7"; // 24C8 FF33 S·
    final static public String DEFINE                 = "DEFINE";
    final static public String Define                 = "Define";
    final static public String DEFINE_UC              = "D\u00B7"; // 24B9 FF24 D·
    final static public String EVALUATOR              = "EVALUATOR";
    final static public String Evaluator              = "Evaluator";
    final static public String CATCH                  = "CATCH";
    final static public String Catch                  = "Catch";
    final static public String APPLIESTO              = "APPLIESTO";
    final static public String AppliesTo              = "AppliesTo";
    final static public String PASS                   = "PASS";
    final static public String Pass                   = "Pass";
    final static public String LOCATOR                = "LOCATOR";
    final static public String Locator                = "Locator";
    final static public String LOCATOR_UC             = "L\u00B7"; // 24C1 FF2C L·
    final static public String LOCATORBASE            = "LOCATORBASE";
    final static public String LocatorBase            = "LocatorBase";
    final static public String LOCATORBASEOIDREF      = "LOCATORBASEOIDREF";
    final static public String LocatorBaseOIdRef      = "LocatorBaseOIdRef";
    final static public String LOCATORBASEPARAMETER   = "LOCATORBASEPARAMETER";
    final static public String LocatorBaseParameter   = "LocatorBaseParameter";
    final static public String BASE                   = "BASE";
    final static public String Base                   = "Base";
    final static public String POLYMORPHIC            = "POLYMORPHIC";
    final static public String Polymorphic            = "Polymorphic";
    final static public String OVERRIDE               = "OVERRIDE";
    final static public String Override               = "Override";
    final static public String COMPLEX                = "COMPLEX";
    final static public String Complex                = "Complex";
    final static public String NULL                   = "NULL";
    final static public String Null                   = "Null";
    final static public String SWITCH                 = "SWITCH";
    final static public String Switch                 = "Switch";
    final static public String CODE                   = "CODE";
    final static public String Code                   = "Code";

    final static public String ATTRIBUTE_RE =
                                   "(" + OCPID + ")|"
                                 + "(" + OPTIONALPARAMETER + ")|"
                                 + "(" + OIDREF + ")|"
                                 + "(" + IDREF + ")|"
                                 + "(" + OPTIONALIDREF + ")|"
                                 + "(" + NAME + ")|"
                                 + "(" + PARAMETER + ")|" + "(" + PARAMETER_UC + ")|"
                                 + "(" + OID + ")|" + "(" + OID_UC + ")|"
                                 + "(" + ASSIGNTO + ")|"
                                 + "(" + CLASS + ")|" + "(" + CLASS_UC + ")|"
                                 + "(" + OUT + ")|"
                                 + "(" + FACTORY + ")|" + "(" + FACTORY_UC + ")|"
                                 + "(" + RETRIEVER + ")|" + "(" + RETRIEVER_UC + ")|"
                                 + "(" + METHOD + ")|"
                                 + "(" + TYPECHECK + ")|"
                                 + "(" + ARGUMENTCHECK + ")|"
                                 + "(" + SINGLETON + ")|"
                                 + "(" + DYNAMICPARAMETERS + ")|"
                                 + "(" + LISTENER + ")|"
                                 + "(" + CONTENT + ")|"
                                 + "(" + COMPONENTTYPE + ")|"
                                 + "(" + ALIAS + ")|"
                                 + "(" + IF + ")|" + "(" + "\\" + IF_UC + ")|"
                                 + "(" + FOREACH + ")|" + "(" + "\\" + FOREACH_UC + ")|"
                                 + "(" + EXPRESSION + ")|" + "(" + EXPRESSION_UC + ")|"
                                 + "(" + EXPRESSION_TEXT + ")|" + "(" + EXPRESSION_TEXT_UC + ")|"
                                 + "(" + EXPRESSION_VALUE + ")|" + "(" + EXPRESSION_VALUE_UC + ")|"
                                 + "(" + SIGNATURE + ")|" + "(" + SIGNATURE_UC + ")|"
                                 + "(" + DEFINE + ")|" + "(" + DEFINE_UC + ")|"
                                 + "(" + EVALUATOR + ")|"
                                 + "(" + CATCH + ")|"
                                 + "(" + APPLIESTO + ")|"
                                 + "(" + PASS + ")|"
                                 + "(" + LOCATOR + ")|" + "(" + LOCATOR_UC + ")|"
                                 + "(" + LOCATORBASE + ")|"
                                 + "(" + LOCATORBASEOIDREF + ")|"
                                 + "(" + LOCATORBASEPARAMETER + ")|"
                                 + "(" + BASE + ")|"
                                 + "(" + POLYMORPHIC + ")|"
                                 + "(" + OVERRIDE + ")|"
                                 + "(" + COMPLEX + ")";

    final static public String ATTRIBUTE_POSTFIX_RE =
                                   "(" + OIDREF + ")|"
                                 + "(" + IDREF + ")|"
                                 + "(" + PARAMETER + ")|" + "(" + PARAMETER_UC + ")|"
                                 + "(" + EXPRESSION_TEXT + ")|" + "(" + EXPRESSION_TEXT_UC + ")|"
                                 + "(" + EXPRESSION_VALUE + ")|" + "(" + EXPRESSION_VALUE_UC + ")|"
                                 + "(" + LOCATOR + ")|" + "(" + LOCATOR_UC + ")|";
}
