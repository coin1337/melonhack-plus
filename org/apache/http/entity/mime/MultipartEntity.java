package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.content.ContentBody;

/** @deprecated */
@Deprecated
public class MultipartEntity implements HttpEntity {
   private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
   private final MultipartEntityBuilder builder;
   private volatile MultipartFormEntity entity;

   public MultipartEntity(HttpMultipartMode var1, String var2, Charset var3) {
      this.builder = (new MultipartEntityBuilder()).setMode(var1).setCharset(var3).setBoundary(var2);
      this.entity = null;
   }

   public MultipartEntity(HttpMultipartMode var1) {
      this(var1, (String)null, (Charset)null);
   }

   public MultipartEntity() {
      this(HttpMultipartMode.STRICT, (String)null, (Charset)null);
   }

   protected String generateContentType(String var1, Charset var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("multipart/form-data; boundary=");
      var3.append(var1);
      if (var2 != null) {
         var3.append("; charset=");
         var3.append(var2.name());
      }

      return var3.toString();
   }

   protected String generateBoundary() {
      StringBuilder var1 = new StringBuilder();
      Random var2 = new Random();
      int var3 = var2.nextInt(11) + 30;

      for(int var4 = 0; var4 < var3; ++var4) {
         var1.append(MULTIPART_CHARS[var2.nextInt(MULTIPART_CHARS.length)]);
      }

      return var1.toString();
   }

   private MultipartFormEntity getEntity() {
      if (this.entity == null) {
         this.entity = this.builder.buildEntity();
      }

      return this.entity;
   }

   public void addPart(FormBodyPart var1) {
      this.builder.addPart(var1);
      this.entity = null;
   }

   public void addPart(String var1, ContentBody var2) {
      this.addPart(new FormBodyPart(var1, var2));
   }

   public boolean isRepeatable() {
      return this.getEntity().isRepeatable();
   }

   public boolean isChunked() {
      return this.getEntity().isChunked();
   }

   public boolean isStreaming() {
      return this.getEntity().isStreaming();
   }

   public long getContentLength() {
      return this.getEntity().getContentLength();
   }

   public org.apache.http.Header getContentType() {
      return this.getEntity().getContentType();
   }

   public org.apache.http.Header getContentEncoding() {
      return this.getEntity().getContentEncoding();
   }

   public void consumeContent() throws IOException, UnsupportedOperationException {
      if (this.isStreaming()) {
         throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
      }
   }

   public InputStream getContent() throws IOException, UnsupportedOperationException {
      throw new UnsupportedOperationException("Multipart form entity does not implement #getContent()");
   }

   public void writeTo(OutputStream var1) throws IOException {
      this.getEntity().writeTo(var1);
   }
}
