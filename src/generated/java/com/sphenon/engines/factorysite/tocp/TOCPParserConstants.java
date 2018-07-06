/* Generated By:JavaCC: Do not edit this line. TOCPParserConstants.java */

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
package com.sphenon.engines.factorysite.tocp;

public interface TOCPParserConstants {

  int EOF = 0;
  int IDENTIFIER = 1;
  int NUMBER = 2;
  int QUOTED = 3;
  int WS = 4;
  int EQ = 5;
  int COMMA = 6;
  int OBC = 7;
  int CBC = 8;
  int OBK = 9;
  int CBK = 10;
  int LT = 11;
  int GT = 12;
  int HASH = 13;
  int MINUS = 14;
  int PLUS = 15;
  int QMARK = 16;
  int PERCENT = 17;
  int EMARK = 18;
  int CARET = 19;
  int TILDE = 20;
  int STAR = 21;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "<IDENTIFIER>",
    "<NUMBER>",
    "<QUOTED>",
    "<WS>",
    "\"=\"",
    "\",\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\"<\"",
    "\">\"",
    "\"#\"",
    "\"-\"",
    "\"+\"",
    "\"?\"",
    "\"%\"",
    "\"!\"",
    "\"^\"",
    "\"~\"",
    "\"*\"",
  };

}
