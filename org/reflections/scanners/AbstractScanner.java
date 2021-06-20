package org.reflections.scanners;

import java.util.function.Predicate;
import org.reflections.Configuration;
import org.reflections.ReflectionsException;
import org.reflections.Store;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.util.Utils;
import org.reflections.vfs.Vfs;

public abstract class AbstractScanner implements Scanner {
   private Configuration configuration;
   private Predicate<String> resultFilter = (var0) -> {
      return true;
   };

   public boolean acceptsInput(String var1) {
      return this.getMetadataAdapter().acceptsInput(var1);
   }

   public Object scan(Vfs.File var1, Object var2, Store var3) {
      if (var2 == null) {
         try {
            var2 = this.configuration.getMetadataAdapter().getOrCreateClassObject(var1);
         } catch (Exception var5) {
            throw new ReflectionsException("could not create class object from file " + var1.getRelativePath(), var5);
         }
      }

      this.scan(var2, var3);
      return var2;
   }

   public abstract void scan(Object var1, Store var2);

   protected void put(Store var1, String var2, String var3) {
      var1.put(Utils.index(this.getClass()), var2, var3);
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public void setConfiguration(Configuration var1) {
      this.configuration = var1;
   }

   public Predicate<String> getResultFilter() {
      return this.resultFilter;
   }

   public void setResultFilter(Predicate<String> var1) {
      this.resultFilter = var1;
   }

   public Scanner filterResultsBy(Predicate<String> var1) {
      this.setResultFilter(var1);
      return this;
   }

   public boolean acceptResult(String var1) {
      return var1 != null && this.resultFilter.test(var1);
   }

   protected MetadataAdapter getMetadataAdapter() {
      return this.configuration.getMetadataAdapter();
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 != null && this.getClass() == var1.getClass();
   }

   public int hashCode() {
      return this.getClass().hashCode();
   }
}
