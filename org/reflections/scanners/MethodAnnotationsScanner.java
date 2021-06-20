package org.reflections.scanners;

import java.util.Iterator;
import org.reflections.Store;

public class MethodAnnotationsScanner extends AbstractScanner {
   public void scan(Object var1, Store var2) {
      Iterator var3 = this.getMetadataAdapter().getMethods(var1).iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         Iterator var5 = this.getMetadataAdapter().getMethodAnnotationNames(var4).iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            if (this.acceptResult(var6)) {
               this.put(var2, var6, this.getMetadataAdapter().getMethodFullKey(var1, var4));
            }
         }
      }

   }
}
