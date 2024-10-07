package com.sphenon.engines.factorysite.diagram;

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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.accessory.classes.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.xml.*;
import com.sphenon.basics.xml.returncodes.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.tplinst.*;

import static com.sphenon.basics.system.StringUtilities.isEmpty;
import static com.sphenon.basics.system.StringUtilities.isNotEmpty;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class DIADiagram extends DIAItem implements MonitorableObject, ProblemMonitor {

    protected DIADiagram (CallContext context, XMLNode node) {
        this(context, node, "ocp");
    }

    protected DIADiagram (CallContext context, XMLNode node, String layers) {
        this(context, node, layers.split(","));
    }

    protected DIADiagram (CallContext context, XMLNode node, String[] layers) {
        super(context, node, layers, ItemType.Diagram, null, null);

        this.diagram = this;

        if (this.node != null) {
            this.layer_node = this.node.getChildElementsByFilters(context, diagram_filter, this.getLayerFilter(context));

            this.source_info = "xml-node"; // need source info in XML Node
        }
    }

    protected String source_info;

    public String getSourceInfo(CallContext context) {
        return this.source_info;
    }

    static public BuildText build(CallContext context, XMLNode node) {
        DIADiagram dou = new DIADiagram(context, node);

        BuildText bt = dou.getBuildText(context);

        if (dou.getProblemState(context).isGreen(context) == false) {
            ProblemStatusGroup psg = new ProblemStatusGroup(context, dou.getProblemStatusDetails(context));
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Diagram is invalid: %(details)", "details", psg);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        com.sphenon.basics.debug.Dumper.dump(context, "DIA Build Text", bt);
        
        return bt;
    }

    protected BuildText getBuildText(CallContext context) {
        DIAConfiguration mc = this.getMainConfiguration(context);
        DIAConfiguration dc = DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), mc);

        BuildText result = null;
        if (this.getProperty(context, dc, null, ItemRole.Root, false)) {
            result = this.createBuildText(context, dc, null);
        } else {
            DIAItem root = this.getRootItem(context, dc);
            result = root == null ? null : root.createBuildText(context, dc, null);
        }

        return result;
    }

    protected DIAConfiguration main_configuration;

    protected DIAConfiguration getMainConfiguration(CallContext context) {
        if (this.main_configuration == null) {
            String ns = this.getNameSpace(context);
            this.main_configuration = DIAConfiguration.retrieveConfiguration(context, ns, getSourceInfo(context));

            if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendSelfDiagnostics(context, "Main configuration for '%(iteminfo)' is:", "iteminfo", this.getSourceInfo(context)); Dumper.dump(context, "Main configuration", this.main_configuration); }
        }
        return this.main_configuration;
    }

    // overload with something better, like note with "Name/Package: ..." oder so
    // protected String retrieveName(CallContext context) {
    //     return this.getAttributeString(context, "name", this.node);
    // }
    protected BuildText createBuildText(CallContext context, DIAConfiguration pc, String name) {
        DIAConfiguration dc = DIAConfiguration.cloneLocalConfiguration(context, this.getLocalConfiguration(context), pc);

        BuildTextComplex_String result = new BuildTextComplex_String(context);

        this.applyOCPProperties(context, dc, result, name);

        String diagram_name = this.getName(context);
        if (diagram_name != null && diagram_name.isEmpty() == false) {
            String diagram_name_attribute = this.getProperty(context, dc, null, OCPProperty.AttributeName, ItemProperty.DiagramName, (String) null);
            BuildTextSimple_String bts = new BuildTextSimple_String(context);

            bts.setText(context, diagram_name);
            bts.setTypeName(context, "java.lang.String");

            result.addItem(context, diagram_name_attribute, bts);
        }

        String class_attribute   = this.getProperty(context, dc, null, OCPProperty.AttributeName, ItemProperty.Class, (String) null);
        if (isNotEmpty(class_attribute)) {
            for (DIAClass dia_class : this.getClasses(context)) {
                BuildText class_bt = dia_class.createBuildText(context, dc, null);
                result.addItem(context, class_attribute, class_bt);
            }
        }

        return result;
    }

    protected DIAItem getRootItem(CallContext context, DIAConfiguration dc) {
        Vector<DIAClass> classes = this.getClasses(context);
        if (classes == null || classes.size() == 0) {
            this.diagram.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' does not contain a root item of type '%(itemtype)'", "dia", this.diagram.getSourceInfo(context), "itemtype", "Class"));
            return null;
        }
        DIAItem root = null;
        if (classes.size() > 1) {
            for (DIAClass dia_class: classes) {
                if (dia_class.getProperty(context, null, dc, ItemRole.Root, false)) {
                    if (root != null) {
                        this.diagram.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains more than one item of type '%(itemtype)' marked as '@Root'", "dia", this.diagram.getSourceInfo(context), "itemtype", "Class"));
                        return null;
                    }
                    root = dia_class;
                }
            }
            if (root == null) {
                this.diagram.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains more than one item of type '%(itemtype)', but none is marked as '@Root'", "dia", this.diagram.getSourceInfo(context), "itemtype", "Class"));
                return null;
            }
        }
        return classes.get(0);
    }

    // default ordering of graphical elements:
    // primary_direction = horizontal|vertical
    // horizontal_ordering = left_to_right|right_to_left
    // vertical_ordering = top_to_bottom|bottom_to_top
    // tolerance = # (distance, which is ignored for detection of difference)

/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    static protected String standard_stereotype = "<entry0>org.uml.stereotypes.*</entry0>";

    public DIADiagram (CallContext context, String uncompressed_dia_file, String layer) {
        this.getSmallPackages(context);
        this.getViews(context);
        this.getStates(context);
        this.getExtensionInstances(context);
        this.getImports(context);

        for (DIADiagram generalization_utility : this.getGeneralizations(context)) {
            String base    = generalization_utility.getBase(context);
            String derived = generalization_utility.getDerived(context);
//             System.err.println("BASE   : " + base);
//             System.err.println("DERIVED: " + derived);

            DIADiagram base_utility    = classes_by_id.get(base);
            DIADiagram derived_utility = classes_by_id.get(derived);

            derived_utility.addBase(context, base_utility);
        }


        for (DIADiagram transition_utility : this.getTransitions(context)) {
//             System.err.println("TU: " + transition_utility);
            String source = transition_utility.getSource(context);
            String target = transition_utility.getTarget(context);

//             System.err.println("TU-S: " + source);
//             System.err.println("TU-T: " + target);

            DIADiagram source_utility = states_by_id.get(source);
            DIADiagram target_utility = states_by_id.get(target);

//             System.err.println("TU-SU: " + source_utility);
//             System.err.println("TU-TU: " + target_utility);

            source_utility.addOutboundTransition(context, transition_utility);
            target_utility.addInboundTransition(context, transition_utility);

            transition_utility.setSourceState(context, source_utility);
            transition_utility.setTargetState(context, target_utility);
        }

        // java.util.Collections.sort(this.associations, new UtilityComparator(context));

        Vector<Object[]> sms_to_process = new Vector<Object[]>();
        Vector<Object[]> pes_to_process = new Vector<Object[]>();

        for (DIADiagram constraint_utility : this.getConstraints(context)) {
            String source = constraint_utility.getSource(context);
            String target = constraint_utility.getTarget(context);
            String constraint = constraint_utility.getConstraint(context);

            DIADiagram extension_instance_utility = extension_instances_by_id.get(source);
            DIADiagram view_of_class_utility = views_by_id.get(source);
            DIADiagram imports_of_class_utility = imports_by_id.get(source);
            DIADiagram pattern_of_class_utility = patterns_by_id.get(source);
            DIADiagram class_utility = classes_by_id.get(target);
            DIADiagram pattern_utility = patterns_by_id.get(target);
            DIADiagram small_package_utility = small_packages_by_id.get(target);
            DIADiagram extended_view_utility = views_by_id.get(target);
            DIADiagram state_utility = states_by_id.get(source);

            if (extension_instance_utility == null && view_of_class_utility == null && imports_of_class_utility == null && pattern_of_class_utility == null && state_utility == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "Source '%(source)' of constraint '%(constraint) in DIA file does not exist (arrow not connected)", "constraint", constraint, "source", source));
                continue;
            }

            if (class_utility == null && pattern_utility == null && small_package_utility == null && extended_view_utility == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "Target '%(target)' of constraint '%(constraint) in DIA file does not exist (arrow not connected)", "constraint", constraint, "target", target));
                continue;
            }

            if (    class_utility != null
                 && pattern_utility == null
                 && small_package_utility == null
                 && extension_instance_utility != null
                 && view_of_class_utility == null
                 && pattern_of_class_utility == null
                 && imports_of_class_utility == null
                 && extended_view_utility == null
                 && state_utility == null
               ) {
                if (constraint == null || constraint.length() == 0) {
                    class_utility.addExtension(context, extension_instance_utility);
                } else {
                    class_utility.addFeatureExtension(context, extension_instance_utility, constraint);
                }
            } else if (    class_utility == null
                        && pattern_utility != null
                        && small_package_utility == null
                        && extension_instance_utility != null
                        && view_of_class_utility == null
                        && pattern_of_class_utility == null
                        && imports_of_class_utility == null
                        && extended_view_utility == null
                        && state_utility == null
               ) {
                if (constraint == null || constraint.length() == 0) {
                    pattern_utility.addExtension(context, extension_instance_utility);
                } else {
                    pattern_utility.addFeatureExtension(context, extension_instance_utility, constraint);
                }
            } else if (    class_utility == null
                        && pattern_utility == null
                        && small_package_utility != null
                        && extension_instance_utility != null
                        && view_of_class_utility == null
                        && pattern_of_class_utility == null
                        && imports_of_class_utility == null
                        && extended_view_utility == null
                        && state_utility == null
                      ) {
                // WM changed 15.07.2011
                //small_package_utility.addExtension(context, extension_instance_utility);
                this.addExtension(context, extension_instance_utility);
            } else if (    class_utility != null
                        && pattern_utility == null
                        && small_package_utility == null
                        && extension_instance_utility == null
                        && view_of_class_utility != null
                        && pattern_of_class_utility == null
                        && imports_of_class_utility == null
                        && extended_view_utility == null
                        && state_utility == null
                      ) {
                class_utility.addViewOfClass(context, view_of_class_utility);
                view_of_class_utility.setClassOfView(context, class_utility);
            } else if (    class_utility != null
                        && pattern_utility == null
                        && small_package_utility == null
                        && extension_instance_utility == null
                        && view_of_class_utility == null
                        && pattern_of_class_utility != null
                        && imports_of_class_utility == null
                        && extended_view_utility == null
                        && state_utility == null
                      ) {
                if (constraint == null || constraint.length() == 0) {
                    this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "Pattern dependency between target '%(target)' and source '%(source)' of constraint '%(constraint) in DIA file must not be empty", "constraint", constraint, "target", target, "source", source));
                } else {
                    Object[] pe_to_process = new Object[3];
                    pe_to_process[0] = pattern_of_class_utility;
                    pe_to_process[1] = class_utility;
                    pe_to_process[2] = constraint;
                    pes_to_process.add(pe_to_process);
                }
            } else if (    class_utility != null
                        && pattern_utility == null
                        && small_package_utility == null
                        && extension_instance_utility == null
                        && view_of_class_utility == null
                        && pattern_of_class_utility == null
                        && imports_of_class_utility != null
                        && extended_view_utility == null
                        && state_utility == null
                      ) {
                class_utility.addImportsOfClass(context, imports_of_class_utility);
                imports_of_class_utility.setClassOfImports(context, class_utility);
            } else if (    class_utility == null
                        && pattern_utility == null
                        && small_package_utility != null
                        && extension_instance_utility == null
                        && view_of_class_utility == null
                        && pattern_of_class_utility == null
                        && imports_of_class_utility != null
                        && extended_view_utility == null
                        && state_utility == null
                      ) {
                small_package_utility.addImportsOfClass(context, imports_of_class_utility);
                imports_of_class_utility.setClassOfImports(context, small_package_utility);
            } else if (    class_utility == null
                        && pattern_utility == null
                        && small_package_utility == null
                        && extension_instance_utility != null
                        && view_of_class_utility == null
                        && pattern_of_class_utility == null
                        && imports_of_class_utility == null
                        && extended_view_utility != null
                        && state_utility == null
                      ) {
                if (constraint == null || constraint.length() == 0) {
                    extended_view_utility.addExtension(context, extension_instance_utility);
                } else {
                    extended_view_utility.addFeatureExtension(context, extension_instance_utility, constraint);
                }
            } else if (    class_utility != null
                        && pattern_utility == null
                        && small_package_utility == null
                        && extension_instance_utility == null
                        && view_of_class_utility == null
                        && pattern_of_class_utility == null
                        && imports_of_class_utility == null
                        && extended_view_utility == null
                        && state_utility != null
                      ) {
                String state_variable = (constraint == null || constraint.length() == 0 ? "main" : constraint);
                Object[] sm_to_process = new Object[3];
                sm_to_process[0] = class_utility;
                sm_to_process[1] = state_utility;
                sm_to_process[2] = state_variable;
                sms_to_process.add(sm_to_process);
            } else {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "Types of target '%(target)' and source '%(source)' of constraint '%(constraint)' in DIA file do not match (a View must be attached to a Class, a State must be attached to a class, or alternatively an Extension must be attached to either a Class or a View)", "constraint", constraint, "target", target, "source", source));
                continue;
            }

        }

        for (Object[] pe : pes_to_process) {
            ((DIADiagram) pe[0]).addEnd(context, ((DIADiagram) pe[1]), ((String) pe[2]));
        }

        for (DIADiagram pattern_utility : this.getPatterns(context)) {
            Vector<DIADiagram> ends = pattern_utility.getEnds(context);
            for (DIADiagram end : ends) {
                DIADiagram element = end.getConnection(context);                
                if (element != null) {
                    element.addPatternEnd(context, end);
                } else {
                    this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "Pattern '%(pattern)' end '%(end)' in DIA file does not exist or is not attached", "pattern", pattern_utility.getName(context), "end", end.getRole(context)));
                }
            }
        }

        for (Object[] sm : sms_to_process) {
            ((DIADiagram) sm[0]).addReachableStates(context, ((DIADiagram) sm[1]), null, null, ((String) sm[2]));
        }

        if (udfs.length > 1) {
            this.additional_packages = new Vector<DIADiagram>();
            for (int i=1; i<udfs.length; i++) {
                DIADiagram additional_package = new DIADiagram(context, udfs[i], layer);
                this.additional_packages.add(additional_package);
                for (DIADiagram a_class : additional_package.getClasses(context)) {
                    this.getClasses(context).add(a_class);
                }
                for (DIADiagram a_association : additional_package.getAssociations(context)) {
                    this.getAssociations(context).add(a_association);
                }
                for (DIADiagram a_pattern : additional_package.getPatterns(context)) {
                    this.getPatterns(context).add(a_pattern);
                }
            }
        }
    }

    public DIADiagram (CallContext context, XMLNode node, DIADiagram root, NodeType node_type) {
        this.node = node;
        this.root = root;
        this.node_type = node_type;
    }

    public DIADiagram (CallContext context, XMLNode node, DIADiagram root, NodeType node_type, DIADiagram parent) {
        this.node = node;
        this.root = root;
        this.parent = parent;
        this.node_type = node_type;
    }

    protected Vector<DIADiagram> small_packages;
    protected HashMap<String,DIADiagram> small_packages_by_id;
    public Vector<DIADiagram> getSmallPackages(CallContext context) {
        if (this.small_packages == null) {
            this.small_packages = new Vector<DIADiagram>();
            this.small_packages_by_id = new HashMap<String,DIADiagram>();

// DELETEME
//             if (this.node != null) {
//                 for (String layer : layers) {
//                     for (XMLNode class_node : this.node.resolveXPath(context, "/dia:diagram/dia:layer[@name='" + layer + "']/dia:object[@type='UML - Class']", namespaces).getNodes(context)) {

            if (this.layer_node != null) {
                for (XMLNode small_package_node : this.layer_node.getChildElementsByFilters(context, small_package_filter).getNodes(context)) {
                    DIADiagram small_package_utility = new DIADiagram(context, small_package_node, this.root, NodeType.SmallPackage);
                    this.small_packages.add(small_package_utility);
                    this.small_packages_by_id.put(small_package_utility.getId(context), small_package_utility);
//                     System.err.println("SMALL PACKAGE: " + small_package_utility.getId(context));
                }
            }
        }
        return this.small_packages;
    }

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    protected Vector<DIAClass> classes;
    protected HashMap<String,DIAClass> classes_by_id;

    public Vector<DIAClass> getClasses(CallContext context) {
        this.prepare(context);
        return this.classes;
    }

    public DIAClass getClassById(CallContext context, String id) {
        return this.classes_by_id == null ? null : this.classes_by_id.get(id);
    }

    protected Vector<DIAAssociation> associations;

    public Vector<DIAAssociation> getAssociations(CallContext context) {
        this.prepare(context);
        return this.associations;
    }

    volatile protected boolean prepared;

    protected void prepare(CallContext context) {
        if ( ! this.prepared) {
            synchronized (this) {
                if ( ! this.prepared) {
                    // --------------------------------------------------------------------------------------------
                    this.classes = new Vector<DIAClass>();
                    this.classes_by_id = new HashMap<String,DIAClass>();
                        
                    if (this.layer_node != null) {
                        for (XMLNode class_node : this.layer_node.getChildElementsByFilters(context, class_filter).getNodes(context)) {
                            DIAClass dia_class = new DIAClass(context, class_node, this.diagram, this);

                            // String[] stereotypes = dia_class.getStereotypes(context);
                            // if (    stereotypes != null
                            //      && stereotypes.length == 1
                            //      && stereotypes[0].matches("Pattern:.*")
                            //    ) {
                            //     class_utility.node_type = NodeType.Pattern;
                            //     this.patterns.add(class_utility);
                            //     this.patterns_by_id.put(class_utility.getId(context), class_utility);
                            // } else {
                            this.classes.add(dia_class);
                            this.classes_by_id.put(dia_class.getId(context), dia_class);
                            // }
                        }
                    }

                    // --------------------------------------------------------------------------------------------
                    this.associations = new Vector<DIAAssociation>();

                    if (this.layer_node != null) {
                        for (XMLNode association_node : this.layer_node.getChildElementsByFilters(context, association_filter).getNodes(context)) {
                            DIAAssociation dia_association = new DIAAssociation(context, association_node, this.diagram, this);
                            this.associations.add(dia_association);
                            // association_utility.setAutoIndex(context, "2." + this.associations.size());
                        }
                    }

                    // --------------------------------------------------------------------------------------------
                    for (DIAAssociation association : this.associations) {
                        Vector<DIAAssociationEnd> ends = association.getEnds(context);
                        if (ends.size() == 2) {
                            DIAAssociationEnd end1 = ends.get(0);
                            DIAAssociationEnd end2 = ends.get(1);

                            DIAClass class1 = end1.getConnection(context);                
                            class1.addAssociationEnd(context, end1);

                            DIAClass class2 = end2.getConnection(context);                
                            class2.addAssociationEnd(context, end2);
                        }
                    }

                    // --------------------------------------------------------------------------------------------

                    this.prepared = true;
                }
            }
        }
    }

    protected Vector<DIANote> notes;
    protected HashMap<String,DIANote> notes_by_id;

    public Vector<DIANote> getNotes(CallContext context) {
        if (this.notes == null) {
            this.notes = new Vector<DIANote>();
            this.notes_by_id = new HashMap<String,DIANote>();

            if (this.layer_node != null) {
                for (XMLNode note_node : this.layer_node.getChildElementsByFilters(context, note_filter).getNodes(context)) {
                    DIANote dia_note = new DIANote(context, note_node, this.diagram, this);

                    // String[] stereotypes = dia_note.getStereotypes(context);
                    // if (    stereotypes != null
                    //      && stereotypes.length == 1
                    //      && stereotypes[0].matches("Pattern:.*")
                    //    ) {
                    //     note_utility.node_type = NodeType.Pattern;
                    //     this.patterns.add(note_utility);
                    //     this.patterns_by_id.put(note_utility.getId(context), note_utility);
                    // } else {
                    this.notes.add(dia_note);
                    this.notes_by_id.put(dia_note.getId(context), dia_note);
                    // }
                }
            }
        }
        return this.notes;
    }

    public String getNameSpace(CallContext context) {
        Vector<DIANote> notes = this.getNotes(context);
        String ns = null;
        if (notes != null && notes.size() != 0) {
            for (DIANote note : notes) {
                String nsp = note.getProperty(context, null, null, OCPProperty.NameSpace, (String) null);
                if (isNotEmpty(nsp)) {
                    if (ns != null) {
                        this.diagram.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains more than one note with a namespace declaration (i.e. a string starting with '@NameSpace:'), first declaration is taken, others ignored", "dia", this.diagram.getSourceInfo(context)));
                    } else {
                        ns = nsp;
                    }
                }
            }
        }
        return ns == null ? "org/grocp/Default/1.0" : ns;
    }

/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    protected Vector<DIADiagram> states;
    protected HashMap<String,DIADiagram> states_by_id;
    public Vector<DIADiagram> getStates(CallContext context) {
        if (this.states == null) {
            this.states = new Vector<DIADiagram>();
            this.states_by_id = new HashMap<String,DIADiagram>();

// DELETEME
//             if (this.node != null) {
//                 for (String layer : layers) {
//                     for (XMLNode state_node : this.node.resolveXPath(context, "/dia:diagram/dia:layer[@name='" + layer + "']/dia:object[@type='UML - State']", namespaces).getNodes(context)) {

            if (this.layer_node != null) {
                for (XMLNode state_node : this.layer_node.getChildElementsByFilters(context, state_filter).getNodes(context)) {
                    DIADiagram state_utility = new DIADiagram(context, state_node, this.root, NodeType.State);
                    if (state_node.getAttribute(context, "type").equals("UML - State Term")) {
                        state_utility.setIsTerminalState(context, true);
                    }
                    if (state_node.getAttribute(context, "type").equals("UML - Branch")) {
                        state_utility.setIsPseudoState(context, true);
                    }
                    this.states.add(state_utility);
                    this.states_by_id.put(state_utility.getId(context), state_utility);
//                     System.err.println("STATE: " + state_utility.getId(context));
                }
            }
        }
        return this.states;
    }

    protected Vector<DIADiagram> patterns;
    protected HashMap<String,DIADiagram> patterns_by_id;
    public Vector<DIADiagram> getPatterns(CallContext context) {
        if (this.patterns == null) {
            this.getClasses(context);
        }
        return this.patterns;
    }

    protected Vector<DIADiagram> generalizations;
    public Vector<DIADiagram> getGeneralizations(CallContext context) {
        if (this.generalizations == null) {
            this.generalizations = new Vector<DIADiagram>();

// DELETEME
//             if (this.node != null) {
//                 for (String layer : layers) {
//                     for (XMLNode generalization_node : this.node.resolveXPath(context, "/dia:diagram/dia:layer[@name='" + layer + "']/dia:object[@type='UML - Generalization']", namespaces).getNodes(context)) {

            if (this.layer_node != null) {
                for (XMLNode generalization_node : this.layer_node.getChildElementsByFilters(context, generalization_filter).getNodes(context)) {
                    DIADiagram generalization_utility = new DIADiagram(context, generalization_node, this.root, NodeType.Generalization);
                    this.generalizations.add(generalization_utility);
                }
            }
        }
        return this.generalizations;
    }

    protected Vector<DIADiagram> transitions;
    public Vector<DIADiagram> getTransitions(CallContext context) {
        if (this.transitions == null) {
            this.transitions = new Vector<DIADiagram>();

            if (this.layer_node != null) {
                for (XMLNode transition_node : this.layer_node.getChildElementsByFilters(context, transition_filter).getNodes(context)) {
                    DIADiagram transition_utility = new DIADiagram(context, transition_node, this.root, NodeType.Transition);
                    this.transitions.add(transition_utility);
                }
            }
        }
        return this.transitions;
    }


    protected Vector<DIADiagram> bases;
    protected void addBase(CallContext context, DIADiagram base_utility) {
        if (this.bases == null) {
            this.bases = new Vector<DIADiagram>();
        }
        this.bases.add(base_utility);
    }

    protected void setBases(CallContext context, Vector<DIADiagram> bases) {
        this.bases = bases;
    }

    public Vector<DIADiagram> getBases(CallContext context) {
        return this.bases;
    }

    protected Vector<DIADiagram> pattern_ends;
    protected void addPatternEnd(CallContext context, DIADiagram pattern_end_utility) {
        if (this.pattern_ends == null) {
            this.pattern_ends = new Vector<DIADiagram>();
        }
        this.pattern_ends.add(pattern_end_utility);
    }

    protected void setPatternEnds(CallContext context, Vector<DIADiagram> pattern_ends) {
        this.pattern_ends = pattern_ends;
    }

    public Vector<DIADiagram> getPatternEnds(CallContext context) {
        return this.pattern_ends;
    }

    protected Vector<DIADiagram> outbound_transitions;
    protected void addOutboundTransition(CallContext context, DIADiagram outbound_transition_utility) {
        if (this.outbound_transitions == null) {
            this.outbound_transitions = new Vector<DIADiagram>();
        }
        this.outbound_transitions.add(outbound_transition_utility);
    }

    protected void setOutboundTransitions(CallContext context, Vector<DIADiagram> outbound_transitions) {
        this.outbound_transitions = outbound_transitions;
    }

    public Vector<DIADiagram> getOutboundTransitions(CallContext context) {
        return this.outbound_transitions;
    }

    protected Vector<DIADiagram> inbound_transitions;
    protected void addInboundTransition(CallContext context, DIADiagram inbound_transition_utility) {
        if (this.inbound_transitions == null) {
            this.inbound_transitions = new Vector<DIADiagram>();
        }
        this.inbound_transitions.add(inbound_transition_utility);
    }

    protected void setInboundTransitions(CallContext context, Vector<DIADiagram> inbound_transitions) {
        this.inbound_transitions = inbound_transitions;
    }

    public Vector<DIADiagram> getInboundTransitions(CallContext context) {
        return this.inbound_transitions;
    }

    protected DIADiagram source_state;
    protected void setSourceState(CallContext context, DIADiagram source_state) {
        this.source_state = source_state;
    }

    public DIADiagram getSourceState(CallContext context) {
        return this.source_state;
    }

    protected DIADiagram target_state;
    protected void setTargetState(CallContext context, DIADiagram target_state) {
        this.target_state = target_state;
    }

    public DIADiagram getTargetState(CallContext context) {
        return this.target_state;
    }

    protected Vector<String[]> target_states_of_transition;
    public Vector<String[]> getTargetStatesOfTransition(CallContext context) {
        if (this.target_states_of_transition == null) {
            this.target_states_of_transition = new Vector<String[]>();
            if (this.getAttachedTransitionsOfClass(context) != null) {
                for (DIADiagram atoc : this.getAttachedTransitionsOfClass(context)) {
                    processTargetStateOfTransition(context, atoc.getTargetState(context), this);
                }
            } else {
                processTargetStateOfTransition(context, this.getTargetState(context), this);
            }
        }
        return this.target_states_of_transition;
    }

    public void processTargetStateOfTransition(CallContext context, DIADiagram tocts, DIADiagram add_to) {
        if (tocts.getIsPseudoState(context) == false) {
            String[] ts = new String[4];
            ts[0] = null;
            ts[1] = tocts.getName(context);
            ts[2] = tocts.getStateVariable(context);
            ts[3] = "true";
            add_to.target_states_of_transition.add(ts);
//             System.err.println("PSEUDO: false");
//             System.err.println("ADDING: " + ts[0] + " - " + ts[1] + " - " + ts[2] + " - " + ts[3]);
//             System.err.println("TO    : " + add_to);
        } else {
            if (tocts.getOutboundTransitions(context) != null) {
                DIADiagram ot_else = null;
                boolean first_ot = true;
                for (DIADiagram ot : tocts.getOutboundTransitions(context)) {
                    String guard = ot.getGuard(context);
                    if ("else".equals(guard)) {
                        ot_else = ot;
                    } else {
                        for (String[] otts : ot.getTargetStatesOfTransition(context)) {
                            String[] ts = new String[4];
                            ts[0] = (otts[0] == null ? guard : ("(" + guard + ") && (" + otts[0] + ")"));
                            ts[1] = otts[1];
                            ts[2] = otts[2];
                            ts[3] = (new Boolean(first_ot)).toString();
                            add_to.target_states_of_transition.add(ts);
//                             System.err.println("PSEUDO: true, IF");
//                             System.err.println("ADDING: " + ts[0] + " - " + ts[1] + " - " + ts[2] + " - " + ts[3]);
//                             System.err.println("TO    : " + add_to);
                        }
                        first_ot = false;
                    }
                }
                if (ot_else != null) {
                    for (String[] otts : ot_else.getTargetStatesOfTransition(context)) {
                        String[] ts = new String[4];
                        ts[0] = (otts[0] == null ? "true" : otts[0]);
                        ts[1] = otts[1];
                        ts[2] = otts[2];
                        ts[3] = (new Boolean(first_ot)).toString();
                        add_to.target_states_of_transition.add(ts);
//                         System.err.println("PSEUDO: true, ELSE");
//                         System.err.println("ADDING: " + ts[0] + " - " + ts[1] + " - " + ts[2] + " - " + ts[3]);
//                         System.err.println("TO    : " + add_to);
                    }
                }
            }
        }
    }

    protected Vector<String[]> source_states_of_transition;
    public Vector<String[]> getSourceStatesOfTransition(CallContext context) {
        if (this.source_states_of_transition == null) {
            this.source_states_of_transition = new Vector<String[]>();
        }
        if (this.getSourceState(context).getIsPseudoState(context) == false) {
            String[] ss = new String[3];
            ss[0] = this.getSourceState(context).getName(context);
            ss[1] = this.getSourceState(context).getStateVariable(context);

            String condition = (ss[0].equals("*") ? null : (ss[1] + "==" + ss[0]));
            String guard = this.getGuard(context);
            if (guard == null || guard.isEmpty()) { guard = null; }
            ss[2] = (     guard == null
                        ? condition
                        : (   condition == null
                            ? guard
                            : ("((" + guard + ")&&(" + condition + "))")
                          )
                    );
            this.source_states_of_transition.add(ss);
        } else {
            if (this.getSourceState(context).getInboundTransitions(context) != null) {
                for (DIADiagram it : this.getSourceState(context).getInboundTransitions(context)) {
                    for (String[] itss : it.getSourceStatesOfTransition(context)) {
                        this.source_states_of_transition.add(itss);
                    }
                }
            }
        }
        return this.source_states_of_transition;
    }

    public String getBeforeStateCondition(CallContext context) {
        String condition = "";
        boolean first_and = true;
//         System.err.println("GETBEF");
        for (DIADiagram atoc : this.getAttachedTransitionsOfClass(context)) {
//             System.err.println("ATOC: " + atoc.getName(context));
            boolean first_or = true;
            for (String[] ss : atoc.getSourceStatesOfTransition(context)) {
                if (ss[2] == null) {
                    continue;
                }
                if (first_or) {
                    if (first_and) {
                        condition += "(";
                    } else {
                        condition += ")&&(";
                    }
                } else {
                    condition += "||";
                }
                condition += ss[2];
                first_or = false;
                first_and = false;
            }
        }
        if (first_and == false) {
            condition += ")";
        }
//         System.err.println("BEFORE CONDITION: " + condition);
        return condition;
    }

    protected Vector<DIADiagram> extension_instances;
    protected HashMap<String,DIADiagram> extension_instances_by_id;
    public Vector<DIADiagram> getExtensionInstances(CallContext context) {
        if (this.extension_instances == null) {
            this.extension_instances = new Vector<DIADiagram>();
            this.extension_instances_by_id = new HashMap<String,DIADiagram>();

            if (this.layer_node != null) {
                for (XMLNode extension_instance_node : this.layer_node.getChildElementsByFilters(context, extension_filter).getNodes(context)) {
                    DIADiagram extension_instance_utility = new DIADiagram(context, extension_instance_node, this.root, NodeType.Extension);
                    this.extension_instances.add(extension_instance_utility);
                    this.extension_instances_by_id.put(extension_instance_utility.getId(context), extension_instance_utility);
                }
            }
        }
        return this.extension_instances;
    }

    protected Vector<DIADiagram> extensions;
    protected void addExtension(CallContext context, DIADiagram extension_utility) {
        if (this.extensions == null) {
            this.extensions = new Vector<DIADiagram>();
        }
        this.extensions.add(extension_utility);
    }

    protected void setExtensions(CallContext context, Vector<DIADiagram> extensions) {
        this.extensions = extensions;
    }

    public Vector<DIADiagram> getExtensions(CallContext context) {
        return this.extensions;
    }

    protected HashMap<String,Vector<DIADiagram>> feature_extensions_map;
    protected void addFeatureExtension(CallContext context, DIADiagram extension_utility, String feature_name) {
//         System.err.println("ADDING " + extension_utility.getName(context) + " TO FEATURE " + feature_name + " AT " + this.getName(context));
        if (feature_name.charAt(0) == '>') {
            feature_name = feature_name.substring(1);
            Vector<DIADiagram> aes = getAssociationEnds(context);
            if (aes != null) {
                for (DIADiagram ae : aes) {
                    if (ae.getOtherEndsName(context).equals(feature_name)) {
                        ae.addExtension(context, extension_utility);
                        return;
                    }
                }
            }
            this.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file contains invalid extension (no such association found: '%(name)')", "name", feature_name));
        } else {
            if (this.feature_extensions_map == null) {
                this.feature_extensions_map = new HashMap<String,Vector<DIADiagram>>();
            }
            Vector<DIADiagram> feature_extensions = this.feature_extensions_map.get(feature_name);
            if (feature_extensions == null) {
                feature_extensions = new Vector<DIADiagram>();
                this.feature_extensions_map.put(feature_name, feature_extensions);
            }
            feature_extensions.add(extension_utility);
        }
    }

    protected Vector<DIADiagram> views;
    protected HashMap<String,DIADiagram> views_by_id;
    public Vector<DIADiagram> getViews(CallContext context) {
        if (this.views == null) {
            this.views = new Vector<DIADiagram>();
            this.views_by_id = new HashMap<String,DIADiagram>();

// DELETEME
//             if (this.node != null) {
//                 for (String layer : layers) {
//                     for (XMLNode view_node : this.node.resolveXPath(context, "/dia:diagram/dia:layer[@name='" + layer + "']/dia:object[@type='ObjectView']", namespaces).getNodes(context)) {

            if (this.layer_node != null) {
                for (XMLNode view_node : this.layer_node.getChildElementsByFilters(context, view_filter).getNodes(context)) {
                    DIADiagram view_utility = new DIADiagram(context, view_node, this.root, NodeType.View);
                    this.views.add(view_utility);
                    this.views_by_id.put(view_utility.getId(context), view_utility);
                }
            }
        }
        return this.views;
    }

    protected Vector<DIADiagram> views_of_class;
    protected void addViewOfClass(CallContext context, DIADiagram view_of_class_utility) {
        if (this.views_of_class == null) {
            this.views_of_class = new Vector<DIADiagram>();
        }
        this.views_of_class.add(view_of_class_utility);
    }

    protected void setViewsOfClass(CallContext context, Vector<DIADiagram> views_of_class) {
        this.views_of_class = views_of_class;
    }

    public Vector<DIADiagram> getViewsOfClass(CallContext context) {
        return this.views_of_class;
    }

    protected DIADiagram class_of_view;
    protected void setClassOfView(CallContext context, DIADiagram class_of_view_utility) {
        this.class_of_view = class_of_view_utility;
    }

    public DIADiagram getClassOfView(CallContext context) {
        return this.class_of_view;
    }

    protected Vector<DIADiagram> imports;
    protected HashMap<String,DIADiagram> imports_by_id;
    public Vector<DIADiagram> getImports(CallContext context) {
        if (this.imports == null) {
            this.imports = new Vector<DIADiagram>();
            this.imports_by_id = new HashMap<String,DIADiagram>();

// DELETEME
//             if (this.node != null) {
//                 for (String layer : layers) {
//                     for (XMLNode imports_node : this.node.resolveXPath(context, "/dia:diagram/dia:layer[@name='" + layer + "']/dia:object[@type='Imports']", namespaces).getNodes(context)) {

            if (this.layer_node != null) {
                for (XMLNode imports_node : this.layer_node.getChildElementsByFilters(context, import_filter).getNodes(context)) {
                    DIADiagram imports_utility = new DIADiagram(context, imports_node, this.root, NodeType.Imports);
                    this.imports.add(imports_utility);
                    this.imports_by_id.put(imports_utility.getId(context), imports_utility);
                }
            }
        }
        return this.imports;
    }

    protected Vector<DIADiagram> package_imports;
    public Vector<DIADiagram> getPackageImports(CallContext context) {
        if (this.package_imports == null) {
            this.package_imports = new Vector<DIADiagram>();
            for (DIADiagram imports : this.getImports(context)) {
                if (    imports.getClassOfImports(context) == null
                     || imports.getClassOfImports(context).getNodeType(context) == NodeType.SmallPackage
                   ) {
                    this.package_imports.add(imports);
                }
            }
        }
        return this.package_imports;
    }

    protected Vector<DIADiagram> imports_of_class;
    protected void addImportsOfClass(CallContext context, DIADiagram imports_of_class_utility) {
        if (this.imports_of_class == null) {
            this.imports_of_class = new Vector<DIADiagram>();
        }
        this.imports_of_class.add(imports_of_class_utility);
    }

    protected void setImportsOfClass(CallContext context, Vector<DIADiagram> imports_of_class) {
        this.imports_of_class = imports_of_class;
    }

    public Vector<DIADiagram> getImportsOfClass(CallContext context) {
        return this.imports_of_class;
    }

    protected DIADiagram class_of_imports;
    protected void setClassOfImports(CallContext context, DIADiagram class_of_imports_utility) {
        this.class_of_imports = class_of_imports_utility;
    }

    public DIADiagram getClassOfImports(CallContext context) {
        return this.class_of_imports;
    }

    protected Vector<DIADiagram> states_of_class;
    protected void addStateOfClass(CallContext context, DIADiagram state_utility) {
        if (this.states_of_class == null) {
            this.states_of_class = new Vector<DIADiagram>();
        }
        this.states_of_class.add(state_utility);
        if (    state_utility.getStateType(context) == null
             || state_utility.getStateType(context).length() == 0
           ) {
            if ("main".equals(state_utility.getStateVariable(context))) {
                StateCategory sc = state_utility.getStateCategory(context);
                if (sc == StateCategory.Active) {
                    state_utility.setStateType(context, "Active");
                    return;
                }
                if (sc == StateCategory.Passive) {
                    if (state_utility.getInboundTransitions(context) != null) {
                        for (DIADiagram ibt : state_utility.getInboundTransitions(context)) {
                            if (    ibt.getSourceState(context).getIsTerminalState(context) == true
                                 && ibt.getSourceState(context).getIsFinal(context) == false
                               ) {
                                state_utility.setStateType(context, "Ready");
                                return;
                            }
                        }
                    }
                    if (state_utility.getOutboundTransitions(context) != null) {
                        for (DIADiagram obt : state_utility.getOutboundTransitions(context)) {
                            if (    obt.getTargetState(context).getIsTerminalState(context) == true
                                 && obt.getTargetState(context).getIsFinal(context) == true
                               ) {
                                state_utility.setStateType(context, "Finished");
                                return;
                            }
                        }
                        for (DIADiagram obt : state_utility.getOutboundTransitions(context)) {
                            if (obt.getTargetState(context).getStateCategory(context) == StateCategory.Active) {
                                state_utility.setStateType(context, "CompletedReadyAgain");
                                return;
                            }
                        }
                    }
                }
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)', state machine '%(name)' contains state '%(state)' with unclear state type semantics", "name", this.getName(context), "dia", this.diagram.getSourceInfo(context), "state", state_utility.getName(context)));
            } else {
                state_utility.setStateType(context, "None");
            }
        }
    }

    protected void setStatesOfClass(CallContext context, Vector<DIADiagram> states_of_class) {
        this.states_of_class = states_of_class;
    }

    public Vector<DIADiagram> getStatesOfClass(CallContext context) {
        return this.states_of_class;
    }

    // es kann mehrere transitions_of_class zum gleichen name geben
    // diese werden zusammengefaßt in der ersten angemeldeten transition_of_class
    // das geschieht deswegen, weil ein Verknüpfungsobjekt benötigt wird
    // etwa für die beforestates usw.
    protected Vector<DIADiagram> transitions_of_class;
    protected Map<String,DIADiagram> transitions_of_class_by_id;
    protected void addTransitionOfClass(CallContext context, DIADiagram transition_utility) {
        String transition_name = transition_utility.getName(context);
//         System.err.println("ADD TOC: " + transition_name);
//         System.err.println("TO: " + this);
        if (this.transitions_of_class == null) {
//             System.err.println("NEW VEC");
            this.transitions_of_class = new Vector<DIADiagram>();
            this.transitions_of_class_by_id = new HashMap<String,DIADiagram>();
        }
        DIADiagram transition_of_class = this.transitions_of_class_by_id.get(transition_name);
        if (transition_of_class == null) {
//             System.err.println("FIRST");
            transition_of_class = transition_utility;
            this.transitions_of_class.add(transition_of_class);
            this.transitions_of_class_by_id.put(transition_name, transition_of_class);
        }
        // hinweis: bei der ersen angemeldeten transition_of_class
        // wird sie sich hier selbst zugewiesen, das ist beabsichtigt
        transition_of_class.attachTransitionOfClass(context, transition_utility);
                    
        if (this.feature_extensions_map != null) {
            Vector<DIADiagram> feature_extensions = this.feature_extensions_map.get(transition_name);
            if (feature_extensions != null) {
//                 for (DIADiagram feature_extension : feature_extensions) {
//                     System.err.println("ATTACHING " + feature_extension.getName(context) + " TO TRANSITION " + transition_name + " FROM " + this.getName(context));
//                 }
                transition_utility.setExtensions(context, feature_extensions);
//             } else {
//                 System.err.println("NO PROPS FOR TRANSITION " + transition_name + " FROM " + this.getName(context));
            }
        }
    }

    protected Vector<DIADiagram> attached_transitions_of_class;
    protected void attachTransitionOfClass(CallContext context, DIADiagram transition_of_class) {
        if (this.attached_transitions_of_class == null) {
            this.attached_transitions_of_class = new Vector<DIADiagram>();
        }
        this.attached_transitions_of_class.add(transition_of_class);
//         System.err.println("NOW: " + this.attached_transitions_of_class.size());
    }

    protected Vector<DIADiagram> getAttachedTransitionsOfClass(CallContext context) {
        return this.attached_transitions_of_class;
    }

    protected void setTransitionsOfClass(CallContext context, Vector<DIADiagram> transitions_of_class) {
        this.transitions_of_class = transitions_of_class;
    }

    public Vector<DIADiagram> getTransitionsOfClass(CallContext context) {
        return this.transitions_of_class;
    }

    protected void addReachableStates(CallContext context, DIADiagram state_utility, Set<DIADiagram> already_added_states, Set<DIADiagram> already_added_transitions, String state_variable) {
        if (already_added_states == null) {
            already_added_states = new HashSet<DIADiagram>();
            already_added_transitions = new HashSet<DIADiagram>();
            this.is_state_machine = true;
        } else {
            if (already_added_states.contains(state_utility)) {
                return;
            }
        }
//         System.err.println("REACHABLE: " + state_utility.getName(context));
        boolean is_initial_state = false;
        boolean is_end_state = false;
        if (state_utility.getIsTerminalState(context)) {
            if (state_utility.getIsFinal(context)) {
                is_end_state = true;
            } else {
                is_initial_state = true;
            }
        } else if (state_utility.getIsPseudoState(context)) {
        } else {
            state_utility.setStateVariable(context, state_variable);
            if (state_utility.getName(context).equals("*") == false) {
                this.addStateOfClass(context, state_utility);
            }
        }
        already_added_states.add(state_utility);

        if (state_utility.getOutboundTransitions(context) != null) {
            for (DIADiagram transition : state_utility.getOutboundTransitions(context)) {
//                 System.err.println("OB: " + transition);
//                 System.err.println("OB-T: " + transition.getTargetState(context));
                if (already_added_transitions.contains(transition) == false) {
//                     System.err.println("OB-T +");
                    already_added_transitions.add(transition);
                    if (    state_utility.getIsTerminalState(context) == false
                         // darf er doch, jawoll ... && state_utility.getIsPseudoState(context) == false
                         && transition.getTargetState(context).getIsTerminalState(context) == false
                       ) {
//                         System.err.println("OB-T +!");
                        if (transition.getName(context) != null && transition.getName(context).length() != 0) {
                            this.addTransitionOfClass(context, transition);
                        }
                    }
                    this.addReachableStates(context, transition.getTargetState(context), already_added_states, already_added_transitions, state_variable);
                    if (is_initial_state) {
                        String guard = transition.getGuard(context);
                        if (guard == null || guard.isEmpty()) { guard = "true"; }
                        this.addInitialState(context, transition.getTargetState(context), state_variable, guard);
                    }
                }
            }
        }
        if (state_utility.getInboundTransitions(context) != null) {
            for (DIADiagram transition : state_utility.getInboundTransitions(context)) {
//                 System.err.println("IB: " + transition);
//                 System.err.println("IB-T: " + transition.getSourceState(context));
                if (already_added_transitions.contains(transition) == false) {
//                     System.err.println("IB-T +");

// das alles hier ausgeklammert, damit er "rückwärts" nur geht um vollständig zu sein
// aber die gesamte verarbeitung nur 1x bei vorwärts macht (DRY code)
//                     already_added_transitions.add(transition);
//                     if (    state_utility.getIsTerminalState(context) == false
//                          && transition.getSourceState(context).getIsTerminalState(context) == false
//                          && transition.getSourceState(context).getIsPseudoState(context) == false
//                        ) {
// //                         System.err.println("IB-T +!");
//                         if (transition.getName(context) != null && transition.getName(context).length() != 0) {
//                             this.addTransitionOfClass(context, transition);
//                         }
//                     }
                    this.addReachableStates(context, transition.getSourceState(context), already_added_states, already_added_transitions, state_variable);
                }
            }
        }
    }

    protected Vector<DIADiagram> initial_states;
    public Vector<DIADiagram> getInitialStates(CallContext context) {
        return this.initial_states;
    }
    public void addInitialState(CallContext context, DIADiagram initial_state, String state_variable, String initial_state_condition) {
        if (this.initial_states == null) {
            this.initial_states = new Vector<DIADiagram>();
        }
        initial_state.setStateVariable(context, state_variable);
        initial_state.setInitialStateCondition(context, initial_state_condition);
        for (DIADiagram is : this.initial_states) {
            if (    is.getStateVariable(context).equals(initial_state.getStateVariable(context))
                 && is.getInitialStateCondition(context).equals(initial_state.getInitialStateCondition(context))
               ) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)', state machine '%(name)' contains more than one initial state for state variable '%(variable)' and condition '%(condition)'", "name", this.getName(context), "dia", this.diagram.getSourceInfo(context), "variable", state_variable, "condition", initial_state_condition));
            }
        }
        this.initial_states.add(initial_state);
    }

    protected Vector<String[]> initial_states_of_transaction;
    public Vector<String[]> getInitialStatesOfTransaction(CallContext context) {
        if (this.initial_states_of_transaction == null) {
            this.initial_states_of_transaction = new Vector<String[]>();

            Map<String,Vector<DIADiagram>> is_by_sv = new HashMap<String,Vector<DIADiagram>>();
            for (DIADiagram is : this.getInitialStates(context)) {
                String sv = is.getStateVariable(context);
                Vector<DIADiagram> sv_is = is_by_sv.get(sv);
                if (sv_is == null) {
                    sv_is = new Vector<DIADiagram>();
                    is_by_sv.put(sv, sv_is);
                }
                sv_is.add(is);
            }
            for (String sv : is_by_sv.keySet()) {
                Vector<DIADiagram> sv_is = is_by_sv.get(sv);
                boolean first_if = true;
                for (DIADiagram is : sv_is) {
                    if (is.getInitialStateCondition(context).equals("else") == false) {
                        String[] ts = new String[4];
                        ts[0] = is.getInitialStateCondition(context);
                        ts[1] = is.getName(context);
                        ts[2] = is.getStateVariable(context);
                        ts[3] = (new Boolean(first_if)).toString();
                        this.initial_states_of_transaction.add(ts);
                        first_if = false;
                    }
                }
                boolean first_else = true;
                for (DIADiagram is : sv_is) {
                    if (is.getInitialStateCondition(context).equals("else") == true) {
                        String[] ts = new String[4];
                        ts[0] = "true";
                        ts[1] = is.getName(context);
                        ts[2] = is.getStateVariable(context);
                        ts[3] = (new Boolean(first_if)).toString();
                        this.initial_states_of_transaction.add(ts);
                        first_if = false;
                        if (first_else == false) {
                            this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)', state machine '%(name)' contains more than one else condition in initial state for state variable '%(variable)'", "name", this.getName(context), "dia", this.diagram.getSourceInfo(context), "variable", is.getStateVariable(context)));
                        }
                        first_else = false;
                    }
                }
            }
        }
        return this.initial_states_of_transaction;
    }

    public String getTransactionInitialisation(CallContext context) {
        StringBuilder sb = new StringBuilder();
        Map<String,Vector<DIADiagram>> is_by_sv = new HashMap<String,Vector<DIADiagram>>();
        for (DIADiagram is : this.getInitialStates(context)) {
            String sv = is.getStateVariable(context);
            Vector<DIADiagram> sv_is = is_by_sv.get(sv);
            if (sv_is == null) {
                sv_is = new Vector<DIADiagram>();
                is_by_sv.put(sv, sv_is);
            }
            sv_is.add(is);
        }
        for (String sv : is_by_sv.keySet()) {
            Vector<DIADiagram> sv_is = is_by_sv.get(sv);
            boolean first_if = true;
            for (DIADiagram is : sv_is) {
                if (is.getInitialStateCondition(context).equals("else") == false) {
                    if (first_if == false) { sb.append("else "); }
                    first_if = false;
                    sb.append("if (");
                    sb.append(is.getInitialStateCondition(context));
                    sb.append(") { ");
                    sb.append("==");
                    sb.append(is.getStateVariable(context));
                    sb.append(">> ");
                    sb.append(is.getName(context));
                    sb.append(";");
                    sb.append(" }");
                    sb.append("\n");
                }
            }
            boolean first_else = true;
            for (DIADiagram is : sv_is) {
                if (is.getInitialStateCondition(context).equals("else") == true) {
                    if (first_if == false) { sb.append("else "); }
                    first_if = false;
                    if (first_else == false) {
                        this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)', state machine '%(name)' contains more than one else condition in initial state for state variable '%(variable)'", "name", this.getName(context), "dia", this.diagram.getSourceInfo(context), "variable", is.getStateVariable(context)));
                    }
                    first_else = false;
                    sb.append("{ ");
                    sb.append("==");
                    sb.append(is.getStateVariable(context));
                    sb.append(">> ");
                    sb.append(is.getName(context));
                    sb.append(";");
                    sb.append(" }");
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    protected boolean is_state_machine;
    public boolean getIsStateMachine (CallContext context) {
        return this.is_state_machine;
    }

    protected Vector<DIADiagram> constraints;
    public Vector<DIADiagram> getConstraints(CallContext context) {
        if (this.constraints == null) {
            this.constraints = new Vector<DIADiagram>();

// DELETEME
//             if (this.node != null) {
//                 for (String layer : layers) {
//                     for (XMLNode constraint_node : this.node.resolveXPath(context, "/dia:diagram/dia:layer[@name='" + layer + "']/dia:object[@type='UML - Constraint']", namespaces).getNodes(context)) {

            if (this.layer_node != null) {
                for (XMLNode constraint_node : this.layer_node.getChildElementsByFilters(context, constraint_filter).getNodes(context)) {
                    DIADiagram constraint_utility = new DIADiagram(context, constraint_node, this.root, NodeType.Constraint);
                    this.constraints.add(constraint_utility);
                }
            }
        }
        return this.constraints;
    }

    protected HashMap<String,DIADiagram> features_by_id;

    protected void addFeature(CallContext context, DIADiagram feature, String alternate_id) {
        if (this.features_by_id == null) {
            this.features_by_id = new HashMap<String,DIADiagram>();
        }
        this.features_by_id.put(feature.getId(context), feature);
        if (alternate_id != null) {
            this.features_by_id.put(alternate_id, feature);
        }
    }

    protected DIADiagram getFeature(CallContext context, String id) {
        this.getAttributes(context);
        this.getOperations(context);
        if (this.features_by_id == null) { return null; }
        return this.features_by_id.get(id);
    }

    protected Vector<String[]> properties;
    public Vector<String[]> getProperties(CallContext context) {
        if (this.properties == null) {
            if (this.node != null) {
                // for (XMLNode property_node : this.node.resolveXPath(context, "dia:attribute[starts-with(@name,'custom:')]", namespaces).getNodes(context)) {
                for (XMLNode property_node : this.node.getChildElementsByRegExp(context, property_filter).getNodes(context)) {
                    String[] property = new String[2];

// DELETEME
//                     property[0] = property_node.resolveXPath(context, "@name", namespaces).toString(context).replaceFirst("^custom:","");
//                     property[1] = property_node.resolveXPath(context, "dia:string", namespaces).toString(context).replaceFirst("(?s-m:^#(.*)#$)","$1");

                    property[0] = property_node.getAttribute(context, "name").replaceFirst("^custom:","");
                    property[1] = property_node.getChildElementsByFilters(context, string_filter).toString(context).replaceFirst("(?s-m:^#(.*)#$)","$1");
                    if (property[1] != null && property[1].length() != 0) {
                        property[1] = Encoding.recode(context, property[1], Encoding.XML, Encoding.UTF8);
                        if (this.properties == null) {
                            this.properties = new Vector<String[]>();
                        }
                        this.properties.add(property);
                    }
                }
            }
        }
        return this.properties;
    }

    static protected long startt;
    static protected long lastt;
    static protected long count;

    protected String ocp;
    public String getOCP(CallContext context) {
        if (this.ocp == null) {
            Vector<String[]> properties = this.getProperties(context);
            if (properties == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains empty extension '%(extension)'", "extension", this.getDIAType(context), "dia", this.diagram.getSourceInfo(context)));
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<Extension CLASS=\"" + this.getDIAType(context) + "\" xmlns=\"http://xmlns.sphenon.com/com/sphenon/ad/adcore/model\" xmlns:code=\"code\">\n");
            for (String[] property : properties) {
                StringBuilder xml = null;
                try {
                    xml = WIMLProperty.parseWIML(property[1]).dumpToXML("    ", null);
                } catch (ParseException pe) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In extension '%(extension)', property '%(property)' has an invalid value: '%(value)'", "extension", this.getDIAType(context), "property", property[0], "value", property[1]);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
                if (xml == null) {
                    sb.append("  <" + property[0] + "/>\n");
                } else {
                    if (xml.length()>0 && xml.charAt(0)=='<') {
                        sb.append("  <" + property[0] + ">\n" + xml  + "  </" + property[0] + ">\n");
                    } else {
                        sb.append("  <" + property[0] + ">" + xml  + "</" + property[0] + ">\n");
                    }
                }
            }
            sb.append("</Extension>\n");
            this.ocp = sb.toString();

            if (debug) {
                System.err.println("================================ OCP ==================================");
                System.err.println(this.ocp);
                System.err.println("=======================================================================");
            }
//                 long totalm = java.lang.Runtime.getRuntime().totalMemory();
//                 long freem = java.lang.Runtime.getRuntime().freeMemory();
//                 long now = System.currentTimeMillis();
//                 long totald;
//                 long lastd;
//                 if (startt == 0) {
//                     startt = now;
//                     lastt = now;
//                     totald = 0;
//                     lastd = 0;
//                 } else {
//                     totald = now - startt;
//                     lastd = now - lastt;
//                     lastt = now;
//                 }

//                 System.err.println("GRUNZ " + count + " " + totalm + " " + freem + " " + (totalm - freem) + " " + lastd + " " + totald + " ");
//                 count++;

                // java.lang.Runtime.getRuntime().gc();

//             if (this.ocp.matches("(?s).*this.getDeleteable.*")) {
//                 System.err.println("...................................");
//                 System.err.println(com.sphenon.basics.debug.RuntimeStep.getStackDump(context));
//                 System.err.println("...................................");
//                 (new Throwable()).printStackTrace();
//             }
        }
        return this.ocp;
    }

    protected BuildText build_text;
    public BuildText getBuildText(CallContext context) {
        if (this.build_text == null) {
//             System.err.println("GBT: " + this.getDIAType(context));
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            Vector<String[]> properties = this.getProperties(context);
            if (properties == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains empty extension '%(extension)'", "extension", this.getDIAType(context), "dia", this.diagram.getSourceInfo(context)));
                return null;
            }
            for (String[] property : properties) {
                StringBuilder psb = new StringBuilder();
                psb.append(property[0]);
                psb.append("=");
                boolean is_assignment = property[1].matches("(?s)^ *[A-Za-z0-9_]+ *=.*");
                if (is_assignment) { psb.append("{"); }
                psb.append(property[1]);
                if (is_assignment) { psb.append("}"); }
                WIMLProperty wp;
//                 System.err.println("GBT PROP: " + psb.toString());
                try {
                    wp = WIMLProperty.parseWIMLProperty(psb.toString());
                } catch (ParseException pe) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In extension '%(extension)', property '%(property)' has an invalid value: '%(value)'", "extension", this.getDIAType(context), "property", property[0], "value", property[1]);
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
//                 System.err.println("GBT WP: ");
//                 System.err.println(wp.dumpToASCII("        ", null).toString());
                named_items.append(context, WIMLPropertyBuildText.create(context, wp));
            }
            this.build_text = new BuildTextComplex_String(context, "Extension", "", "", this.getDIAType(context), "", "", "<DIAProperty>", named_items);
        }
        return this.build_text;
    }

    protected String slot_specifications_ocp;
    public String getSlotSpecificationsOCP(CallContext context) {
        if (this.slot_specifications_ocp == null) {
            Vector<String[]> properties = this.getProperties(context);
            String ssp = null;
            StringBuilder xml = null;
            if (properties != null) {
                for (String[] property : properties) {
                    if (property[0].equals("SlotSpecifications")) {
                        ssp = property[1];
                        try {
                            xml = WIMLProperty.parseWIML(ssp).dumpToXML("  ", null);
                        } catch (ParseException pe) {
                            CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In object view '%(view)', slot specification has an invalid value: '%(value)'", "view", this.getDIAType(context), "value", ssp);
                            throw (ExceptionPreConditionViolation) null; // compiler insists
                        }
                        break;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<SlotSpecifications CLASS=\"com.sphenon.modelling.uml.tplinst.Vector_UMLSlotSpecification_long_\" xmlns=\"http://xmlns.sphenon.com/com/sphenon/ad/adcore/model\" xmlns:code=\"code\"");

            if (xml == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains object view '%(view)' with empty slot specification", "view", this.getDIAType(context), "dia", this.diagram.getSourceInfo(context)));
                sb.append("/>\n");
            } else {
                sb.append(">\n");
                sb.append("  " + xml  + "\n");
                sb.append("</SlotSpecifications>\n");
            }
            this.slot_specifications_ocp = sb.toString();
        }
        if (debug) {
            System.err.println("=============================== SSOCP =================================");
            System.err.println(this.slot_specifications_ocp);
            System.err.println("=======================================================================");
        }
        return this.slot_specifications_ocp;
    }

    protected BuildText slot_specifications_build_text;
    public BuildText getSlotSpecificationsBuildText(CallContext context) {
        if (this.slot_specifications_build_text == null) {
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            Vector<String[]> properties = this.getProperties(context);
            boolean found = false;
            if (properties != null) {
                for (String[] property : properties) {
                    if (property[0].equals("SlotSpecifications")) {
                        StringBuilder psb = new StringBuilder();
                        psb.append("SlotSpecifications=[");
                        psb.append(property[1].replaceAll("SlotSpecification=",""));
                        psb.append("]");
                        WIMLProperty wp;
                        try {
                            wp = WIMLProperty.parseWIMLProperty(psb.toString());
                        } catch (ParseException pe) {
                            CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In object view '%(view)', slot specification has an invalid value: '%(value)'", "view", this.getDIAType(context), "value", psb.toString());
                            throw (ExceptionPreConditionViolation) null; // compiler insists
                        }
                        this.slot_specifications_build_text = WIMLPropertyBuildText.create(context, wp).getItem1(context);
                        this.slot_specifications_build_text.setTypeName(context, "com.sphenon.modelling.uml.tplinst.Vector_UMLSlotSpecification_long_");
                        // Dumper.dump(context, "SlotSpecification", slot_specifications_build_text);
                        found = true;
                        break;
                    }
                }
            }
            if (found == false) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains object view '%(view)' with empty slot specification", "view", this.getDIAType(context), "dia", this.diagram.getSourceInfo(context)));
                return null;
            }
        }
        return this.slot_specifications_build_text;
    }

    protected String model_imports_ocp;
    public String getModelImportsOCP(CallContext context) {
        if (this.model_imports_ocp == null) {
            StringBuilder xml = null;
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps != null) {
                for (DIADiagram imp : imps) {
                    Vector<String[]> properties = imp.getProperties(context);
                    String mi = null;
                    if (properties != null) {
                        for (String[] property : properties) {
                            if (property[0].equals("ModelImports")) {
                                mi = property[1];
                                try {
                                    xml = WIMLProperty.parseWIML(mi).dumpToXML("  ", null);
                                } catch (ParseException pe) {
                                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, ModelImports has an invalid value: '%(value)'", "value", mi);
                                    throw (ExceptionPreConditionViolation) null; // compiler insists
                                }
                            }
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<ModelImports CLASS=\"com.sphenon.basics.many.tplinst.Vector_String_long_\" xmlns=\"http://xmlns.sphenon.com/com/sphenon/ad/adcore/model\" xmlns:code=\"code\"");

            if (xml == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with empty ModelImports", "dia", this.diagram.getSourceInfo(context)));
                sb.append("/>\n");
            } else {
                sb.append(">\n");
                sb.append("  " + xml  + "\n");
                sb.append("</ModelImports>\n");
            }
            this.model_imports_ocp = sb.toString();
        }
        if (debug) {
            System.err.println("=============================== MIOCP =================================");
            System.err.println(this.model_imports_ocp);
            System.err.println("=======================================================================");
//          System.err.println("###################################");
//          System.err.println(com.sphenon.basics.debug.RuntimeStep.getStackDump(context));
//          System.err.println("...................................");
//          (new Throwable()).printStackTrace();
//          System.err.println("###################################");
        }
        return this.model_imports_ocp;
    }

    protected BuildText model_imports_build_text;
    public BuildText getModelImportsBuildText(CallContext context) {
        if (this.model_imports_build_text == null) {
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps == null) { return null; }

            StringBuilder psb = new StringBuilder();
            psb.append("ModelImports=[");
            boolean first = true;
            for (DIADiagram imp : imps) {
                Vector<String[]> properties = imp.getProperties(context);
                boolean found = false;
                if (properties != null) {
                    for (String[] property : properties) {
                        if (property[0].equals("ModelImports")) {
                            String propval = property[1].replaceFirst("^ *\\[? *","").replaceFirst(" *\\]? *$","");
                            if (propval.matches(" *") == false) {
                                if ( ! first) { psb.append(","); }
                                first = false;
                                psb.append(propval);
                            }
                            found = true;
                            break;
                        }
                    }
                }
                if (false && found == false) {
                    this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with no ModelImports property", "dia", this.diagram.getSourceInfo(context)));
                }
            }
            psb.append("]");

            if (first) { return null; }

            WIMLProperty wp;
            try {
                wp = WIMLProperty.parseWIMLProperty(psb.toString());
            } catch (ParseException pe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, ModelImports has an invalid value: '%(value)'", "value", psb.toString());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            this.model_imports_build_text = WIMLPropertyBuildText.create(context, wp).getItem1(context);
            this.model_imports_build_text.setTypeName(context, "com.sphenon.basics.many.tplinst.Vector_String_long_");
        }
        return this.model_imports_build_text;
    }

    protected String internal_model_imports_ocp;
    public String getInternalModelImportsOCP(CallContext context) {
        if (this.internal_model_imports_ocp == null) {
            StringBuilder xml = null;
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps != null) {
                for (DIADiagram imp : imps) {
                    Vector<String[]> properties = imp.getProperties(context);
                    String imi = null;
                    if (properties != null) {
                        for (String[] property : properties) {
                            if (property[0].equals("InternalModelImports")) {
                                imi = property[1];
                                try {
                                    xml = WIMLProperty.parseWIML(imi).dumpToXML("  ", null);
                                } catch (ParseException pe) {
                                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, InternalModelImports has an invalid value: '%(value)'", "value", imi);
                                    throw (ExceptionPreConditionViolation) null; // compiler insists
                                }
                            }
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<InternalModelImports CLASS=\"com.sphenon.basics.many.tplinst.Vector_String_long_\" xmlns=\"http://xmlns.sphenon.com/com/sphenon/ad/adcore/model\" xmlns:code=\"code\"");

            if (xml == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with empty InternalModelImports", "dia", this.diagram.getSourceInfo(context)));
                sb.append("/>\n");
            } else {
                sb.append(">\n");
                sb.append("  " + xml  + "\n");
                sb.append("</InternalModelImports>\n");
            }
            this.internal_model_imports_ocp = sb.toString();
        }
        if (debug) {
            System.err.println("=============================== IMIOCP ================================");
            System.err.println(this.internal_model_imports_ocp);
            System.err.println("=======================================================================");
        }
        return this.internal_model_imports_ocp;
    }

    protected BuildText internal_model_imports_build_text;
    public BuildText getInternalModelImportsBuildText(CallContext context) {
        if (this.internal_model_imports_build_text == null) {
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps == null) { return null; }

            StringBuilder psb = new StringBuilder();
            psb.append("InternalModelImports=[");
            boolean first = true;
            for (DIADiagram imp : imps) {
                Vector<String[]> properties = imp.getProperties(context);
                boolean found = false;
                if (properties != null) {
                    for (String[] property : properties) {
                        if (property[0].equals("InternalModelImports")) {
                            String propval = property[1].replaceFirst("^ *\\[? *","").replaceFirst(" *\\]? *$","");
                            if (propval.matches(" *") == false) {
                                if ( ! first) { psb.append(","); }
                                first = false;
                                psb.append(propval);
                            }
                            found = true;
                            break;
                        }
                    }
                }
                if (false && found == false) {
                    this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with no InternalModelImports property", "dia", this.diagram.getSourceInfo(context)));
                }
            }
            psb.append("]");

            if (first) { return null; }

            WIMLProperty wp;
            try {
                wp = WIMLProperty.parseWIMLProperty(psb.toString());
            } catch (ParseException pe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, InternalModelImports has an invalid value: '%(value)'", "value", psb.toString());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            this.internal_model_imports_build_text = WIMLPropertyBuildText.create(context, wp).getItem1(context);
            this.internal_model_imports_build_text.setTypeName(context, "com.sphenon.basics.many.tplinst.Vector_String_long_");
        }
        return this.internal_model_imports_build_text;
    }

    protected String stereotype_model_imports_ocp;
    public String getStereotypeModelImportsOCP(CallContext context) {
        if (this.stereotype_model_imports_ocp == null) {
            StringBuilder xml = null;
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps != null) {
                for (DIADiagram imp : imps) {
                    Vector<String[]> properties = imp.getProperties(context);
                    String smi = null;
                    if (properties != null) {
                        for (String[] property : properties) {
                            if (property[0].equals("StereotypeModelImports")) {
                                smi = property[1];
                                try {
                                    xml = WIMLProperty.parseWIML(smi).dumpToXML("  ", null);
                                } catch (ParseException pe) {
                                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, StereotypeModelImports has an invalid value: '%(value)'", "value", smi);
                                    throw (ExceptionPreConditionViolation) null; // compiler insists
                                }
                            }
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<StereotypeModelImports CLASS=\"com.sphenon.basics.many.tplinst.Vector_String_long_\" xmlns=\"http://xmlns.sphenon.com/com/sphenon/ad/adcore/model\" xmlns:code=\"code\"");

            if (xml == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with empty StereotypeModelImports", "dia", this.diagram.getSourceInfo(context)));
                sb.append(">\n");
                sb.append(" "+standard_stereotype+"\n");
                sb.append("</StereotypeModelImports>\n");
            } else {
                sb.append(">\n");
                sb.append(" "+standard_stereotype+"\n");
                sb.append("  " + xml  + "\n");
                sb.append("</StereotypeModelImports>\n");
            }
            this.stereotype_model_imports_ocp = sb.toString();
        }
        if (debug) {
            System.err.println("=============================== SMIOCP ================================");
            System.err.println(this.stereotype_model_imports_ocp);
            System.err.println("=======================================================================");
        }
        return this.stereotype_model_imports_ocp;
    }

    protected BuildText stereotype_model_imports_build_text;
    public BuildText getStereotypeModelImportsBuildText(CallContext context) {
        // System.err.println("SMIBT");
        if (this.stereotype_model_imports_build_text == null) {
            // System.err.println("null");
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            // System.err.println("imps " + imps);
            if (imps == null) { return null; }

            StringBuilder psb = new StringBuilder();
            psb.append("StereotypeModelImports=[ \"org.uml.stereotypes.*\"");
            boolean first = true;
            for (DIADiagram imp : imps) {
                // System.err.println("imp");
                Vector<String[]> properties = imp.getProperties(context);
                boolean found = false;
                if (properties != null) {
                    // System.err.println("props");
                    for (String[] property : properties) {
                        // System.err.println("prop 0 " + property[0]);
                        // System.err.println("prop 1 " + property[1]);
                        if (property[0].equals("StereotypeModelImports")) {
                            String propval = property[1].replaceFirst("^ *\\[? *","").replaceFirst(" *\\]? *$","");
                            // System.err.println("val " + propval);
                            if (propval.matches(" *") == false) {
                                psb.append(",");
                                first = false;
                                psb.append(propval);
                            }
                            found = true;
                            break;
                        }
                    }
                }
                if (false && found == false) {
                    this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with no StereotypeModelImports property", "dia", this.diagram.getSourceInfo(context)));
                }
            }
            psb.append("]");

            // System.err.println("first " + first);
            // if (first) { return null; }

            // System.err.println("psb " + psb.toString());
            WIMLProperty wp;
            try {
                wp = WIMLProperty.parseWIMLProperty(psb.toString());
            } catch (ParseException pe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, StereotypeModelImports has an invalid value: '%(value)'", "value", psb.toString());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            // System.err.println("wp " + wp.dumpToASCII("", null).toString());
            this.stereotype_model_imports_build_text = WIMLPropertyBuildText.create(context, wp).getItem1(context);
            this.stereotype_model_imports_build_text.setTypeName(context, "com.sphenon.basics.many.tplinst.Vector_String_long_");
        }
        return this.stereotype_model_imports_build_text;
    }

    protected String external_imports_ocp;
    public String getExternalImportsOCP(CallContext context) {
        if (this.external_imports_ocp == null) {
            StringBuilder xml = null;
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps != null) {
                for (DIADiagram imp : imps) {
                    Vector<String[]> properties = imp.getProperties(context);
                    String ei = null;
                    if (properties != null) {
                        for (String[] property : properties) {
                            if (property[0].equals("ExternalImports")) {
                                ei = property[1];
                                try {
                                    xml = WIMLProperty.parseWIML(ei).dumpToXML("  ", null);
                                } catch (ParseException pe) {
                                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, ExternalImports has an invalid value: '%(value)'", "value", ei);
                                    throw (ExceptionPreConditionViolation) null; // compiler insists
                                }
                            }
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<ExternalImports CLASS=\"com.sphenon.basics.many.tplinst.Vector_String_long_\" xmlns=\"http://xmlns.sphenon.com/com/sphenon/ad/adcore/model\" xmlns:code=\"code\"");

            if (xml == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with empty ExternalImports", "dia", this.diagram.getSourceInfo(context)));
                sb.append("/>\n");
            } else {
                sb.append(">\n");
                sb.append("  " + xml  + "\n");
                sb.append("</ExternalImports>\n");
            }
            this.external_imports_ocp = sb.toString();
        }
        if (debug) {
            System.err.println("=============================== EIOCP =================================");
            System.err.println(this.external_imports_ocp);
            System.err.println("=======================================================================");
        }
        return this.external_imports_ocp;
    }

    protected BuildText external_imports_build_text;
    public BuildText getExternalImportsBuildText(CallContext context, boolean with_primitives) {
        if (this.external_imports_build_text == null) {
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps == null) { return null; }

            boolean first = true;
            StringBuilder psb = new StringBuilder();
            psb.append("ExternalImports=[ ");
            if (with_primitives) {
                psb.append("\"java.lang.Boolean\", \"java.lang.Byte\",\"java.lang.Short\",\"java.lang.Integer\",\"java.lang.Long\",\"java.lang.Float\",\"java.lang.Double\",\"java.lang.Character\",\"java.lang.String\",\"java.util.Date\",\"java.lang.Object\"");
                first = false;
            }

            for (DIADiagram imp : imps) {
                Vector<String[]> properties = imp.getProperties(context);
                boolean found = false;
                if (properties != null) {
                    for (String[] property : properties) {
                        if (property[0].equals("ExternalImports")) {
                            String propval = property[1].replaceFirst("^ *\\[? *","").replaceFirst(" *\\]? *$","");
                            if (propval.matches(" *") == false) {
                                if ( ! first) { psb.append(","); }
                                first = false;
                                psb.append(propval);
                            }
                            found = true;
                            break;
                        }
                    }
                }
                if (false && found == false) {
                    this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with no ExternalImports property", "dia", this.diagram.getSourceInfo(context)));
                }
            }
            psb.append(" ]");

            if (first) { return null; }

            WIMLProperty wp;
            try {
                wp = WIMLProperty.parseWIMLProperty(psb.toString());
            } catch (ParseException pe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, ExternalImports has an invalid value: '%(value)'", "value", psb.toString());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            this.external_imports_build_text = WIMLPropertyBuildText.create(context, wp).getItem1(context);
            this.external_imports_build_text.setTypeName(context, "com.sphenon.basics.many.tplinst.Vector_String_long_");
        }
        return this.external_imports_build_text;
    }

    protected String primitive_imports_ocp;
    public String getPrimitiveImportsOCP(CallContext context) {
        if (this.primitive_imports_ocp == null) {
            StringBuilder xml = null;
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps != null) {
                for (DIADiagram imp : imps) {
                    Vector<String[]> properties = imp.getProperties(context);
                    String pi = null;
                    if (properties != null) {
                        for (String[] property : properties) {
                            if (property[0].equals("PrimitiveImports")) {
                                pi = property[1];
                                try {
                                    xml = WIMLProperty.parseWIML(pi).dumpToXML("  ", null);
                                } catch (ParseException pe) {
                                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, PrimitiveImports has an invalid value: '%(value)'", "value", pi);
                                    throw (ExceptionPreConditionViolation) null; // compiler insists
                                }
                            }
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<PrimitiveImports CLASS=\"com.sphenon.basics.many.tplinst.Vector_String_long_\" xmlns=\"http://xmlns.sphenon.com/com/sphenon/ad/adcore/model\" xmlns:code=\"code\"");

            if (xml == null) {
                this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with empty PrimitiveImports", "dia", this.diagram.getSourceInfo(context)));
                sb.append("/>\n");
            } else {
                sb.append(">\n");
                sb.append("  " + xml  + "\n");
                sb.append("</PrimitiveImports>\n");
            }
            this.primitive_imports_ocp = sb.toString();
        }
        if (debug) {
            System.err.println("=============================== PIOCP =================================");
            System.err.println(this.primitive_imports_ocp);
            System.err.println("=======================================================================");
        }
        return this.primitive_imports_ocp;
    }

    protected BuildText primitive_imports_build_text;
    public BuildText getPrimitiveImportsBuildText(CallContext context, boolean with_primitives) {
        if (this.primitive_imports_build_text == null) {
            Vector_Pair_BuildText_String__long_ named_items = Factory_Vector_Pair_BuildText_String__long_.construct(context);
            Vector<DIADiagram> imps = this.node_type == NodeType.Package ? this.getPackageImports(context) : this.node_type == NodeType.Class ? this.getImportsOfClass(context) : null;
            if (imps == null) { return null; }

            boolean first = true;
            StringBuilder psb = new StringBuilder();
            psb.append("PrimitiveImports=[ ");
            if (with_primitives) {
                psb.append("\"boolean\", \"byte\", \"short\", \"int\", \"long\", \"float\", \"double\", \"char\"");
                first = false;
            }

            for (DIADiagram imp : imps) {
                Vector<String[]> properties = imp.getProperties(context);
                boolean found = false;
                if (properties != null) {
                    for (String[] property : properties) {
                        if (property[0].equals("PrimitiveImports")) {
                            String propval = property[1].replaceFirst("^ *\\[? *","").replaceFirst(" *\\]? *$","");
                            if (propval.matches(" *") == false) {
                                if ( ! first) { psb.append(", "); }
                                first = false;
                                psb.append(propval);
                            }
                            found = true;
                            break;
                        }
                    }
                }
                if (false && found == false) {
                    this.addProblemStatus(context, ProblemState.CAUTION, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains imports with no PrimitiveImports property", "dia", this.diagram.getSourceInfo(context)));
                }
            }
            psb.append(" ]");

            if (first) { return null; }

            WIMLProperty wp;
            try {
                wp = WIMLProperty.parseWIMLProperty(psb.toString());
            } catch (ParseException pe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, pe, "In imports, PrimitiveImports has an invalid value: '%(value)'", "value", psb.toString());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            this.primitive_imports_build_text = WIMLPropertyBuildText.create(context, wp).getItem1(context);
            this.primitive_imports_build_text.setTypeName(context, "com.sphenon.basics.many.tplinst.Vector_String_long_");
        }
        return this.primitive_imports_build_text;
    }


    protected Vector<DIADiagram> parameters;
    public Vector<DIADiagram> getParameters(CallContext context) {
        if (this.parameters == null) {
            this.parameters = new Vector<DIADiagram>();
            if (this.node != null) {

// DELETEME
//                 for (XMLNode parameter_node : this.node.resolveXPath(context, "dia:attribute[@name='parameters']/dia:composite[@type='umlparameter']", namespaces).getNodes(context)) {

                for (XMLNode parameter_node : this.node.getChildElementsByFilters(context, parameter_filter_1, parameter_filter_2).getNodes(context)) {
                    DIADiagram parameter_utility = new DIADiagram(context, parameter_node, this.root, NodeType.Parameter);
                    this.parameters.add(parameter_utility);
                }
            }
        }
        return this.parameters;
    }

// template
// dia:boolean/@val
// templates

//     protected Vector<DIADiagram> templates;
//     public Vector<DIADiagram> getTemplates(CallContext context) {
//         if (this.templates == null) {
//             this.templates = new Vector<DIADiagram>();
//             for (XMLNode template_node : this.node.resolveXPath(context, "dia:attribute[@name='templates']/dia:composite[@type='umltemplate']", namespaces).getNodes(context)) {
//                 DIADiagram template_utility = new DIADiagram(context, template_node, this.root);
//                 this.templates.add(template_utility);
//             }
//         }
//         return this.templates;
//     }

    public void addEnd(CallContext context, DIADiagram class_utility, String constraint) {
        if (this.ends == null) {
            this.ends = new Vector<DIADiagram>();
        }
        if (this.node != null) {
            if (this.node_type == NodeType.Pattern) {
                DIADiagram end_utility = new DIADiagram(context, this.node, this.root, NodeType.PatternEnd);
                String[] role = constraint.split("/");
                end_utility.setRole(context, role[0]);
                if (role.length > 1) {
                    DIADiagram feature_utility = class_utility.getFeature(context, role[1]);
                    if (feature_utility == null) {
                        this.addProblemStatus(context, ProblemState.ERROR, CustomaryContext.create((Context)context).createConfigurationError(context, "DIA file '%(dia)' contains pattern end with reference to nonexisting feature '%(feature)'", "feature", role[1], "dia", this.diagram.getSourceInfo(context)));
                    } else {
                        end_utility.setConnection(context, feature_utility);
                    }
                } else {
                    end_utility.setConnection(context, class_utility);
                }

                if (this.feature_extensions_map != null) {
                    String end_id_1 = "End:" + role[0] + "/" + class_utility.getName(context);
                    String end_id_2 = "End:" + role[0];
                    Vector<DIADiagram> feature_extensions = this.feature_extensions_map.get(end_id_1);
                    if (feature_extensions == null) {
                        feature_extensions = this.feature_extensions_map.get(end_id_2);
                    }
                    if (feature_extensions != null) {
                        end_utility.setExtensions(context, feature_extensions);
                    }
                }

                this.ends.add(end_utility);
            }
        }
    }

    public String getConnectionNameRelativeToClass(CallContext context) {
        if (this.connection == null) { return null; }

        if (    this.connection.node_type == NodeType.Attribute
             || this.connection.node_type == NodeType.Operation
             || this.connection.node_type == NodeType.AssociationEnd
           ) {
            return "." + this.connection.parent.getName(context) + "." + this.connection.getModelId(context);
        } else {
            return this.connection.getName(context);
        }
    }

    protected String state_variable;
    public String getStateVariable(CallContext context) {
        return state_variable;
    }
    public void setStateVariable(CallContext context, String state_variable) {
        this.state_variable = state_variable;
    }

    protected String initial_state_condition;
    public String getInitialStateCondition (CallContext context) {
        return this.initial_state_condition;
    }
    public void setInitialStateCondition (CallContext context, String initial_state_condition) {
        this.initial_state_condition = initial_state_condition;
    }

    protected String model_id;
    public String getModelId(CallContext context) {
        if (this.model_id == null) {
            this.model_id = this.getName(context);
            if (this.node_type == NodeType.Operation) {
                this.model_id += "(";
                boolean first = true;
                for (DIADiagram parameter : this.getParameters(context)) {
                    this.model_id += (first ? "" : ",") + "," + parameter.getType(context);
                }                    
                this.model_id += "):" + this.getType(context);
            }
        }
        return this.model_id;
    }

    protected String pattern_id;
    public String getPatternId(CallContext context) {
        if (this.pattern_id == null) {
            this.pattern_id = this.getName(context);
        }
        return this.pattern_id;
    }

    protected String pattern_stereotype;
    public String getPatternStereotype(CallContext context) {
        if (this.pattern_stereotype == null) {
            this.pattern_stereotype = this.getStereotypes(context)[0].split(":")[1];
        }
        return this.pattern_stereotype;
    }

    static protected enum FontStyle { Normal, Italic, Bold, BoldItalic };
    protected FontStyle font_style;
    public FontStyle getFontStyle(CallContext context) {
        if (this.font_style == null) {
            if (this.node != null) {
                if (this.node_type == NodeType.State) {
                    String fs = this.getAttributeFontStyle(context, "font", this.node.getChildElementsByFilters(context, viewname_filter_1, viewname_filter_2));
                    if (fs != null) {
                        if      (fs.equals( "0")) { this.font_style = FontStyle.Normal; }
                        else if (fs.equals( "8")) { this.font_style = FontStyle.Italic; }
                        else if (fs.equals("80")) { this.font_style = FontStyle.Bold; }
                        else if (fs.equals("88")) { this.font_style = FontStyle.BoldItalic; }
                    }
                }
            }
        }
        return this.font_style;
    }

    static protected enum StateCategory { Active, Passive, Unknown };
    protected StateCategory state_category;
    public StateCategory getStateCategory(CallContext context) {
        if (this.state_category == null) {
            FontStyle fs = this.getFontStyle(context);
            if (fs == FontStyle.Normal || fs == FontStyle.Bold) {
                this.state_category = StateCategory.Active;
            } else if (fs == FontStyle.Italic || fs == FontStyle.BoldItalic) {
                this.state_category = StateCategory.Passive;
            } else {
                this.state_category = StateCategory.Unknown;
            }
        }
        return this.state_category;
    }

    protected String guard;
    public String getGuard(CallContext context) {
        if (this.guard == null) {
            if (this.node != null) {
                this.guard = this.getAttributeString(context, "guard", this.node);
            }
        }
        return this.guard;
    }
    
    static protected RegularExpression idxre = new RegularExpression("([^#]*)(?:#(!?[0-9]+)(?:#(.*))?)?");
    //static protected RegularExpression idxre = new RegularExpression("([^#]*)(?:#([0-9]+)(?:#(.*))?)?");
    protected String index;
    public String getIndex(CallContext context) {
        if (this.index == null) {
            this.getComment(context);
        }
        return this.index;
    }

    static protected RegularExpression rore = new RegularExpression("([^\\{]*)(?:\\{(ReadOnly)\\})(.*)");

    protected Boolean read_only;
    public Boolean getReadOnly(CallContext context) {
        if (this.read_only == null) {
            this.getComment(context);
            if (this.read_only == null) {
                this.read_only = false;
            }
        }
        return this.read_only;
    }

    protected String dia_type;
    public String getDIAType(CallContext context) {
        if (this.dia_type == null) {
            if (this.node != null) {

// DELETEME
//                 this.dia_type = this.node.resolveXPath(context, "@type", namespaces).toString(context);

                this.dia_type = this.node.getAttribute(context, "type");
            }
        }
        return this.dia_type;
    }

    protected String constraint;
    public String getConstraint(CallContext context) {
        if (this.constraint == null) {
            if (this.node != null) {
                this.constraint = this.getAttributeString(context, "constraint", this.node);
            }
        }
        return this.constraint;
    }
    public void setConstraint(CallContext context, String constraint) {
        this.constraint = constraint;
    }

    protected String visibility;
      // 0 : public
      // 1 : private
      // 2 : protected
    public String getVisibility(CallContext context) {
        if (this.visibility == null) {
            if (this.node != null) {
                this.visibility = this.getAttributeEnum(context, "visibility", this.node);
            }
        }
        return this.visibility;
    }

    protected String direction;
      // 0 : none
      // 1 : A to B
      // 2 : B to A
    public String getDirection(CallContext context) {
        if (this.direction == null) {
            if (this.node != null) {
                this.direction = this.getAttributeEnum(context, "direction", this.node);
            }
        }
        return this.direction;
    }

    protected String inheritance_type;
      // 0 : abstract
      // 1 : polymorphic/virtual
      // 2 : leaf/final
    public String getInheritanceType(CallContext context) {
        if (this.inheritance_type == null) {
            if (this.node != null) {
                this.inheritance_type = this.getAttributeEnum(context, "inheritance_type", this.node);
            }
        }
        return this.inheritance_type;
    }

    protected Boolean is_abstract;
    public Boolean getIsAbstract(CallContext context) {
        if (this.is_abstract == null) {
            if (this.node != null) {

// DELETEME
//                 this.is_abstract = new Boolean(this.node.resolveXPath(context, "dia:attribute[@name='abstract']/dia:boolean/@val", namespaces).toString(context));

                this.is_abstract = new Boolean(this.getAttributeBoolean(context, "abstract", this.node));
            }
        }
        return this.is_abstract;
    }

    protected Boolean is_static;
    public Boolean getIsStatic(CallContext context) {
        if (this.is_static == null) {
            if (this.node != null) {

// DELETEME
//                 this.is_static = new Boolean(this.node.resolveXPath(context, "dia:attribute[@name='class_scope']/dia:boolean/@val", namespaces).toString(context));

                this.is_static = new Boolean(this.getAttributeBoolean(context, "class_scope", this.node));
            }
        }
        return this.is_static;
    }

    protected Boolean is_template;
    public Boolean getIsTemplate(CallContext context) {
        if (this.is_template == null) {
            if (this.node != null) {

// DELETEME
//                 this.is_template = new Boolean(this.node.resolveXPath(context, "dia:attribute[@name='template']/dia:boolean/@val", namespaces).toString(context));

                this.is_template = new Boolean(this.getAttributeBoolean(context, "template", this.node));
            }
        }
        return this.is_template;
    }

    protected Boolean is_navigable;
    public Boolean getIsNavigable(CallContext context) {
        if (this.is_navigable == null) {
            if (this.node != null) {

// DELETEME
//                 this.is_navigable = new Boolean(this.node.resolveXPath(context, "dia:attribute[@name='arrow']/dia:boolean/@val", namespaces).toString(context));

                this.is_navigable = new Boolean(this.getAttributeBoolean(context, "arrow", this.node));
            }
        }
        return this.is_navigable;
    }

    protected Boolean is_final;
    public Boolean getIsFinal(CallContext context) {
        if (this.is_final == null) {
            if (this.node != null) {
                this.is_final = new Boolean(this.getAttributeBoolean(context, "is_final", this.node));
            }
        }
        return this.is_final;
    }

    protected boolean is_terminal_state;
    public boolean getIsTerminalState(CallContext context) {
        return this.is_terminal_state;
    }
    public void setIsTerminalState(CallContext context, boolean is_terminal_state) {
        this.is_terminal_state = is_terminal_state;
    }

    protected boolean is_pseudo_state;
    public boolean getIsPseudoState(CallContext context) {
        return this.is_pseudo_state;
    }
    public void setIsPseudoState(CallContext context, boolean is_pseudo_state) {
        this.is_pseudo_state = is_pseudo_state;
    }

    protected String aggregation_kind;
      // 0 : none             None
      // 1 : hollow diamond   Shared
      // 2 : black diamond    Composite
    public String getAggregationKind(CallContext context) {
        if (this.aggregation_kind == null) {
            if (this.node != null) {
                String ak = this.getAttributeEnum(context, "aggregate", this.node);
                this.aggregation_kind =   (ak == null || ak.equals("0")) ? "None"
                                        :                ak.equals("1")  ? "Shared"
                                        :                ak.equals("2")  ? "Composite"
                                        :                                  "UNKNOWN";
            }
        }
        return this.aggregation_kind;
    }

    protected String base;
    public String getBase(CallContext context) {
        if (this.base == null) {
            if (this.node != null) {

// DELETEME
//                 this.base = this.node.resolveXPath(context, "dia:connections/dia:connection[@handle='0']/@to", namespaces).toString(context).replaceFirst("^#(.*)#$","$1");

                this.base = this.node.getChildElementsByFilters(context, connection_filter_1, connection_h0_filter).getAttribute(context, "to");
            }
        }
        return this.base;
    }

    protected String derived;
    public String getDerived(CallContext context) {
        if (this.derived == null) {
            if (this.node != null) {

// DELETEME
//                 this.derived = this.node.resolveXPath(context, "dia:connections/dia:connection[@handle='1']/@to", namespaces).toString(context).replaceFirst("^#(.*)#$","$1");

                this.derived = this.node.getChildElementsByFilters(context, connection_filter_1, connection_h1_filter).getAttribute(context, "to");
            }
        }
        return this.derived;
    }

    protected String source;
    public String getSource(CallContext context) {
        if (this.source == null) {
            if (this.node != null) {

// DELETEME
//                 this.source = this.node.resolveXPath(context, "dia:connections/dia:connection[@handle='0']/@to", namespaces).toString(context).replaceFirst("^#(.*)#$","$1");

                this.source = this.node.getChildElementsByFilters(context, connection_filter_1, connection_h0_filter).getAttribute(context, "to");
            }
        }
        return this.source;
    }

    protected String target;
    public String getTarget(CallContext context) {
        if (this.target == null) {
            if (this.node != null) {

// DELETEME
//                 this.target = this.node.resolveXPath(context, "dia:connections/dia:connection[@handle='1']/@to", namespaces).toString(context).replaceFirst("^#(.*)#$","$1");

                this.target = this.node.getChildElementsByFilters(context, connection_filter_1, connection_h1_filter).getAttribute(context, "to");
            }
        }
        return this.target;
    }

    protected Boolean is_imported;
    public Boolean isImported(CallContext context) {
        if (this.is_imported == null) {
            if (this.node != null) {

// DELETEME
//                 this.is_imported = this.node.resolveXPath(context, "dia:attribute[@name='stereotype']/dia:string", namespaces).toString(context).replaceFirst("(?s-m:^#(.*)#$)","$1").equals("Imported") ? true : false;

                this.is_imported = this.getAttributeString(context, "stereotype", this.node).equals("Imported") ? true : false;
            }
        }
        return this.is_imported;
    }

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
    // ONLY SINGLE INHERITANCE, WE HAVE TO REPEAT THIS HERE FROM MONITORABLE CLASS

    protected Vector<ProblemStatus> problem_status_details;

    public Vector<ProblemStatus> getProblemStatusDetails(CallContext context) {
        return this.problem_status_details;
    }

    public ProblemState getProblemState(CallContext context) {
        ProblemState problem_state = ProblemState.OK;
        if (this.problem_status_details != null) {
            for (ProblemStatus ps : this.problem_status_details) {
                problem_state = problem_state.combineWith(context, ps.getProblemState(context));
            }
        }
        return problem_state;
    }

    public void addProblemStatus(CallContext context, ProblemStatus problem_status) {
        if (this.problem_status_details == null) {
            this.problem_status_details = new Vector<ProblemStatus>();
        }
        this.problem_status_details.add(problem_status);
    }

    public void addProblemStatus(CallContext context, ProblemState problem_state, Problem problem) {
        this.addProblemStatus(context, new ProblemStatus(context, problem_state, problem));
    }

    public void addProblemStatus(CallContext context, ProblemState problem_state, String message) {
        this.addProblemStatus(context, new ProblemStatus(context, problem_state, message));
    }

    public void addProblemStatus(CallContext context, ProblemState problem_state, Throwable exception) {
        this.addProblemStatus(context, new ProblemStatus(context, problem_state, exception));
    }

    public void addProblemStatus(CallContext context, ProblemState problem_state, int return_code) {
        this.addProblemStatus(context, new ProblemStatus(context, problem_state, return_code));
    }
}
