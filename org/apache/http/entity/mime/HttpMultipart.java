package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** @deprecated */
@Deprecated
public class HttpMultipart extends AbstractMultipartForm {
   private final HttpMultipartMode mode;
   private final List<FormBodyPart> parts;

   public HttpMultipart(String var1, Charset var2, String var3, HttpMultipartMode var4) {
      super(var1, var2, var3);
      this.mode = var4;
      this.parts = new ArrayList();
   }

   public HttpMultipart(String var1, Charset var2, String var3) {
      this(var1, var2, var3, HttpMultipartMode.STRICT);
   }

   public HttpMultipart(String var1, String var2) {
      this(var1, (Charset)null, var2);
   }

   public HttpMultipartMode getMode() {
      return this.mode;
   }

   protected void formatMultipartHeader(FormBodyPart var1, OutputStream var2) throws IOException {
      Header var3 = var1.getHeader();
      switch(this.mode) {
      case BROWSER_COMPATIBLE:
         MinimalField var4 = var3.getField("Content-Disposition");
         writeField(var4, this.charset, var2);
         String var5 = var1.getBody().getFilename();
         if (var5 != null) {
            MinimalField var6 = var3.getField("Content-Type");
            writeField(var6, this.charset, var2);
         }
         break;
      default:
         Iterator var8 = var3.iterator();

         while(var8.hasNext()) {
            MinimalField var7 = (MinimalField)var8.next();
            writeField(var7, var2);
         }
      }

   }

   public List<FormBodyPart> getBodyParts() {
      return this.parts;
   }

   public void addBodyPart(FormBodyPart var1) {
      if (var1 != null) {
         this.parts.add(var1);
      }
   }
}
