package org.reflections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.reflections.util.Utils;

public class Store {
   private final ConcurrentHashMap<String, Map<String, Collection<String>>> storeMap = new ConcurrentHashMap();

   protected Store() {
   }

   public Set<String> keySet() {
      return this.storeMap.keySet();
   }

   private Map<String, Collection<String>> get(String var1) {
      Map var2 = (Map)this.storeMap.get(var1);
      if (var2 == null) {
         throw new ReflectionsException("Scanner " + var1 + " was not configured");
      } else {
         return var2;
      }
   }

   public Set<String> get(Class<?> var1, String var2) {
      return this.get((String)Utils.index(var1), (Collection)Collections.singletonList(var2));
   }

   public Set<String> get(String var1, String var2) {
      return this.get((String)var1, (Collection)Collections.singletonList(var2));
   }

   public Set<String> get(Class<?> var1, Collection<String> var2) {
      return this.get(Utils.index(var1), var2);
   }

   private Set<String> get(String var1, Collection<String> var2) {
      Map var3 = this.get(var1);
      LinkedHashSet var4 = new LinkedHashSet();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         Collection var7 = (Collection)var3.get(var6);
         if (var7 != null) {
            var4.addAll(var7);
         }
      }

      return var4;
   }

   public Set<String> getAllIncluding(Class<?> var1, Collection<String> var2) {
      String var3 = Utils.index(var1);
      Map var4 = this.get(var3);
      ArrayList var5 = new ArrayList(var2);
      HashSet var6 = new HashSet();

      for(int var7 = 0; var7 < var5.size(); ++var7) {
         String var8 = (String)var5.get(var7);
         if (var6.add(var8)) {
            Collection var9 = (Collection)var4.get(var8);
            if (var9 != null) {
               var5.addAll(var9);
            }
         }
      }

      return var6;
   }

   public Set<String> getAll(Class<?> var1, String var2) {
      return this.getAllIncluding(var1, this.get(var1, var2));
   }

   public Set<String> getAll(Class<?> var1, Collection<String> var2) {
      return this.getAllIncluding(var1, this.get(var1, var2));
   }

   public Set<String> keys(String var1) {
      Map var2 = (Map)this.storeMap.get(var1);
      return (Set)(var2 != null ? new HashSet(var2.keySet()) : Collections.emptySet());
   }

   public Set<String> values(String var1) {
      Map var2 = (Map)this.storeMap.get(var1);
      return var2 != null ? (Set)var2.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()) : Collections.emptySet();
   }

   public boolean put(Class<?> var1, String var2, String var3) {
      return this.put(Utils.index(var1), var2, var3);
   }

   public boolean put(String var1, String var2, String var3) {
      return ((Collection)((Map)this.storeMap.computeIfAbsent(var1, (var0) -> {
         return new ConcurrentHashMap();
      })).computeIfAbsent(var2, (var0) -> {
         return new ArrayList();
      })).add(var3);
   }

   void merge(Store var1) {
      if (var1 != null) {
         Iterator var2 = var1.keySet().iterator();

         while(true) {
            String var3;
            Map var4;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               var3 = (String)var2.next();
               var4 = var1.get(var3);
            } while(var4 == null);

            Iterator var5 = var4.keySet().iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               Iterator var7 = ((Collection)var4.get(var6)).iterator();

               while(var7.hasNext()) {
                  String var8 = (String)var7.next();
                  this.put(var3, var6, var8);
               }
            }
         }
      }
   }
}
