package org.reflections.scanners;

import java.util.Iterator;
import java.util.List;
import org.reflections.Store;
import org.reflections.adapters.MetadataAdapter;

public class MethodParameterScanner extends AbstractScanner {
   public void scan(Object var1, Store var2) {
      MetadataAdapter var3 = this.getMetadataAdapter();
      Iterator var4 = var3.getMethods(var1).iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         String var6 = var3.getParameterNames(var5).toString();
         if (this.acceptResult(var6)) {
            this.put(var2, var6, var3.getMethodFullKey(var1, var5));
         }

         String var7 = var3.getReturnTypeName(var5);
         if (this.acceptResult(var7)) {
            this.put(var2, var7, var3.getMethodFullKey(var1, var5));
         }

         List var8 = var3.getParameterNames(var5);

         for(int var9 = 0; var9 < var8.size(); ++var9) {
            Iterator var10 = var3.getParameterAnnotationNames(var5, var9).iterator();

            while(var10.hasNext()) {
               Object var11 = var10.next();
               if (this.acceptResult((String)var11)) {
                  this.put(var2, (String)var11, var3.getMethodFullKey(var1, var5));
               }
            }
         }
      }

   }
}
