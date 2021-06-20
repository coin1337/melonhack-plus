package org.reflections.scanners;

import java.util.function.Predicate;
import org.reflections.Configuration;
import org.reflections.Store;
import org.reflections.vfs.Vfs;

public interface Scanner {
   void setConfiguration(Configuration var1);

   Scanner filterResultsBy(Predicate<String> var1);

   boolean acceptsInput(String var1);

   Object scan(Vfs.File var1, Object var2, Store var3);

   boolean acceptResult(String var1);
}
