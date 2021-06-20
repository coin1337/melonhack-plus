package org.reflections.scanners;

import java.util.Iterator;
import java.util.List;
import org.reflections.Store;

public class FieldAnnotationsScanner extends AbstractScanner {
   public void scan(Object var1, Store var2) {
      String var3 = this.getMetadataAdapter().getClassName(var1);
      List var4 = this.getMetadataAdapter().getFields(var1);
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         List var7 = this.getMetadataAdapter().getFieldAnnotationNames(var6);
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            String var9 = (String)var8.next();
            if (this.acceptResult(var9)) {
               String var10 = this.getMetadataAdapter().getFieldName(var6);
               this.put(var2, var9, String.format("%s.%s", var3, var10));
            }
         }
      }

   }
}
