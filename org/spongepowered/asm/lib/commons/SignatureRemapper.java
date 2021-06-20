package org.spongepowered.asm.lib.commons;

import java.util.Stack;
import org.spongepowered.asm.lib.signature.SignatureVisitor;

public class SignatureRemapper extends SignatureVisitor {
   private final SignatureVisitor v;
   private final Remapper remapper;
   private Stack<String> classNames;

   public SignatureRemapper(SignatureVisitor var1, Remapper var2) {
      this(327680, var1, var2);
   }

   protected SignatureRemapper(int var1, SignatureVisitor var2, Remapper var3) {
      super(var1);
      this.classNames = new Stack();
      this.v = var2;
      this.remapper = var3;
   }

   public void visitClassType(String var1) {
      this.classNames.push(var1);
      this.v.visitClassType(this.remapper.mapType(var1));
   }

   public void visitInnerClassType(String var1) {
      String var2 = (String)this.classNames.pop();
      String var3 = var2 + '$' + var1;
      this.classNames.push(var3);
      String var4 = this.remapper.mapType(var2) + '$';
      String var5 = this.remapper.mapType(var3);
      int var6 = var5.startsWith(var4) ? var4.length() : var5.lastIndexOf(36) + 1;
      this.v.visitInnerClassType(var5.substring(var6));
   }

   public void visitFormalTypeParameter(String var1) {
      this.v.visitFormalTypeParameter(var1);
   }

   public void visitTypeVariable(String var1) {
      this.v.visitTypeVariable(var1);
   }

   public SignatureVisitor visitArrayType() {
      this.v.visitArrayType();
      return this;
   }

   public void visitBaseType(char var1) {
      this.v.visitBaseType(var1);
   }

   public SignatureVisitor visitClassBound() {
      this.v.visitClassBound();
      return this;
   }

   public SignatureVisitor visitExceptionType() {
      this.v.visitExceptionType();
      return this;
   }

   public SignatureVisitor visitInterface() {
      this.v.visitInterface();
      return this;
   }

   public SignatureVisitor visitInterfaceBound() {
      this.v.visitInterfaceBound();
      return this;
   }

   public SignatureVisitor visitParameterType() {
      this.v.visitParameterType();
      return this;
   }

   public SignatureVisitor visitReturnType() {
      this.v.visitReturnType();
      return this;
   }

   public SignatureVisitor visitSuperclass() {
      this.v.visitSuperclass();
      return this;
   }

   public void visitTypeArgument() {
      this.v.visitTypeArgument();
   }

   public SignatureVisitor visitTypeArgument(char var1) {
      this.v.visitTypeArgument(var1);
      return this;
   }

   public void visitEnd() {
      this.v.visitEnd();
      this.classNames.pop();
   }
}
