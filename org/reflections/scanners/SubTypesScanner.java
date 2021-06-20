package org.reflections.scanners;

import java.util.Iterator;
import org.reflections.Store;
import org.reflections.util.FilterBuilder;

public class SubTypesScanner extends AbstractScanner {
   public SubTypesScanner() {
      this(true);
   }

   public SubTypesScanner(boolean var1) {
      if (var1) {
         this.filterResultsBy((new FilterBuilder()).exclude(Object.class.getName()));
      }

   }

   public void scan(Object var1, Store var2) {
      String var3 = this.getMetadataAdapter().getClassName(var1);
      String var4 = this.getMetadataAdapter().getSuperclassName(var1);
      if (this.acceptResult(var4)) {
         this.put(var2, var4, var3);
      }

      Iterator var5 = this.getMetadataAdapter().getInterfacesNames(var1).iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (this.acceptResult(var6)) {
            this.put(var2, var6, var3);
         }
      }

   }
}
