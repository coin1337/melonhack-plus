package org.reflections.scanners;

import org.reflections.Store;
import org.reflections.vfs.Vfs;

public class ResourcesScanner extends AbstractScanner {
   public boolean acceptsInput(String var1) {
      return !var1.endsWith(".class");
   }

   public Object scan(Vfs.File var1, Object var2, Store var3) {
      this.put(var3, var1.getName(), var1.getRelativePath());
      return var2;
   }

   public void scan(Object var1, Store var2) {
      throw new UnsupportedOperationException();
   }
}
