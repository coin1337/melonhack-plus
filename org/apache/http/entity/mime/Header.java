package org.apache.http.entity.mime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Header implements Iterable<MinimalField> {
   private final List<MinimalField> fields = new LinkedList();
   private final Map<String, List<MinimalField>> fieldMap = new HashMap();

   public void addField(MinimalField var1) {
      if (var1 != null) {
         String var2 = var1.getName().toLowerCase(Locale.US);
         Object var3 = (List)this.fieldMap.get(var2);
         if (var3 == null) {
            var3 = new LinkedList();
            this.fieldMap.put(var2, var3);
         }

         ((List)var3).add(var1);
         this.fields.add(var1);
      }
   }

   public List<MinimalField> getFields() {
      return new ArrayList(this.fields);
   }

   public MinimalField getField(String var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.toLowerCase(Locale.US);
         List var3 = (List)this.fieldMap.get(var2);
         return var3 != null && !var3.isEmpty() ? (MinimalField)var3.get(0) : null;
      }
   }

   public List<MinimalField> getFields(String var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.toLowerCase(Locale.US);
         List var3 = (List)this.fieldMap.get(var2);
         return (List)(var3 != null && !var3.isEmpty() ? new ArrayList(var3) : Collections.emptyList());
      }
   }

   public int removeFields(String var1) {
      if (var1 == null) {
         return 0;
      } else {
         String var2 = var1.toLowerCase(Locale.US);
         List var3 = (List)this.fieldMap.remove(var2);
         if (var3 != null && !var3.isEmpty()) {
            this.fields.removeAll(var3);
            return var3.size();
         } else {
            return 0;
         }
      }
   }

   public void setField(MinimalField var1) {
      if (var1 != null) {
         String var2 = var1.getName().toLowerCase(Locale.US);
         List var3 = (List)this.fieldMap.get(var2);
         if (var3 != null && !var3.isEmpty()) {
            var3.clear();
            var3.add(var1);
            int var4 = -1;
            int var5 = 0;

            for(Iterator var6 = this.fields.iterator(); var6.hasNext(); ++var5) {
               MinimalField var7 = (MinimalField)var6.next();
               if (var7.getName().equalsIgnoreCase(var1.getName())) {
                  var6.remove();
                  if (var4 == -1) {
                     var4 = var5;
                  }
               }
            }

            this.fields.add(var4, var1);
         } else {
            this.addField(var1);
         }
      }
   }

   public Iterator<MinimalField> iterator() {
      return Collections.unmodifiableList(this.fields).iterator();
   }

   public String toString() {
      return this.fields.toString();
   }
}
