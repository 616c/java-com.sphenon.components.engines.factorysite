package com.sphenon.engines.factorysite;

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
import com.sphenon.basics.variatives.*;
import com.sphenon.basics.variatives.classes.*;

public class FactorySiteStringPool extends StringPoolClass {
    static protected FactorySiteStringPool singleton = null;

    static public FactorySiteStringPool getSingleton (CallContext cc) {
        if (singleton == null) {
            singleton = new FactorySiteStringPool(cc);
        }
        return singleton;
    }

    static public VariativeString get(CallContext cc, String id) {
        return VariativeStringClass.createVariativeStringClass(cc, id, getSingleton(cc));
    }

    static public String get(CallContext cc, String id, String isolang) {
        return getSingleton(cc).getString(cc, id, isolang);
    }

    protected FactorySiteStringPool (CallContext cc) {
        super(cc);
        /*************************************************/
        /* THE FOLLOWING SECTION IS PARTIALLY GENERATED. */
        /* BE CAREFUL WHEN EDITING MANUALLY !            */
        /*                                               */
        /* See StringPool.java for explanation.          */
        /*************************************************/
        //BEGINNING-OF-STRINGS
        //P-0-com.sphenon.engines.factorysite
        //F-0-0-BuildTextComplexXML.java
        addEntry(cc, "0.0.0", "en", "Node is of DOM type 'ELEMENT_NODE' but cannot be down-cast to Element");
        addEntry(cc, "0.0.0", "de", "Der DOM-Typ des Nodes ist 'ELEMENT_NODE', ein entsprechender down-cast zu 'Element' schlägt jedoch fehl");
        addEntry(cc, "0.0.1", "en", "Text node in complex BuildText ist not plain whitespace, as expected: '%(data)'");
        addEntry(cc, "0.0.1", "de", "Text Node in komplexem BuildText besteht nicht, wie erwartet, ausschließlich aus Whitespace: '%(data)'");
        addEntry(cc, "0.0.2", "en", "RegExp parse error: %(reemsg)");
        addEntry(cc, "0.0.2", "de", "RegExp Parse-Fehler: %(reemsg)");
        addEntry(cc, "0.0.3", "en", "Node type '%(nodetype)' not expected");
        addEntry(cc, "0.0.3", "de", "Unerwarteter Node Typ '%(nodetype)'");
        addEntry(cc, "0.0.4", "en", "Tag with OUT attribute must not have childs (they are ignored)");
        addEntry(cc, "0.0.4", "de", "Ein Tag mit einem OUT Attribut darf keine weiteren Tags beinhalten (sie werden ignoriert)");
        //F-0-1-BuildTextSimpleXML.java
        addEntry(cc, "0.1.0", "en", "Node for SimpleText must be a text node");
        addEntry(cc, "0.1.0", "de", "Der Knoten eines SimpleText muß Text-Knoten sein");
        //F-0-2-BuildTextXMLFactory.java
        addEntry(cc, "0.2.0", "en", "SAX-Parser does not support all expected features");
        addEntry(cc, "0.2.0", "en", "SAX-Parser erfüllt nicht alle Anforderungen");
        //F-0-3-DataSinkSlotImpl.java
        addEntry(cc, "0.3.0", "en", "DataSinkSlot contains no valid DataSink yet (null pointer)");
        addEntry(cc, "0.3.0", "de", "DataSinkSlot enthält noch keine gültige DataSink (null pointer)");
        addEntry(cc, "0.3.1", "en", "DataSinkSlot of type '%(slottype)' rejects DataSink of type '%(type)'");
        addEntry(cc, "0.3.1", "en", "DataSinkSlot vom Typ '%(slottype)' verweigert Annahme von DataSink vom Typ '%(type)'");
        //F-0-4-DataSourceSlotImpl.java
        addEntry(cc, "0.4.0", "en", "DataSourceSlot contains no valid DataSource yet (null pointer)");
        addEntry(cc, "0.4.0", "de", "DataSourceSlot enthält noch keine gültige DataSource (null pointer)");
        addEntry(cc, "0.4.1", "en", "DataSourceSlot of type '%(slottype)' rejects DataSource of type '%(type)'");
        addEntry(cc, "0.4.1", "de", "DataSourceSlot vom Type '%(slottype)' verweigert Annahme von DataSource vom Typ '%(type)'");
        addEntry(cc, "0.4.2", "en", "Cannot change DataSourceSlot type after type is already fixed");
        addEntry(cc, "0.4.2", "de", "DataSourceSlot Type kann nicht geändert werden, nachdem er bereits festgelegt wurde");
        //F-0-5-FactorySiteTextBased.java
        addEntry(cc, "0.5.0", "en", "Putting up '%(type)' Scaffold%(factory)%(oid)%(dynamic)%(missing) (%(info))...");
        addEntry(cc, "0.5.0", "de", "'%(type)'-Gerüst%(factory)%(oid)%(dynamic)%(missing) wird errichtet (%(info))...");
        addEntry(cc, "0.5.1", "en", "Scaffold parameter does not accept 'String', although it reports being one");
        addEntry(cc, "0.5.1", "de", "Scaffold-Parameter akzeptiert einen 'String' nicht, obwohl es sich angeblich um einen solchen handelt");
        addEntry(cc, "0.5.2", "en", "(registering Scaffold with OID #%(oid), type is '%(type)')");
        addEntry(cc, "0.5.2", "de", "(Scaffold wird unter OID #%(oid) verzeichnet, Typ ist '%(type)')");
        addEntry(cc, "0.5.3", "en", "No such OID #%(oid)");
        addEntry(cc, "0.5.3", "de", "OID #%(oid) unbekannt");
        addEntry(cc, "0.5.4", "en", "Reference to OID #%(oidref) expects type '%(expected)', but refered object is of type '%(got)'");
        addEntry(cc, "0.5.4", "de", "Referenz zu OID #%(oidref) erwartet Typ '%(expected)', referenziertes Objekt ist jedoch vom Typ '%(got)'");
        addEntry(cc, "0.5.5", "en", "Cannot put up due to unavailability of required factory '%(factory)'");
        addEntry(cc, "0.5.5", "de", "Aufgrund der Nichtverfügbarkeit einer erforderten Fabrik '%(factory)' kann nicht errichtet werden");
        addEntry(cc, "0.5.6", "en", "Cannot put up due to unavailability of required class '%(type)'");
        addEntry(cc, "0.5.6", "de", "Aufgrund der Nichtverfügbarkeit einer erforderten Klasse '%(type)' kann nicht errichtet werden");
        addEntry(cc, "0.5.7", "en", "Cannot put up due to unavailability of an (appropriate) constructor '%(type)'");
        addEntry(cc, "0.5.7", "de", "Aufgrund der Nichtverfügbarkeit eines erforderten Konstruktors '%(type)' kann nicht errichtet werden");
        addEntry(cc, "0.5.8", "en", "Class '%(class)' does not exist (for parameter '%(parameter)')");
        addEntry(cc, "0.5.8", "de", "Klasse '%(class)' existiert nicht (für Parameter '%(parameter)')");
        addEntry(cc, "0.5.9", "en", "Cannot put up due to unavailability of required factory '%(factory)'");
        addEntry(cc, "0.5.9", "de", "Aufgrund der Nichtverfügbarkeit einer erforderten Fabrik '%(factory)' kann nicht errichtet werden");
        addEntry(cc, "0.5.10", "en", "Cannot put up due to unavailability of required class '%(type)'");
        addEntry(cc, "0.5.10", "de", "Aufgrund der Nichtverfügbarkeit einer erforderten Klasse '%(type)' kann nicht errichtet werden");
        addEntry(cc, "0.5.11", "en", "Cannot put up due to unavailability of an (appropriate) constructor '%(type)'");
        addEntry(cc, "0.5.11", "de", "Aufgrund der Nichtverfügbarkeit eines erforderten Konstruktors '%(type)' kann nicht errichtet werden");
        addEntry(cc, "0.5.12", "en", "In FactorySite '%(siteid)', Scaffold parameter '%(spname)' of type '%(sptype)' does not accept DataSource of type '%(type)'");
        addEntry(cc, "0.5.12", "de", "In FabrikGelände '%(siteid)', Gerüst Parameter '%(spname)' vom Typ '%(sptype)' akzeptiert keine DataSource vom Typ '%(type)'");
        addEntry(cc, "0.5.13", "en", "build_text is neither, as expected, of type BuildTextSimple, BuildTextRef nor of type BuildTextComplex, but of type '%(type)'");
        addEntry(cc, "0.5.13", "en", "build_text ist weder, wie erwartet, vom Typ BuildTextSimple, BuildTextRef noch vom Typ BuildTextComplex, sondern vom Typ '%(type)'");
        addEntry(cc, "0.5.15", "en", "Duplicate OID #%(oid)");
        addEntry(cc, "0.5.15", "de", "Doppelt verwendete OID #%(oid)");
        addEntry(cc, "0.5.16", "en", "Could not create main object");
        addEntry(cc, "0.5.16", "de", "Haupt Objekt konnte nicht erstellt werden");
        addEntry(cc, "0.5.17", "en", "Cannot put up, scaffold creation for '%(type)[F|%(factory)/R|%(retriever)]' failed");
        addEntry(cc, "0.5.17", "de", "Gerüst-Erzeugung schlug fehl, '%(type)[F|%(factory)/R|%(retriever)]' kann nicht errichtet werden");
        addEntry(cc, "0.5.18", "en", "Cannot put up, scaffold creation for '%(type)[F|%(factory)/R|%(retriever)]' failed");
        addEntry(cc, "0.5.18", "de", "Gerüst-Erzeugung schlug fehl, '%(type)[F|%(factory)/R|%(retriever)]' kann nicht errichtet werden");
        addEntry(cc, "0.5.19", "en", "Reference to OID #%(oidref) is used inconsistent: one occurence expects a type '%(expected1)', while the other occurence expects '%(expected2)'");
        addEntry(cc, "0.5.19", "de", "Bezug zu OID #%(oidref) wird inkonsitent gebraucht: an einer Stelle wird ein Typ '%(expected1)' erwartet, an anderer ein '%(expected2)'");
        addEntry(cc, "0.5.20", "en", "Component in collection is not of type '%(comptype)', as expected, but of type '%(partype)'");
        addEntry(cc, "0.5.20", "de", "Ein Element einer Menge ist nicht, wie erwartet, vom Typ '%(comptype)', sondern vom Typ '%(partype)'");
        addEntry(cc, "0.5.21", "en", "Parameter '%(parname)' is used inconsistent: one occurence expects a type '%(expected1)', while the other occurence expects '%(expected2)'");
        addEntry(cc, "0.5.21", "de", "Parameter '%(parname)' wird inkonsitent gebraucht: an einer Stelle wird ein Typ '%(expected1)' erwartet, an anderer ein '%(expected2)'");
        addEntry(cc, "0.5.22", "en", "Factory_Aggregate[%(oid)].create(): setting null parameter '%(key)'");
        addEntry(cc, "0.5.22", "de", "Factory_Aggregate[%(oid)].create(): Null Parameter '%(key)' wird gesetzt");
        //F-0-6-ScaffoldFactory.java.first_version
        addEntry(cc, "0.6.0", "en", "Type of 'Type' but not of 'Type'...?");
        addEntry(cc, "0.6.0", "de", "Vom Typ 'Typ', aber nicht 'Typ'...?");
        addEntry(cc, "0.6.1", "en", "Statement was not intended to be logically reachable");
        addEntry(cc, "0.6.1", "de", "Zeile hätte logisch gesehen nicht erreichbar sein sollen");
        addEntry(cc, "0.6.2", "en", "Factory found ('%(factory)'), but 'create' method returns a '%(returntype)', not a type matching to '%(expected)', as expected");
        addEntry(cc, "0.6.2", "de", "Fabrik gefunden ('%(factory)'), 'create'-Methode liefert jedoch ein '%(returntype)' zurück, und nicht, wie erwartet, einen Typ passend zu '%(expected)'");
        addEntry(cc, "0.6.3", "en", "Factory found ('%(factory)'), but requires more parameters than %(given)");
        addEntry(cc, "0.6.3", "de", "Fabrik gefunden ('%(factory)'), jedoch sind mehr als %(given) Parameter erforderlich");
        addEntry(cc, "0.6.4", "en", "Factory found ('%(factory)'), but parameter #%(index) differs: given '%(given)', but factory requires '%(required)'");
        addEntry(cc, "0.6.4", "de", "Fabrik gefunden ('%(factory)'), Parameter #%(index) paßt jedoch nicht: erhalten '%(given)', Fabrik erfordert '%(required)'");
        addEntry(cc, "0.6.5", "en", "Factory found ('%(factory)'), but method '%(method)' has %(given) parameters, not 2, as expected");
        addEntry(cc, "0.6.5", "de", "Fabrik gefunden ('%(factory)'), '%(method)'-Methode erforderrt jedoch keine %(given) Parameter, sondern 2");
        addEntry(cc, "0.6.6", "en", "Factory found ('%(factory)'), but method's '%(method)' 1st parameter is of type '%(got)', not a 'CallContext', as expected");
        addEntry(cc, "0.6.6", "de", "Fabrik gefunden ('%(factory)'), der erste Parameter der '%(method)'-Methode ist jedoch vom Typ '%(got)', und nicht, wie erwartet, vom Typ 'CallContext'");
        addEntry(cc, "0.6.7", "en", "Factory found ('%(factory)'), but method's '%(method)' 2nd parameter is of type '%(got)', not a type matching to '%(expected)', as expected");
        addEntry(cc, "0.6.7", "de", "Fabrik gefunden ('%(factory)'), der zweite Parameter der '%(method)'-Methode ist jedoch vom Typ '%(got)', und nicht, wie erwartet, von einem zu '%(expected)' passenden Typ");
        addEntry(cc, "0.6.8", "en", "Factory found ('%(factory)'), but no (appropriate) 'create', 'create%(typename)' or 'create[DerivedClass]' method");
        addEntry(cc, "0.6.8", "de", "Fabrik gefunden ('%(factory)'), jedoch keine (geeignete) 'create'-, 'create%(typename)'- oder 'create[DerivedClass]'-Methode");
        addEntry(cc, "0.6.9", "en", "Factory found ('%(factory)'), but does provide 'set' as well as 'set_ParametersAtOnce' methods, which are mutually exclusive for factories");
        addEntry(cc, "0.6.9", "de", "Fabrik gefunden ('%(factory)'), jedoch sowohl 'set' als auch 'set_ParametersAtOnce'-Methoden, was sich bei Fabriken gegenseitig ausschließt");
        addEntry(cc, "0.6.10", "en", "Factory found ('%(factory)'), but does not provide 'set' methods for all parameters");
        addEntry(cc, "0.6.10", "de", "Fabrik gefunden ('%(factory)'), jedoch nicht mit allen erforderlichen 'set'-Methoden für die Parameter");
        addEntry(cc, "0.6.11", "en", "Factory found ('%(factory)'), but interface is invalid");
        addEntry(cc, "0.6.11", "de", "Fabrik gefunden ('%(factory)'), das Interface ist jedoch ungültig");
        addEntry(cc, "0.6.12", "en", "No such class: %(class)");
        addEntry(cc, "0.6.12", "de", "Klasse unbekannt: %(class)");
        addEntry(cc, "0.6.13", "en", "Class '%(class)' has no constructors");
        addEntry(cc, "0.6.13", "de", "Klasse '%(class)' hat keine Konstruktoren");
        addEntry(cc, "0.6.14", "en", "No constructor of class '%(class)' matches");
        addEntry(cc, "0.6.14", "de", "Kein Konstruktor der Klasse '%(class)' paßt");
        //F-0-7-ScaffoldGenericConstructor.java
        addEntry(cc, "0.7.0", "en", "getValueAsObject <%(type)> entry...");
        addEntry(cc, "0.7.0", "de", "getValueAsObject <%(type)> beginnt...");
        addEntry(cc, "0.7.1", "en", "getValueAsObject <%(type)> exit.");
        addEntry(cc, "0.7.1", "de", "getValueAsObject <%(type)> beendet.");
        addEntry(cc, "0.7.2", "en", "Java 'instanceof' operator reports castability to 'java.lang.RuntimeException', but cast failed");
        addEntry(cc, "0.7.2", "de", "Gemäß java's 'instanceof' Operator kann ein Cast zu 'java.lang.RuntimeException' erfolgen, dieser schlägt jedoch fehl");
        addEntry(cc, "0.7.3", "en", "Java 'instanceof' operator reports castability to 'java.lang.Error', but cast failed");
        addEntry(cc, "0.7.3", "de", "Gemäß java's 'instanceof' Operator kann ein Cast zu 'java.lang.Error' erfolgen, dieser schlägt jedoch fehl");
        addEntry(cc, "0.7.4", "en", "Object of type '%(type)' cannot be delivered, object aggregate contains a cyclic reference and there is no factory available (which could provide a precreate method)");
        addEntry(cc, "0.7.4", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, Objekt-Aggregat enthält eine zyklische Referenz und es ist keine Fabrik verfügbar (welche eine precreate-Methode zur Verfügung stellen könnte)");
        addEntry(cc, "0.7.5", "en", "Object of type '%(type)' cannot be delivered, class is abstract (%(info))");
        addEntry(cc, "0.7.5", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, Klasse ist abstrakt (%(info))");
        addEntry(cc, "0.7.6", "en", "Object of type '%(type)' cannot be delivered, constructor is inaccessible (%(info))");
        addEntry(cc, "0.7.6", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, auf Konstruktor kann nicht zugegriffen werden (%(info))");
        addEntry(cc, "0.7.7", "en", "Object of type '%(type)' cannot be delivered, signature mismatch or unwrapping or method invocation (%(info))");
        addEntry(cc, "0.7.7", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, Signatur paßt nicht, oder 'unwrapping or method invocation (%(info))'");
        addEntry(cc, "0.7.8", "en", "Object of type '%(type)' cannot be delivered, constructor throwed an exception (%(info))");
        addEntry(cc, "0.7.8", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, der Konstruktor löste eine Exception aus (%(info))");
        //F-0-8-ScaffoldGenericFactory.java
        addEntry(cc, "0.8.0", "en", "Java 'instanceof' operator reports castability to 'java.lang.RuntimeException', but cast failed");
        addEntry(cc, "0.8.0", "de", "Gemäß java's 'instanceof' Operator kann ein Cast zu 'java.lang.RuntimeException' erfolgen, dieser schlägt jedoch fehl");
        addEntry(cc, "0.8.1", "en", "Java 'instanceof' operator reports castability to 'java.lang.Error', but cast failed");
        addEntry(cc, "0.8.1", "de", "Gemäß java's 'instanceof' Operator kann ein Cast zu 'java.lang.Error' erfolgen, dieser schlägt jedoch fehl");
        addEntry(cc, "0.8.2", "en", "Invalid factory, no (appropriate) constructor (i.e. either no parameters or 'CallContext' parameter)");
        addEntry(cc, "0.8.2", "de", "Ungültige Fabrik, kein (geeigneter) Konstruktor (d.h. entweder ohne Parameter oder 'CallContext' Parameter)");
        addEntry(cc, "0.8.3", "en", "Invalid factory, 'create' method returns a '%(returntype)', not a type matching to '%(expected)', as expected");
        addEntry(cc, "0.8.3", "de", "Ungültige Fabrik, 'create'-Methode liefert ein '%(returntype)' zurück, und nicht, wie erwartet, einen zu '%(expected)' passenden Typ");
        addEntry(cc, "0.8.4", "en", "invalid factory, 'precreate' method returns a '%(returntype)', not a type matching to '%(expected)', as expected.");
        addEntry(cc, "0.8.4", "de", "Ungültige Fabrik, 'precreate'-Methode liefert ein '%(returntype)' zurück, und nicht, wie erwartet, einen zu '%(expected)' passenden Typ");
        addEntry(cc, "0.8.5", "en", "Invalid factory, no (appropriate) 'create' or 'create%(typename)' method");
        addEntry(cc, "0.8.5", "de", "Ungültige Fabrik, keine (geeignete) 'create'- oder 'create%(typename)'-Methode");
        addEntry(cc, "0.8.6", "en", "Invalid factory, provides 'set' as well as 'set_ParametersAtOnce' methods, which are mutually exclusive");
        addEntry(cc, "0.8.6", "de", "Ungültige Fabrik, sowohl 'set'- als auch 'set_ParametersAtOnce'-Methoden vorgefunden, was sich gegensitig ausschließt");
        addEntry(cc, "0.8.7", "en", "Object of type '%(type)' cannot be delivered, object aggregate contains a cyclic reference and factory does not provide a 'precreate' method");
        addEntry(cc, "0.8.7", "de", "Objekt of type '%(type)' kann nicht bereitgestellt werden, Objekt-Aggregat enthält eine zyklische Referent und Fabrik stellt keine 'precreate'-Methode zur Verfügung");
        addEntry(cc, "0.8.8", "en", "Object of type '%(type)' cannot be delivered, factory invocation failed%(cycle), abstract class");
        addEntry(cc, "0.8.8", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, Fabrik-Aufruf schlug fehl%(cycle), abstrakte Klasse");
        addEntry(cc, "0.8.9", "en", "Object of type '%(type)' cannot be delivered, factory invocation failed%(cycle), constructor is inaccessible");
        addEntry(cc, "0.8.9", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, Fabrik-Aufruf schlug fehl%(cycle), auf Konstruktor kann nicht zugegriffen werden");
        addEntry(cc, "0.8.10", "en", "Object of type '%(type)' cannot be delivered, factory invocation failed%(cycle), signature mismatch or unwrapping or method invocation");
        addEntry(cc, "0.8.10", "de", "Objekt vom Typ '%(type)' kann nicht erstellt werden, Fabrik-Aufruf schlug fehl%(cycle), Signatur paßt nicht oder 'unwrapping or method invocation'");
        addEntry(cc, "0.8.11", "en", "Object of type '%(type)' cannot be delivered, factory invocation failed%(cycle), constructor throwed an exception");
        addEntry(cc, "0.8.11", "de", "Objekt vom Typ '%(type)' kann nicht erstellt werden, Fabrik-Aufruf schlug fehl%(cycle), Konstruktor löste eine Exception aus");
        addEntry(cc, "0.8.12", "en", "Object of type '%(type)' cannot be delivered, '%(who)' throwed an exception");
        addEntry(cc, "0.8.12", "de", "Objekt vom Typ '%(type)' kann nicht erstellt werden, '%(who)'-Aufruf schlug fehl");
        //F-0-9-BuildTextRefXML.java
        addEntry(cc, "0.9.0", "en", "OIDREF node has FACTORY attribute ('%(factory)'), which is ignored");
        addEntry(cc, "0.9.0", "de", "OIDREF Node hat FACTORY Attribut ('%(factory)'), es wird ignoriert");
        addEntry(cc, "0.9.1", "en", "OIDREF node has RETRIEVER attribute ('%(retriever)'), which is ignored");
        addEntry(cc, "0.9.1", "de", "OIDREF Node hat RETRIEVER Attribut ('%(retriever)'), es wird ignoriert");
        addEntry(cc, "0.9.2", "en", "NULL node has FACTORY attribute ('%(factory)'), which is ignored");
        addEntry(cc, "0.9.2", "de", "NULL Node hat FACTORY Attribut ('%(factory)'), es wird ignoriert");
        addEntry(cc, "0.9.3", "en", "NULL node has RETRIEVER attribute ('%(retriever)'), which is ignored");
        addEntry(cc, "0.9.3", "de", "NULL Node hat RETRIEVER Attribut ('%(retriever)'), es wird ignoriert");
        addEntry(cc, "0.9.4", "en", "NULL node has OIDREF attribute ('%(retriever)'), which is ignored");
        addEntry(cc, "0.9.4", "de", "NULL Node hat OIDREF Attribut ('%(retriever)'), es wird ignoriert");
        //F-0-10-BuildTextParameterXML.java
        addEntry(cc, "0.10.0", "en", "PARAMETER node has FACTORY attribute ('%(factory)'), which is ignored");
        addEntry(cc, "0.10.0", "de", "PARAMETER Node hat FACTORY Attribut ('%(factory)'), es wird ignoriert");
        addEntry(cc, "0.10.1", "en", "PARAMETER node has RETRIEVER attribute ('%(retriever)'), which is ignored");
        addEntry(cc, "0.10.1", "de", "PARAMETER Node hat RETRIEVER Attribut ('%(retriever)'), es wird ignoriert");
        //F-0-11-ScaffoldGenericRetriever.java
        addEntry(cc, "0.11.0", "en", "Object of type '%(type)' cannot be delivered, object aggregate contains a cyclic reference and retriever does not provide a 'preretrieve' method");
        addEntry(cc, "0.11.0", "de", "Objekt of type '%(type)' kann nicht bereitgestellt werden, Objekt-Aggregat enthält eine zyklische Referent und Abrufer stellt keine 'precreate'-Methode zur Verfügung");
        addEntry(cc, "0.11.1", "en", "Object of type '%(type)' cannot be delivered, retriever invocation failed%(cycle), abstract class");
        addEntry(cc, "0.11.1", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, Abrufer-Aufruf schlug fehl%(cycle), abstrakte Klasse");
        addEntry(cc, "0.11.2", "en", "Object of type '%(type)' cannot be delivered, retriever invocation failed %(cycle), constructor is inaccessible");
        addEntry(cc, "0.11.2", "de", "Objekt vom Typ '%(type)' kann nicht bereitgestellt werden, Abrufer-Aufruf schlug fehl%(cycle), auf Konstruktor kann nicht zugegriffen werden");
        addEntry(cc, "0.11.3", "en", "Object of type '%(type)' cannot be delivered, retriever invocation failed %(cycle), signature mismatch or unwrapping or method invocation");
        addEntry(cc, "0.11.3", "de", "Objekt vom Typ '%(type)' kann nicht erstellt werden, Abrufer-Aufruf schlug fehl%(cycle), Signatur paßt nicht oder 'unwrapping or method invocation'");
        addEntry(cc, "0.11.4", "en", "Java 'instanceof' operator reports castability to 'java.lang.RuntimeException', but cast failed");
        addEntry(cc, "0.11.4", "de", "Gemäß java's 'instanceof' Operator kann ein Cast zu 'java.lang.RuntimeException' erfolgen, dieser schlägt jedoch fehl");
        addEntry(cc, "0.11.5", "en", "Java 'instanceof' operator reports castability to 'java.lang.Error', but cast failed");
        addEntry(cc, "0.11.5", "de", "Gemäß java's 'instanceof' Operator kann ein Cast zu 'java.lang.Error' erfolgen, dieser schlägt jedoch fehl");
        addEntry(cc, "0.11.6", "en", "Object of type '%(type)' cannot be delivered, retriever invocation failed%(cycle), constructor throwed an exception");
        addEntry(cc, "0.11.6", "de", "Objekt vom Typ '%(type)' kann nicht erstellt werden, Abrufer-Aufruf schlug fehl%(cycle), Konstruktor löste eine Exception aus");
        //P-1-com.sphenon.engines.factorysite.factories
        //F-1-0-Factory_Factory_Aggregate.java
        addEntry(cc, "1.0.0", "en", "Factory_Factory_Aggregate: parameter not set: aggregate class");
        addEntry(cc, "1.0.0", "de", "Factory_Factory_Aggregate: Parameter 'aggregate class' wurde nicht übergeben");
        addEntry(cc, "1.0.1", "en", "Factory_Factory_Aggregate: parameter not set: parameters");
        addEntry(cc, "1.0.1", "de", "Factory_Factory_Aggregate: Parameter 'parameters' wurde nicht übergeben");
        //F-1-1-Factory_Hashtable.java
        addEntry(cc, "1.1.0", "en", "Number of names differs from number of values");
        addEntry(cc, "1.1.0", "de", "Anzahl der Namen und Werte ist unterschiedlich");
        //F-1-2-Factory_Aggregate.java
        addEntry(cc, "1.2.0", "en", "Factory_Aggregate: search path not configured (use '.configSearchPath(context, search_path);' )");
        addEntry(cc, "1.2.0", "de", "Factory_Aggregate: Suchpfad nicht konfiguriert (vermittels '.configSearchPath(context, search_path);' )");
        addEntry(cc, "1.2.1", "en", "Factory_Aggregate: parameter not set: aggregate class");
        addEntry(cc, "1.2.1", "de", "Factory_Aggregate: Parameter 'aggregate class' wurde nicht übergeben");
        addEntry(cc, "1.2.2", "en", "Factory_Aggregate: parameter not set: parameters");
        addEntry(cc, "1.2.2", "de", "Factory_Aggregate: Parameter 'parameters' wurde nicht übergeben");
        addEntry(cc, "1.2.3", "en", "Could not read object construction plan (ocp/xml): '%(ocp)'");
        addEntry(cc, "1.2.3", "de", "Objekt-Konstruktions-Plan konnte nicht gelesen werden (ocp/xml): '%(ocp)'");
        addEntry(cc, "1.2.4", "en", "Could not put up factory site");
        addEntry(cc, "1.2.4", "de", "Fabrikgelände konnte nicht errichtet werden");
        addEntry(cc, "1.2.5", "en", "No such parameter (%(key))");
        addEntry(cc, "1.2.5", "de", "Parameter unbekannt (%(key))");
        addEntry(cc, "1.2.6", "en", "Parameter type mismatch (%(key))");
        addEntry(cc, "1.2.6", "de", "Parameter paßt nicht (%(key))");
        addEntry(cc, "1.2.7", "en", "Could not build object aggregate");
        addEntry(cc, "1.2.7", "de", "Objekt-Aggregat konnte nicht gebaut werden");
        addEntry(cc, "1.2.8", "en", "Factory_Aggregate[%(oid)] created");
        addEntry(cc, "1.2.8", "de", "Factory_Aggregate[%(oid)] erzeugt");
        addEntry(cc, "1.2.9", "en", "Factory_Aggregate[%(oid)].create(): entry... (aggregate class: %(aggregate_class))");
        addEntry(cc, "1.2.9", "de", "Factory_Aggregate[%(oid)].create(): beginnt... (Aggregat-Klasse: %(aggregate_class))");
        addEntry(cc, "1.2.10", "en", "Factory_Aggregate[%(oid)].create(): creating new factory site");
        addEntry(cc, "1.2.10", "de", "Factory_Aggregate[%(oid)].create(): Fabrik-Gelände wird neu errichtet");
        addEntry(cc, "1.2.11", "en", "Factory_Aggregate[%(oid)].create(): setting parameter '%(key)'");
        addEntry(cc, "1.2.11", "de", "Factory_Aggregate[%(oid)].create(): Parameter '%(key)' wird gesetzt");
        addEntry(cc, "1.2.12", "en", "Factory_Aggregate[%(oid)].create(): building aggregate...");
        addEntry(cc, "1.2.12", "de", "Factory_Aggregate[%(oid)].create(): Aggregat wird zusammengebaut...");
        addEntry(cc, "1.2.13", "en", "Factory_Aggregate[%(oid)].create(): exit. (aggregate class: %(aggregate_class))");
        addEntry(cc, "1.2.13", "de", "Factory_Aggregate[%(oid)].create(): beendet. (Aggregat-Klasse: %(aggregate_class))");
        addEntry(cc, "1.2.14", "en", "Could not find object construction plan (ocp/xml): '%(ocp)'");
        addEntry(cc, "1.2.14", "de", "Objekt-Konstruktions-Plan wurde nicht gefunden (ocp/xml): '%(ocp)'");
        addEntry(cc, "1.2.15", "en", "Parameter 'AggregateClass' ('%(aggregateclass)') is invalid");
        addEntry(cc, "1.2.15", "de", "Parameter 'AggregateClass' ('%(aggregateclass)') ist ungültig");
        addEntry(cc, "1.2.16", "en", "Parameter 'Parameters' is invalid");
        addEntry(cc, "1.2.16", "de", "Parameter 'Parameters' ist ungültig");
        addEntry(cc, "1.2.17", "en", "Factory_Aggregate[%(oid)].create(): OCP for factory site has changed");
        addEntry(cc, "1.2.17", "de", "Factory_Aggregate[%(oid)].create(): OCP für Fabrikgelände wurde verändert");
        addEntry(cc, "1.2.18", "en", "Factory_Aggregate[%(oid)].create(): no cache entry");
        addEntry(cc, "1.2.18", "de", "Factory_Aggregate[%(oid)].create(): kein Cache Eintrag");
        addEntry(cc, "1.2.19", "en", "Factory_Aggregate[%(oid)].create(): cache entry, created at %(ctime), last modification of file is %(mtime)");
        addEntry(cc, "1.2.19", "de", "Factory_Aggregate[%(oid)].create(): Cache Eintrag, erstellt um %(ctime), letzte Änderung der Datei um %(mtime)");
        addEntry(cc, "1.2.20", "en", "Factory_Aggregate[%(oid)].create(): setting out parameter '%(key)'");
        addEntry(cc, "1.2.20", "de", "Factory_Aggregate[%(oid)].create(): Ausgabe-Parameter '%(key)' wird gesetzt");
        addEntry(cc, "1.2.21", "en", "Could not compile factory site");
        addEntry(cc, "1.2.21", "de", "Fabrikgelände konnte nicht übersetzt werden");
        addEntry(cc, "1.2.22", "en", "Could not open compiled factory site java file '%(file)'");
        addEntry(cc, "1.2.22", "de", "Übersetzte Fabrik-Gelände-Java-Datei '%(file)' konnte nicht geöffnet werden");
        addEntry(cc, "1.2.23", "en", "No type context configured for xml namespace '%(xmlns)'");
        addEntry(cc, "1.2.23", "de", "Für den XML Namespace '%(xmlns)' ist kein Typ-Kontext definiert");
        addEntry(cc, "1.2.24", "en", "Factory_Aggregate[%(oid)].create(): setting optional parameter '%(key)'");
        addEntry(cc, "1.2.24", "de", "Factory_Aggregate[%(oid)].create(): Optionaler Parameter '%(key)' wird gesetzt");
        //P-2-com.sphenon.engines.factorysite.test
        //F-2-0-Erni.java
        addEntry(cc, "2.0.0", "en", "Hey, this is Erni!");
        addEntry(cc, "2.0.0", "de", "Ei verbibschd, hier is der Erni!");
        //F-2-1-Main.java
        addEntry(cc, "2.1.0", "en", "Could not build object aggregate: %(reason)");
        addEntry(cc, "2.1.0", "de", "Objekt-Aggregat konnte nicht gebaut werden: %(reason)");
        addEntry(cc, "2.1.1", "en", "Factory site and object aggregate successfully put up and built");
        addEntry(cc, "2.1.1", "de", "Fabrikgelände und Objekt-Aggregat wurden erfolgreich errichtet und gebaut");
        addEntry(cc, "2.1.2", "en", "Table 1, object 1: %(class) - %(object)");
        addEntry(cc, "2.1.2", "de", "Tabelle 1, Objekt 1: %(class) - %(object)");
        addEntry(cc, "2.1.3", "en", "Table 1, object 2: %(class) - %(object)");
        addEntry(cc, "2.1.3", "de", "Tabelle 1, Objekt 2: %(class) - %(object)");
        addEntry(cc, "2.1.4", "en", "Table 1, object 3: %(class) - %(object)");
        addEntry(cc, "2.1.4", "de", "Tabelle 1, Objekt 3: %(class) - %(object)");
        addEntry(cc, "2.1.5", "en", "Table 1, object 4: %(class) - %(object)");
        addEntry(cc, "2.1.5", "de", "Tabelle 1, Objekt 4: %(class) - %(object)");
        addEntry(cc, "2.1.6", "en", "Table 1, object 5: %(class) - %(object)");
        addEntry(cc, "2.1.6", "de", "Tabelle 1, Objekt 5: %(class) - %(object)");
        addEntry(cc, "2.1.7", "en", "Table 1, object 6: %(class) - %(object)");
        addEntry(cc, "2.1.7", "de", "Tabelle 1, Objekt 6: %(class) - %(object)");
        addEntry(cc, "2.1.8", "en", "Table 1, object 7: %(class) - %(object)");
        addEntry(cc, "2.1.8", "de", "Tabelle 1, Objekt 7: %(class) - %(object)");
        addEntry(cc, "2.1.9", "en", "Table 1, object 7, Erni's Bert: %(bert)");
        addEntry(cc, "2.1.9", "de", "Tabelle 1, Objekt 7, Erni's Bert: %(bert)");
        addEntry(cc, "2.1.10", "en", "o17 is not an Erni, as expected");
        addEntry(cc, "2.1.10", "de", "o17 ist nicht, wie erwartet, ein Erni");
        addEntry(cc, "2.1.11", "en", "Table 1, object 8: %(class) - %(object)");
        addEntry(cc, "2.1.11", "de", "Tabelle 1, Objekt 8: %(class) - %(object)");
        addEntry(cc, "2.1.12", "en", "Table 1, object 8, Bert's Erni: %(erni)");
        addEntry(cc, "2.1.12", "de", "Tabelle 1, Objekt 8, Bert's Erni: %(erni)");
        addEntry(cc, "2.1.13", "en", "o18 is not a Bert, as expected");
        addEntry(cc, "2.1.13", "de", "o18 ist nicht, wie erwartet, ein Bert");
        addEntry(cc, "2.1.14", "en", "Table 2, object 1: %(class) - %(object)");
        addEntry(cc, "2.1.14", "de", "Tabelle 2, Objekt 1: %(class) - %(object)");
        addEntry(cc, "2.1.15", "en", "Table 2, object 2: %(class) - %(object)");
        addEntry(cc, "2.1.15", "de", "Tabelle 2, Objekt 2: %(class) - %(object)");
        addEntry(cc, "2.1.16", "en", "Table 2, object 3: %(class) - %(object)");
        addEntry(cc, "2.1.16", "de", "Tabelle 2, Objekt 3: %(class) - %(object)");
        addEntry(cc, "2.1.17", "en", "Table 2, object 4: %(class) - %(object)");
        addEntry(cc, "2.1.17", "de", "Tabelle 2, Objekt 4: %(class) - %(object)");
        addEntry(cc, "2.1.18", "en", "Table 2, object 5: %(class) - %(object)");
        addEntry(cc, "2.1.18", "de", "Tabelle 2, Objekt 5: %(class) - %(object)");
        addEntry(cc, "2.1.19", "en", "Table 2, object 6: %(class) - %(object)");
        addEntry(cc, "2.1.19", "de", "Tabelle 2, Objekt 6: %(class) - %(object)");
        addEntry(cc, "2.1.20", "en", "Table 2, object 7: %(class) - %(object)");
        addEntry(cc, "2.1.20", "de", "Tabelle 2, Objekt 7: %(class) - %(object)");
        addEntry(cc, "2.1.21", "en", "Table 2, object 7, Erni's Bert: %(bert)");
        addEntry(cc, "2.1.21", "de", "Tabelle 2, Objekt 7, Erni's Bert: %(bert)");
        addEntry(cc, "2.1.22", "en", "o27 is not an Erni, as expected");
        addEntry(cc, "2.1.22", "en", "o27 ist nicht, wie erwartet, ein Erni");
        addEntry(cc, "2.1.23", "en", "Table 2, object 8: %(class) - %(object)");
        addEntry(cc, "2.1.23", "de", "Tabelle 2, Objekt 8: %(class) - %(object)");
        addEntry(cc, "2.1.24", "en", "Table 2, object 8, Bert's Erni: %(erni)");
        addEntry(cc, "2.1.24", "de", "Tabelle 2, Objekt 8, Bert's Erni: %(erni)");
        addEntry(cc, "2.1.25", "en", "o28 is not a Bert, as expected");
        addEntry(cc, "2.1.25", "de", "o28 ist nicht, wie erwartet, ein Bert");
        addEntry(cc, "2.1.26", "en", "Objects are identical, contrary to expectation");
        addEntry(cc, "2.1.26", "en", "Objekte sind, entgegen der Erwartung, identisch");
        addEntry(cc, "2.1.27", "en", "Table 1, object 9: %(class) - %(object)");
        addEntry(cc, "2.1.27", "de", "Tabelle 1, Objekt 9, %(class) - %(object)");
        addEntry(cc, "2.1.28", "en", "Table 2, object 9: %(class) - %(object)");
        addEntry(cc, "2.1.28", "de", "Tabelle 2, Objekt 9, %(class) - %(object)");
        addEntry(cc, "2.1.29", "en", "Table 1, object A: %(class) - %(object)");
        addEntry(cc, "2.1.29", "de", "Tabelle 1, Objekt A, %(class) - %(object)");
        addEntry(cc, "2.1.30", "en", "Table 2, object A: %(class) - %(object)");
        addEntry(cc, "2.1.30", "de", "Tabelle 2, Objekt A, %(class) - %(object)");
        addEntry(cc, "2.1.31", "en", "Out parameter not retrievable: %(reason)");
        addEntry(cc, "2.1.31", "de", "Ausgabe-Parameter nicht verfügbar: %(reason)");
        addEntry(cc, "2.1.32", "en", "Table 1, object B (out parameter): %(class) - %(object)");
        addEntry(cc, "2.1.32", "de", "Tabelle 1, Objekt B (Ausgabe-Parameter), %(class) - %(object)");
        addEntry(cc, "2.1.33", "en", "Table 2, object B (out parameter): %(class) - %(object)");
        addEntry(cc, "2.1.33", "de", "Tabelle 2, Objekt B (Ausgabe-Parameter), %(class) - %(object)");
        //F-2-2-Walter.java
        addEntry(cc, "2.2.0", "en", "Walter is frugal. He gets by without parameters.");
        addEntry(cc, "2.2.0", "de", "Walter is genügsam. Er kommt ohne Parameter aus.");
        //F-2-3-Werner.java
        addEntry(cc, "2.3.0", "en", "Werner wants to know it. The truth is %(truth).");
        addEntry(cc, "2.3.0", "de", "Werner will's wissen. Die Wahrheit ist %(truth).");
        //F-2-4-Willy.java
        addEntry(cc, "2.4.0", "en", "Hey, a new Willy(context, %(s1), %(s2), %(s3)) is born! %(willy)");
        addEntry(cc, "2.4.0", "de", "Ei verbibschd, ein neuer Willy(context, %(s1), %(s2), %(s3)) erblickt das Licht der Welt! %(willy)");
        //F-2-5-Kunigunde.java
        addEntry(cc, "2.5.0", "en", "Hey, a new Kunigunde(context) is born!");
        addEntry(cc, "2.5.0", "de", "Ei verbibschd, eine neue Kunigunde(context) erblickt das Licht der Welt!");
        //F-2-6-Bert.java
        addEntry(cc, "2.6.0", "en", "Hey, this is Bert!");
        addEntry(cc, "2.6.0", "de", "Ei verbibschd, hier is der Bert!");
        //END-OF-STRINGS
        /*************************************************/
    }
}
