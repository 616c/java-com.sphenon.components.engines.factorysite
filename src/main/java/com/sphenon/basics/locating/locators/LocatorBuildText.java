package com.sphenon.basics.locating.locators;

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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.javaresources.factories.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.variatives.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;

import java.util.Vector;
import java.util.regex.*;

public class LocatorBuildText extends Locator {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.locators.LocatorBuildText"); };

    static protected Configuration config;
    static { config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.basics.locating.locators.LocatorBuildText"); };

    public LocatorBuildText (CallContext context, String text_locator_value, Locator sub_locator, String locator_class_parameter_string) {
        super(context, text_locator_value, sub_locator, locator_class_parameter_string);
    }

    /* Parser States -------------------------------------------------------------------- */
    
    static protected LocatorParserState[] locator_parser_states;
        
    protected LocatorParserState[] getParserStates(CallContext context) {
        if (locator_parser_states == null) {
            String att_trans  = ".*:" + BuildTextComplexXML.fs_attribute_re + ":String|Object-BuildText:4";
            String main_trans = "IDREF::String:3,OPTIONALIDREF::String:3,FACTORY::String:3,RETRIEVER::String:3,LOCATOR::String:3,APPLIESTO::String:3," + att_trans;
            locator_parser_states = new LocatorParserState[] {

                // parser state: default_attribute  transitions  is_final  can_terminate  target_type
                // transition  : key_include_regexp  key_exclude_regexp  allowed  next_state
                // allowed     : String | Object [- locator_default_type]

                new LocatorParserState(context, "CLASS" , "NAME::String:1,CLASS::String:2,OID::String:3," + main_trans, false, true, null),
                new LocatorParserState(context, "CLASS" , "CLASS::String:2,OID::String:3," + main_trans, false, true, null),
                new LocatorParserState(context, "OID"   , "OID::String:3," + main_trans, false, true, null),
                new LocatorParserState(context, ""      , main_trans, false, true, null),
                new LocatorParserState(context, ""      , att_trans, false, true, null)
            };
        }
        return locator_parser_states;
    }

    /* Base Acceptors ------------------------------------------------------------------- */

    static protected Vector<LocatorBaseAcceptor> locator_base_acceptors;

    static protected Vector<LocatorBaseAcceptor> initBaseAcceptors(CallContext context) {
        if (locator_base_acceptors == null) {
            locator_base_acceptors = new Vector<LocatorBaseAcceptor>();
        }
        return locator_base_acceptors;
    }

    protected Vector<LocatorBaseAcceptor> getBaseAcceptors(CallContext context) {
        return initBaseAcceptors(context);
    }

    static public void addBaseAcceptor(CallContext context, LocatorBaseAcceptor base_acceptor) {
        initBaseAcceptors(context).add(base_acceptor);
    }
    
    /* ---------------------------------------------------------------------------------- */

    public String getTargetVariableName(CallContext context) {
        return "aggregate";
    }

    protected boolean isTargetCacheable(CallContext context) throws InvalidLocator {
        return false;
    }

    protected Object retrieveLocalTarget(CallContext context) throws InvalidLocator {
        // Object base = lookupBaseObject(context, true);

        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Retrieving local target of BuildText Locator '%(textlocator)'...", "textlocator", this.text_locator_value); }

        LocatorStep[] steps = getLocatorSteps(context);

        String ocpid = "[Locator: " + this.text_locator_value + "]";

        String NAME          = "";
        String CLASS         = "";
        String OID           = "";
        String IDREF         = "";
        String OPTIONALIDREF = "";
        String FACTORY       = "";
        String RETRIEVER     = "";
        String LOCATOR       = "";
        String APPLIESTO     = "";

        Vector<Pair_BuildText_String_> pbtss = new Vector<Pair_BuildText_String_>();

        for (LocatorStep step : steps) {
            if (step.getAttribute(context).equals("NAME"))          { NAME          = step.getValue(context); continue; }
            if (step.getAttribute(context).equals("CLASS"))         { CLASS         = step.getValue(context); continue; }
            if (step.getAttribute(context).equals("OID"))           { OID           = step.getValue(context); continue; }
            if (step.getAttribute(context).equals("IDREF"))         { IDREF         = step.getValue(context); continue; }
            if (step.getAttribute(context).equals("OPTIONALIDREF")) { OPTIONALIDREF = step.getValue(context); continue; }
            if (step.getAttribute(context).equals("FACTORY"))       { FACTORY       = step.getValue(context); continue; }
            if (step.getAttribute(context).equals("RETRIEVER"))     { RETRIEVER     = step.getValue(context); continue; }
            if (step.getAttribute(context).equals("LOCATOR"))       { LOCATOR       = step.getValue(context);
                if (LOCATOR != null && LOCATOR.length() != 0) {
                    RETRIEVER = "com.sphenon.basics.locating.retrievers.RetrieverByTextLocator";
                }
                continue;
            }
            if (step.getAttribute(context).equals("APPLIESTO"))     { APPLIESTO     = step.getValue(context); continue; }

            BuildText bt = null;
            String an = null;
            
            if (step.getAttribute(context) == null || step.getAttribute(context).isEmpty()) {
                bt = new BuildTextSimple_String (context, "", "", "java.lang.String", "", "", step.getValue(context), ocpid);
                an = "";
            } else {
                if (step.getLocator(context) != null) {
                    bt = (BuildText) step.getLocator(context).retrieveTarget(context);
                } else {
                    bt = new BuildTextComplex_String(context, "", "", "", "", "", ocpid, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "java.lang.String", "", "", step.getValue(context), ocpid), ""));
                }
                an = step.getAttribute(context);
            }

            pbtss.add(new Pair_BuildText_String_(context, bt, an));
        }

        BuildText result;

        if (IDREF != null && IDREF.length() != 0) {
            result = new BuildTextRefById_String(context, OID, "", CLASS, IDREF, ocpid);
        } else if (OPTIONALIDREF != null && OPTIONALIDREF.length() != 0) {
            result = new BuildTextOptionalRefById_String(context, OID, CLASS, new BuildTextRefById_String(context, "", "", CLASS, OPTIONALIDREF, ocpid), createComplex(context, "", CLASS, FACTORY, RETRIEVER, ocpid, LOCATOR, pbtss), ocpid);
        } else {
            result = createComplex(context, OID, CLASS, FACTORY, RETRIEVER, ocpid, LOCATOR, pbtss);
        }

        if (NAME != null && NAME.length() != 0) {
            if (NAME.matches("^[A-Za-z0-9_]+$")) {
                result.setNodeName(context, NAME);
            } else {
                result.setNameAttribute(context, NAME);
            }
        }

        if (APPLIESTO != null) {
            result.setAppliesTo(context, APPLIESTO);
        }

        return result;
    }

    protected BuildTextBaseImpl createComplex(CallContext context, String OID, String CLASS, String FACTORY, String RETRIEVER, String ocpid, String LOCATOR, Vector<Pair_BuildText_String_> pbtss) {
        BuildTextComplex_String bpcs = new BuildTextComplex_String (context, OID, "", CLASS, FACTORY, RETRIEVER, ocpid);
        if (LOCATOR != null && LOCATOR.length() != 0) {
            bpcs.setAllowDynamicTypeCheck(context, true);
            bpcs.getItems(context).append(context, new Pair_BuildText_String_(context, new BuildTextComplex_String(context, "", "", "", "", "", ocpid, new Pair_BuildText_String_(context, new BuildTextSimple_String (context, "", "", "String", "", "", LOCATOR, ocpid), "")), "TextLocator"));
        }
        for (Pair_BuildText_String_ pbts : pbtss) {
            bpcs.getItems(context).append(context, pbts);
        }
        return bpcs;
    }
}
