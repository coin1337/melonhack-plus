package org.reflections.scanners;

import java.lang.annotation.Inherited;
import java.util.Iterator;
import org.reflections.Store;

public class TypeAnnotationsScanner extends AbstractScanner {
   public void scan(Object var1, Store var2) {
      String var3 = this.getMetadataAdapter().getClassName(var1);
      Iterator var4 = this.getMetadataAdapter().getClassAnnotationNames(var1).iterator();

      while(true) {
         String var5;
         do {
            if (!var4.hasNext()) {
               return;
            }

            var5 = (String)var4.next();
         } while(!this.acceptResult(var5) && !var5.equals(Inherited.class.getName()));

         this.put(var2, var5, var3);
      }
   }
}
