package org.spongepowered.tools.obfuscation.mapping.mcp;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import org.spongepowered.asm.mixin.throwables.MixinException;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.obfuscation.mapping.mcp.MappingFieldSrg;
import org.spongepowered.tools.obfuscation.mapping.common.MappingProvider;

public class MappingProviderSrg extends MappingProvider {
   public MappingProviderSrg(Messager var1, Filer var2) {
      super(var1, var2);
   }

   public void read(final File var1) throws IOException {
      final BiMap var2 = this.packageMap;
      final BiMap var3 = this.classMap;
      final BiMap var4 = this.fieldMap;
      final BiMap var5 = this.methodMap;
      Files.readLines(var1, Charset.defaultCharset(), new LineProcessor<String>() {
         public String getResult() {
            return null;
         }

         public boolean processLine(String var1x) throws IOException {
            if (!Strings.isNullOrEmpty(var1x) && !var1x.startsWith("#")) {
               String var2x = var1x.substring(0, 2);
               String[] var3x = var1x.substring(4).split(" ");
               if (var2x.equals("PK")) {
                  var2.forcePut(var3x[0], var3x[1]);
               } else if (var2x.equals("CL")) {
                  var3.forcePut(var3x[0], var3x[1]);
               } else if (var2x.equals("FD")) {
                  var4.forcePut((new MappingFieldSrg(var3x[0])).copy(), (new MappingFieldSrg(var3x[1])).copy());
               } else {
                  if (!var2x.equals("MD")) {
                     throw new MixinException("Invalid SRG file: " + var1);
                  }

                  var5.forcePut(new MappingMethod(var3x[0], var3x[1]), new MappingMethod(var3x[2], var3x[3]));
               }

               return true;
            } else {
               return true;
            }
         }
      });
   }

   public MappingField getFieldMapping(MappingField var1) {
      if (((MappingField)var1).getDesc() != null) {
         var1 = new MappingFieldSrg((MappingField)var1);
      }

      return (MappingField)this.fieldMap.get(var1);
   }
}
