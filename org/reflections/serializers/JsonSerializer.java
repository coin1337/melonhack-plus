package org.reflections.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.reflections.Reflections;
import org.reflections.util.Utils;

public class JsonSerializer implements Serializer {
   private Gson gson;

   public Reflections read(InputStream var1) {
      return (Reflections)this.getGson().fromJson(new InputStreamReader(var1), Reflections.class);
   }

   public File save(Reflections var1, String var2) {
      try {
         File var3 = Utils.prepareFile(var2);
         Files.write(var3.toPath(), this.toString(var1).getBytes(Charset.defaultCharset()), new OpenOption[0]);
         return var3;
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   public String toString(Reflections var1) {
      return this.getGson().toJson(var1);
   }

   private Gson getGson() {
      if (this.gson == null) {
         this.gson = (new GsonBuilder()).setPrettyPrinting().create();
      }

      return this.gson;
   }
}
