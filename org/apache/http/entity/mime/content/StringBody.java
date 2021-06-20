package org.apache.http.entity.mime.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;

public class StringBody extends AbstractContentBody {
   private final byte[] content;

   /** @deprecated */
   @Deprecated
   public static StringBody create(String var0, String var1, Charset var2) throws IllegalArgumentException {
      try {
         return new StringBody(var0, var1, var2);
      } catch (UnsupportedEncodingException var4) {
         throw new IllegalArgumentException("Charset " + var2 + " is not supported", var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public static StringBody create(String var0, Charset var1) throws IllegalArgumentException {
      return create(var0, (String)null, var1);
   }

   /** @deprecated */
   @Deprecated
   public static StringBody create(String var0) throws IllegalArgumentException {
      return create(var0, (String)null, (Charset)null);
   }

   /** @deprecated */
   @Deprecated
   public StringBody(String var1, String var2, Charset var3) throws UnsupportedEncodingException {
      this(var1, ContentType.create(var2, var3));
   }

   /** @deprecated */
   @Deprecated
   public StringBody(String var1, Charset var2) throws UnsupportedEncodingException {
      this(var1, "text/plain", var2);
   }

   /** @deprecated */
   @Deprecated
   public StringBody(String var1) throws UnsupportedEncodingException {
      this(var1, "text/plain", Consts.ASCII);
   }

   public StringBody(String var1, ContentType var2) {
      super(var2);
      Charset var3 = var2.getCharset();
      String var4 = var3 != null ? var3.name() : Consts.ASCII.name();

      try {
         this.content = var1.getBytes(var4);
      } catch (UnsupportedEncodingException var6) {
         throw new UnsupportedCharsetException(var4);
      }
   }

   public Reader getReader() {
      Charset var1 = this.getContentType().getCharset();
      return new InputStreamReader(new ByteArrayInputStream(this.content), var1 != null ? var1 : Consts.ASCII);
   }

   public void writeTo(OutputStream var1) throws IOException {
      Args.notNull(var1, "Output stream");
      ByteArrayInputStream var2 = new ByteArrayInputStream(this.content);
      byte[] var3 = new byte[4096];

      int var4;
      while((var4 = var2.read(var3)) != -1) {
         var1.write(var3, 0, var4);
      }

      var1.flush();
   }

   public String getTransferEncoding() {
      return "8bit";
   }

   public long getContentLength() {
      return (long)this.content.length;
   }

   public String getFilename() {
      return null;
   }
}
