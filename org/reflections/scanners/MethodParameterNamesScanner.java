package org.reflections.scanners;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.reflections.Store;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.util.Utils;

public class MethodParameterNamesScanner extends AbstractScanner {
   public void scan(Object var1, Store var2) {
      MetadataAdapter var3 = this.getMetadataAdapter();
      Iterator var4 = var3.getMethods(var1).iterator();

      while(true) {
         Object var5;
         String var6;
         LocalVariableAttribute var8;
         int var9;
         int var10;
         do {
            do {
               if (!var4.hasNext()) {
                  return;
               }

               var5 = var4.next();
               var6 = var3.getMethodFullKey(var1, var5);
            } while(!this.acceptResult(var6));

            CodeAttribute var7 = ((MethodInfo)var5).getCodeAttribute();
            var8 = var7 != null ? (LocalVariableAttribute)var7.getAttribute("LocalVariableTable") : null;
            var9 = var8 != null ? var8.tableLength() : 0;
            var10 = Modifier.isStatic(((MethodInfo)var5).getAccessFlags()) ? 0 : 1;
         } while(var10 >= var9);

         ArrayList var11 = new ArrayList(var9 - var10);

         while(var10 < var9) {
            var11.add(((MethodInfo)var5).getConstPool().getUtf8Info(var8.nameIndex(var10++)));
         }

         this.put(var2, var6, Utils.join(var11, ", "));
      }
   }
}
