package org.apache.http.entity.mime;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.Args;

public class MultipartEntityBuilder {
   private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
   private static final String DEFAULT_SUBTYPE = "form-data";
   private String subType = "form-data";
   private HttpMultipartMode mode;
   private String boundary;
   private Charset charset;
   private List<FormBodyPart> bodyParts;

   public static MultipartEntityBuilder create() {
      return new MultipartEntityBuilder();
   }

   MultipartEntityBuilder() {
      this.mode = HttpMultipartMode.STRICT;
      this.boundary = null;
      this.charset = null;
      this.bodyParts = null;
   }

   public MultipartEntityBuilder setMode(HttpMultipartMode var1) {
      this.mode = var1;
      return this;
   }

   public MultipartEntityBuilder setLaxMode() {
      this.mode = HttpMultipartMode.BROWSER_COMPATIBLE;
      return this;
   }

   public MultipartEntityBuilder setStrictMode() {
      this.mode = HttpMultipartMode.STRICT;
      return this;
   }

   public MultipartEntityBuilder setBoundary(String var1) {
      this.boundary = var1;
      return this;
   }

   public MultipartEntityBuilder setCharset(Charset var1) {
      this.charset = var1;
      return this;
   }

   MultipartEntityBuilder addPart(FormBodyPart var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.bodyParts == null) {
            this.bodyParts = new ArrayList();
         }

         this.bodyParts.add(var1);
         return this;
      }
   }

   public MultipartEntityBuilder addPart(String var1, ContentBody var2) {
      Args.notNull(var1, "Name");
      Args.notNull(var2, "Content body");
      return this.addPart(new FormBodyPart(var1, var2));
   }

   public MultipartEntityBuilder addTextBody(String var1, String var2, ContentType var3) {
      return this.addPart(var1, new StringBody(var2, var3));
   }

   public MultipartEntityBuilder addTextBody(String var1, String var2) {
      return this.addTextBody(var1, var2, ContentType.DEFAULT_TEXT);
   }

   public MultipartEntityBuilder addBinaryBody(String var1, byte[] var2, ContentType var3, String var4) {
      return this.addPart(var1, new ByteArrayBody(var2, var3, var4));
   }

   public MultipartEntityBuilder addBinaryBody(String var1, byte[] var2) {
      return this.addBinaryBody(var1, (byte[])var2, ContentType.DEFAULT_BINARY, (String)null);
   }

   public MultipartEntityBuilder addBinaryBody(String var1, File var2, ContentType var3, String var4) {
      return this.addPart(var1, new FileBody(var2, var3, var4));
   }

   public MultipartEntityBuilder addBinaryBody(String var1, File var2) {
      return this.addBinaryBody(var1, (File)var2, ContentType.DEFAULT_BINARY, (String)null);
   }

   public MultipartEntityBuilder addBinaryBody(String var1, InputStream var2, ContentType var3, String var4) {
      return this.addPart(var1, new InputStreamBody(var2, var3, var4));
   }

   public MultipartEntityBuilder addBinaryBody(String var1, InputStream var2) {
      return this.addBinaryBody(var1, (InputStream)var2, ContentType.DEFAULT_BINARY, (String)null);
   }

   private String generateContentType(String var1, Charset var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("multipart/form-data; boundary=");
      var3.append(var1);
      if (var2 != null) {
         var3.append("; charset=");
         var3.append(var2.name());
      }

      return var3.toString();
   }

   private String generateBoundary() {
      StringBuilder var1 = new StringBuilder();
      Random var2 = new Random();
      int var3 = var2.nextInt(11) + 30;

      for(int var4 = 0; var4 < var3; ++var4) {
         var1.append(MULTIPART_CHARS[var2.nextInt(MULTIPART_CHARS.length)]);
      }

      return var1.toString();
   }

   MultipartFormEntity buildEntity() {
      String var1 = this.subType != null ? this.subType : "form-data";
      Charset var2 = this.charset;
      String var3 = this.boundary != null ? this.boundary : this.generateBoundary();
      Object var4 = this.bodyParts != null ? new ArrayList(this.bodyParts) : Collections.emptyList();
      HttpMultipartMode var5 = this.mode != null ? this.mode : HttpMultipartMode.STRICT;
      Object var6;
      switch(var5) {
      case BROWSER_COMPATIBLE:
         var6 = new HttpBrowserCompatibleMultipart(var1, var2, var3, (List)var4);
         break;
      case RFC6532:
         var6 = new HttpRFC6532Multipart(var1, var2, var3, (List)var4);
         break;
      default:
         var6 = new HttpStrictMultipart(var1, var2, var3, (List)var4);
      }

      return new MultipartFormEntity((AbstractMultipartForm)var6, this.generateContentType(var3, var2), ((AbstractMultipartForm)var6).getTotalLength());
   }

   public HttpEntity build() {
      return this.buildEntity();
   }
}
