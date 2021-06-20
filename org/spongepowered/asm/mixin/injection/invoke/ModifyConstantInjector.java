package org.spongepowered.asm.mixin.injection.invoke;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.invoke.util.InsnFinder;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.Locals;
import org.spongepowered.asm.util.SignaturePrinter;

public class ModifyConstantInjector extends RedirectInjector {
   private static final int OPCODE_OFFSET = 6;

   public ModifyConstantInjector(InjectionInfo var1) {
      super(var1, "@ModifyConstant");
   }

   protected void inject(Target var1, InjectionNodes.InjectionNode var2) {
      if (this.preInject(var2)) {
         if (var2.isReplaced()) {
            throw new UnsupportedOperationException("Target failure for " + this.info);
         } else {
            AbstractInsnNode var3 = var2.getCurrentTarget();
            if (var3 instanceof JumpInsnNode) {
               this.checkTargetModifiers(var1, false);
               this.injectExpandedConstantModifier(var1, (JumpInsnNode)var3);
            } else if (Bytecode.isConstant(var3)) {
               this.checkTargetModifiers(var1, false);
               this.injectConstantModifier(var1, var3);
            } else {
               throw new InvalidInjectionException(this.info, this.annotationType + " annotation is targetting an invalid insn in " + var1 + " in " + this);
            }
         }
      }
   }

   private void injectExpandedConstantModifier(Target var1, JumpInsnNode var2) {
      int var3 = var2.getOpcode();
      if (var3 >= 155 && var3 <= 158) {
         InsnList var4 = new InsnList();
         var4.add((AbstractInsnNode)(new InsnNode(3)));
         AbstractInsnNode var5 = this.invokeConstantHandler(Type.getType("I"), var1, var4, var4);
         var4.add((AbstractInsnNode)(new JumpInsnNode(var3 + 6, var2.label)));
         var1.replaceNode(var2, var5, var4);
         var1.addToStack(1);
      } else {
         throw new InvalidInjectionException(this.info, this.annotationType + " annotation selected an invalid opcode " + Bytecode.getOpcodeName(var3) + " in " + var1 + " in " + this);
      }
   }

   private void injectConstantModifier(Target var1, AbstractInsnNode var2) {
      Type var3 = Bytecode.getConstantType(var2);
      if (var3.getSort() <= 5 && this.info.getContext().getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) {
         this.checkNarrowing(var1, var2, var3);
      }

      InsnList var4 = new InsnList();
      InsnList var5 = new InsnList();
      AbstractInsnNode var6 = this.invokeConstantHandler(var3, var1, var4, var5);
      var1.wrapNode(var2, var6, var4, var5);
   }

   private AbstractInsnNode invokeConstantHandler(Type var1, Target var2, InsnList var3, InsnList var4) {
      String var5 = Bytecode.generateDescriptor(var1, var1);
      boolean var6 = this.checkDescriptor(var5, var2, "getter");
      if (!this.isStatic) {
         var3.insert((AbstractInsnNode)(new VarInsnNode(25, 0)));
         var2.addToStack(1);
      }

      if (var6) {
         this.pushArgs(var2.arguments, var4, var2.getArgIndices(), 0, var2.arguments.length);
         var2.addToStack(Bytecode.getArgsSize(var2.arguments));
      }

      return this.invokeHandler(var4);
   }

   private void checkNarrowing(Target var1, AbstractInsnNode var2, Type var3) {
      AbstractInsnNode var4 = (new InsnFinder()).findPopInsn(var1, var2);
      if (var4 != null) {
         if (var4 instanceof FieldInsnNode) {
            FieldInsnNode var5 = (FieldInsnNode)var4;
            Type var6 = Type.getType(var5.desc);
            this.checkNarrowing(var1, var2, var3, var6, var1.indexOf(var4), String.format("%s %s %s.%s", Bytecode.getOpcodeName(var4), SignaturePrinter.getTypeName(var6, false), var5.owner.replace('/', '.'), var5.name));
         } else if (var4.getOpcode() == 172) {
            this.checkNarrowing(var1, var2, var3, var1.returnType, var1.indexOf(var4), "RETURN " + SignaturePrinter.getTypeName(var1.returnType, false));
         } else if (var4.getOpcode() == 54) {
            int var9 = ((VarInsnNode)var4).var;
            LocalVariableNode var10 = Locals.getLocalVariableAt(var1.classNode, var1.method, var4, var9);
            if (var10 != null && var10.desc != null) {
               String var7 = var10.name != null ? var10.name : "unnamed";
               Type var8 = Type.getType(var10.desc);
               this.checkNarrowing(var1, var2, var3, var8, var1.indexOf(var4), String.format("ISTORE[var=%d] %s %s", var9, SignaturePrinter.getTypeName(var8, false), var7));
            }
         }

      }
   }

   private void checkNarrowing(Target var1, AbstractInsnNode var2, Type var3, Type var4, int var5, String var6) {
      int var7 = var3.getSort();
      int var8 = var4.getSort();
      if (var8 < var7) {
         String var9 = SignaturePrinter.getTypeName(var3, false);
         String var10 = SignaturePrinter.getTypeName(var4, false);
         String var11 = var8 == 1 ? ". Implicit conversion to <boolean> can cause nondeterministic (JVM-specific) behaviour!" : "";
         Level var12 = var8 == 1 ? Level.ERROR : Level.WARN;
         Injector.logger.log(var12, "Narrowing conversion of <{}> to <{}> in {} target {} at opcode {} ({}){}", new Object[]{var9, var10, this.info, var1, var5, var6, var11});
      }

   }
}
