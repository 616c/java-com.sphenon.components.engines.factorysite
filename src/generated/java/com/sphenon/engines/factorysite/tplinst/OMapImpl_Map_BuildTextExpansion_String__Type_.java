// instantiated with javainst.pl from /workspace/sphenon/projects/components/basics/many/v0001/origin/source/java/com/sphenon/basics/many/templates/OMapImpl.javatpl
// please do not modify this file directly
package com.sphenon.engines.factorysite.tplinst;

import com.sphenon.basics.many.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;
import com.sphenon.engines.factorysite.xml.*;
import com.sphenon.engines.factorysite.json.*;
import java.lang.reflect.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.metadata.tplinst.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.many.tplinst.*;

import com.sphenon.basics.context.*;
import com.sphenon.basics.exception.*;

import com.sphenon.basics.many.returncodes.*;

import java.util.Hashtable;

public class OMapImpl_Map_BuildTextExpansion_String__Type_
  implements OMap_Map_BuildTextExpansion_String__Type_
{
    protected java.util.Hashtable map;

    protected class Entry {
        private Map_BuildTextExpansion_String_                     actual_item;
        private Map_BuildTextExpansion_String_                     cached_item;
        private java.util.Vector             child_entries;
        private java.util.Vector             parent_entries;
        private OMapImpl_Map_BuildTextExpansion_String__Type_ omap;
        private Type                    index;
        private int                          path_length;
        private Type                    cached_index;

        public Entry (CallContext context, OMapImpl_Map_BuildTextExpansion_String__Type_ omap, Type index) {
            this.omap = omap;
            this.index = index;
            this.child_entries = new java.util.Vector();
            this.parent_entries = new java.util.Vector();
            this.actual_item = null;
            this.path_length = 9999;
            this.cached_item = null;
            this.cached_index = null;
            this.omap.map.put(index, this);

            Iterator_Type_ supertypes = index.getSuperTypes(context).getNavigator(context);
            Type supertype;
            while ((supertype = supertypes.tryGetCurrent(context)) != null) {
                Entry parent = this.omap._getEntry(context, supertype);
                this.parent_entries.add(parent);
                parent.addChildEntry(context, this);
                supertypes.next(context);
            }
            this.update(context);
        }

        public boolean isActualEntry(CallContext context) {
            return this.actual_item == null ? false : true;
        }

        public void setItem(CallContext context, Map_BuildTextExpansion_String_ item) {
            this.actual_item = item;
            this.cached_item = null;
            this.cached_index = null;
            if (item == null) {
                this.path_length = 9999;
                this.update(context);
            } else {
                this.path_length = 0;
                this.updateChildEntries(context);
            }
        }

        public Map_BuildTextExpansion_String_ getItem(CallContext context) {
            return this.actual_item != null ? this.actual_item : this.cached_item;
        }

        public void addChildEntry (CallContext context, Entry child_entry) {
            this.child_entries.add(child_entry);
        }

        public int getPathLength(CallContext context) {
            return this.path_length;
        }

        public Type getIndexType(CallContext context) {
            return this.actual_item != null ? this.index : this.cached_index;
        }

        public void update (CallContext context) {
            if (this.actual_item != null) {
                return;
            }
            boolean changed = false;
            for (int i=0; i < this.parent_entries.size(); i++) {
                Entry parent_entry = (Entry) this.parent_entries.elementAt(i);
                Map_BuildTextExpansion_String_ parent_item = parent_entry.getItem(context);
                if (parent_item != null) {
                    if (this.cached_item != parent_item && (this.cached_item == null || (    this.path_length > parent_entry.getPathLength(context) + 1)
                                                                                          && (this.cached_index == null || ! this.cached_index.isA(context, parent_entry.getIndexType(context)))
                                                                                        )
                                                                                     || (    this.cached_index != null && this.cached_index != parent_entry.getIndexType(context) && parent_entry.getIndexType(context).isA(context, this.cached_index))
                                                           ) {
                        this.cached_item  = parent_item;
                        this.path_length  = parent_entry.getPathLength(context) + 1;
                        this.cached_index = parent_entry.getIndexType(context);
                        changed = true;
                    }
                }
            }
            if (changed) {
                this.updateChildEntries(context);
            }
        }

        public void updateChildEntries (CallContext context) {
            for (int i=0; i < this.child_entries.size(); i++) {
                ((Entry) this.child_entries.elementAt(i)).update(context);
            }
        }
    }

    public OMapImpl_Map_BuildTextExpansion_String__Type_ (CallContext context)
    {
        map = new java.util.Hashtable ();
    }

    public OMapImpl_Map_BuildTextExpansion_String__Type_ (CallContext context, java.util.Hashtable map )
    {
        this.map = map;
    }

    private Entry _getEntry  (CallContext context, Type index)
    {
        Entry entry = (Entry) map.get(index);
        // Entry entry = (Entry) map.get(   index instanceof TypeParametrised ?
        //                                      ((TypeParametrised)index).getBaseType(context)
        //                                    : index
        //                              );
        if (entry == null) {
            entry = new Entry (context, this, index);
        }
        return entry;
    }

    public Map_BuildTextExpansion_String_ get     (CallContext context, Type index) throws DoesNotExist
    {
        Map_BuildTextExpansion_String_ item = this.tryGet(context, index);
        if (item == null) DoesNotExist.createAndThrow(context);
        return item;
    }

    public Map_BuildTextExpansion_String_ tryGet  (CallContext context, Type index)
    {
        return this._getEntry(context, index).getItem(context);
    }

    public boolean  canGet  (CallContext context, Type index)
    {
        if (this.tryGet(context, index) == null) return false;
        return true;
    }

    public void     set     (CallContext context, Type index, Map_BuildTextExpansion_String_ item)
    {
        Entry entry = this._getEntry(context, index);
        entry.setItem(context, item);
    }

    public void     add     (CallContext context, Type index, Map_BuildTextExpansion_String_ item) throws AlreadyExists
    {
        Entry entry = this._getEntry(context, index);
        if (entry.isActualEntry(context)) AlreadyExists.createAndThrow (context);
        entry.setItem(context, item);
    }

    public void     replace (CallContext context, Type index, Map_BuildTextExpansion_String_ item) throws DoesNotExist
    {
        Entry entry = this._getEntry(context, index);
        if (!entry.isActualEntry(context)) DoesNotExist.createAndThrow (context);
        entry.setItem(context, item);
    }

    public void     unset   (CallContext context, Type index)
    {
        Entry entry = this._getEntry(context, index);
        entry.setItem(context, null);
    }

    public void     remove  (CallContext context, Type index) throws DoesNotExist
    {
        Entry entry = this._getEntry(context, index);
        if (!entry.isActualEntry(context)) DoesNotExist.createAndThrow (context);
        entry.setItem(context, null);
    }

    public boolean canGetExactMatch (CallContext context, Type index) {
        return this._getEntry(context, index).isActualEntry(context);
    }
}
