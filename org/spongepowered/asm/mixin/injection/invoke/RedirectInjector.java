package org.spongepowered.asm.mixin.injection.invoke;

import com.google.common.base.Joiner;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess;
import org.spongepowered.asm.mixin.injection.points.BeforeNew;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;

public class RedirectInjector extends InvokeInjector {
   private static final String KEY_NOMINATORS = "nominators";
   private static final String KEY_FUZZ = "fuzz";
   private static final String KEY_OPCODE = "opcode";
   protected RedirectInjector.Meta meta;
   private Map<BeforeNew, RedirectInjector.ConstructorRedirectData> ctorRedirectors;

   public RedirectInjector(InjectionInfo var1) {
      this(var1, "@Redirect");
   }

   protected RedirectInjector(InjectionInfo var1, String var2) {
      super(var1, var2);
      this.ctorRedirectors = new HashMap();
      int var3 = var1.getContext().getPriority();
      boolean var4 = Annotations.getVisible(this.methodNode, Final.class) != null;
      this.meta = new RedirectInjector.Meta(var3, var4, this.info.toString(), this.methodNode.desc);
   }

   protected void checkTarget(Target var1) {
   }

   protected void addTargetNode(Target var1, List<InjectionNodes.InjectionNode> var2, AbstractInsnNode var3, Set<InjectionPoint> var4) {
      InjectionNodes.InjectionNode var5 = var1.getInjectionNode(var3);
      RedirectInjector.ConstructorRedirectData var6 = null;
      int var7 = 8;
      int var8 = 0;
      if (var5 != null) {
         RedirectInjector.Meta var9 = (RedirectInjector.Meta)var5.getDecoration("redirector");
         if (var9 != null && var9.getOwner() != this) {
            if (var9.priority >= this.meta.priority) {
               Injector.logger.warn("{} conflict. Skipping {} with priority {}, already redirected by {} with priority {}", new Object[]{this.annotationType, this.info, this.meta.priority, var9.name, var9.priority});
               return;
            }

            if (var9.isFinal) {
               throw new InvalidInjectionException(this.info, String.format("%s conflict: %s failed because target was already remapped by %s", this.annotationType, this, var9.name));
            }
         }
      }

      Iterator var12 = var4.iterator();

      while(var12.hasNext()) {
         InjectionPoint var10 = (InjectionPoint)var12.next();
         if (var10 instanceof BeforeNew) {
            var6 = this.getCtorRedirect((BeforeNew)var10);
            var6.wildcard = !((BeforeNew)var10).hasDescriptor();
         } else if (var10 instanceof BeforeFieldAccess) {
            BeforeFieldAccess var11 = (BeforeFieldAccess)var10;
            var7 = var11.getFuzzFactor();
            var8 = var11.getArrayOpcode();
         }
      }

      InjectionNodes.InjectionNode var13 = var1.addInjectionNode(var3);
      var13.decorate("redirector", this.meta);
      var13.decorate("nominators", var4);
      if (var3 instanceof TypeInsnNode && var3.getOpcode() == 187) {
         var13.decorate("ctor", var6);
      } else {
         var13.decorate("fuzz", var7);
         var13.decorate("opcode", var8);
      }

      var2.add(var13);
   }

   private RedirectInjector.ConstructorRedirectData getCtorRedirect(BeforeNew var1) {
      RedirectInjector.ConstructorRedirectData var2 = (RedirectInjector.ConstructorRedirectData)this.ctorRedirectors.get(var1);
      if (var2 == null) {
         var2 = new RedirectInjector.ConstructorRedirectData();
         this.ctorRedirectors.put(var1, var2);
      }

      return var2;
   }

   protected void inject(Target var1, InjectionNodes.InjectionNode var2) {
      if (this.preInject(var2)) {
         if (var2.isReplaced()) {
            throw new UnsupportedOperationException("Redirector target failure for " + this.info);
         } else if (var2.getCurrentTarget() instanceof MethodInsnNode) {
            this.checkTargetForNode(var1, var2);
            this.injectAtInvoke(var1, var2);
         } else if (var2.getCurrentTarget() instanceof FieldInsnNode) {
            this.checkTargetForNode(var1, var2);
            this.injectAtFieldAccess(var1, var2);
         } else if (var2.getCurrentTarget() instanceof TypeInsnNode && var2.getCurrentTarget().getOpcode() == 187) {
            if (!this.isStatic && var1.isStatic) {
               throw new InvalidInjectionException(this.info, String.format("non-static callback method %s has a static target which is not supported", this));
            } else {
               this.injectAtConstructor(var1, var2);
            }
         } else {
            throw new InvalidInjectionException(this.info, String.format("%s annotation on is targetting an invalid insn in %s in %s", this.annotationType, var1, this));
         }
      }
   }

   protected boolean preInject(InjectionNodes.InjectionNode var1) {
      RedirectInjector.Meta var2 = (RedirectInjector.Meta)var1.getDecoration("redirector");
      if (var2.getOwner() != this) {
         Injector.logger.warn("{} conflict. Skipping {} with priority {}, already redirected by {} with priority {}", new Object[]{this.annotationType, this.info, this.meta.priority, var2.name, var2.priority});
         return false;
      } else {
         return true;
      }
   }

   protected void postInject(Target var1, InjectionNodes.InjectionNode var2) {
      super.postInject(var1, var2);
      if (var2.getOriginalTarget() instanceof TypeInsnNode && var2.getOriginalTarget().getOpcode() == 187) {
         RedirectInjector.ConstructorRedirectData var3 = (RedirectInjector.ConstructorRedirectData)var2.getDecoration("ctor");
         if (var3.wildcard && var3.injected == 0) {
            throw new InvalidInjectionException(this.info, String.format("%s ctor invocation was not found in %s", this.annotationType, var1));
         }
      }

   }

   protected void injectAtInvoke(Target var1, InjectionNodes.InjectionNode var2) {
      RedirectInjector.RedirectedInvoke var3 = new RedirectInjector.RedirectedInvoke(var1, (MethodInsnNode)var2.getCurrentTarget());
      this.validateParams(var3);
      InsnList var4 = new InsnList();
      int var5 = Bytecode.getArgsSize(var3.locals) + 1;
      int var6 = 1;
      int[] var7 = this.storeArgs(var1, var3.locals, var4, 0);
      if (var3.captureTargetArgs) {
         int var8 = Bytecode.getArgsSize(var1.arguments);
         var5 += var8;
         var6 += var8;
         var7 = Ints.concat(new int[][]{var7, var1.getArgIndices()});
      }

      AbstractInsnNode var9 = this.invokeHandlerWithArgs(this.methodArgs, var4, var7);
      var1.replaceNode(var3.node, var9, var4);
      var1.addToLocals(var5);
      var1.addToStack(var6);
   }

   protected void validateParams(RedirectInjector.RedirectedInvoke var1) {
      int var2 = this.methodArgs.length;
      String var3 = String.format("%s handler method %s", this.annotationType, this);
      if (!var1.returnType.equals(this.returnType)) {
         throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Expected return type %s found %s", var3, this.returnType, var1.returnType));
      } else {
         for(int var4 = 0; var4 < var2; ++var4) {
            Type var5 = null;
            if (var4 >= this.methodArgs.length) {
               throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Not enough arguments found for capture of target method args, expected %d but found %d", var3, var2, this.methodArgs.length));
            }

            Type var6 = this.methodArgs[var4];
            if (var4 < var1.locals.length) {
               var5 = var1.locals[var4];
            } else {
               var1.captureTargetArgs = true;
               var2 = Math.max(var2, var1.locals.length + var1.target.arguments.length);
               int var7 = var4 - var1.locals.length;
               if (var7 >= var1.target.arguments.length) {
                  throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Found unexpected additional target argument with type %s at index %d", var3, var6, var4));
               }

               var5 = var1.target.arguments[var7];
            }

            AnnotationNode var9 = Annotations.getInvisibleParameter(this.methodNode, Coerce.class, var4);
            if (var6.equals(var5)) {
               if (var9 != null && this.info.getContext().getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) {
                  Injector.logger.warn("Redundant @Coerce on {} argument {}, {} is identical to {}", new Object[]{var3, var4, var5, var6});
               }
            } else {
               boolean var8 = Injector.canCoerce(var6, var5);
               if (var9 == null) {
                  throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Found unexpected argument type %s at index %d, expected %s", var3, var6, var4, var5));
               }

               if (!var8) {
                  throw new InvalidInjectionException(this.info, String.format("%s has an invalid signature. Cannot @Coerce argument type %s at index %d to %s", var3, var5, var4, var6));
               }
            }
         }

      }
   }

   private void injectAtFieldAccess(Target var1, InjectionNodes.InjectionNode var2) {
      FieldInsnNode var3 = (FieldInsnNode)var2.getCurrentTarget();
      int var4 = var3.getOpcode();
      Type var5 = Type.getType("L" + var3.owner + ";");
      Type var6 = Type.getType(var3.desc);
      int var7 = var6.getSort() == 9 ? var6.getDimensions() : 0;
      int var8 = this.returnType.getSort() == 9 ? this.returnType.getDimensions() : 0;
      if (var8 > var7) {
         throw new InvalidInjectionException(this.info, "Dimensionality of handler method is greater than target array on " + this);
      } else {
         if (var8 == 0 && var7 > 0) {
            int var9 = (Integer)var2.getDecoration("fuzz");
            int var10 = (Integer)var2.getDecoration("opcode");
            this.injectAtArrayField(var1, var3, var4, var5, var6, var9, var10);
         } else {
            this.injectAtScalarField(var1, var3, var4, var5, var6);
         }

      }
   }

   private void injectAtArrayField(Target var1, FieldInsnNode var2, int var3, Type var4, Type var5, int var6, int var7) {
      Type var8 = var5.getElementType();
      if (var3 != 178 && var3 != 180) {
         throw new InvalidInjectionException(this.info, String.format("Unspported opcode %s for array access %s", Bytecode.getOpcodeName(var3), this.info));
      } else {
         AbstractInsnNode var9;
         if (this.returnType.getSort() != 0) {
            if (var7 != 190) {
               var7 = var8.getOpcode(46);
            }

            var9 = BeforeFieldAccess.findArrayNode(var1.insns, var2, var7, var6);
            this.injectAtGetArray(var1, var2, var9, var4, var5);
         } else {
            var9 = BeforeFieldAccess.findArrayNode(var1.insns, var2, var8.getOpcode(79), var6);
            this.injectAtSetArray(var1, var2, var9, var4, var5);
         }

      }
   }

   private void injectAtGetArray(Target var1, FieldInsnNode var2, AbstractInsnNode var3, Type var4, Type var5) {
      String var6 = getGetArrayHandlerDescriptor(var3, this.returnType, var5);
      boolean var7 = this.checkDescriptor(var6, var1, "array getter");
      this.injectArrayRedirect(var1, var2, var3, var7, "array getter");
   }

   private void injectAtSetArray(Target var1, FieldInsnNode var2, AbstractInsnNode var3, Type var4, Type var5) {
      String var6 = Bytecode.generateDescriptor((Object)null, (Object[])getArrayArgs(var5, 1, var5.getElementType()));
      boolean var7 = this.checkDescriptor(var6, var1, "array setter");
      this.injectArrayRedirect(var1, var2, var3, var7, "array setter");
   }

   public void injectArrayRedirect(Target var1, FieldInsnNode var2, AbstractInsnNode var3, boolean var4, String var5) {
      if (var3 == null) {
         String var7 = "";
         throw new InvalidInjectionException(this.info, String.format("Array element %s on %s could not locate a matching %s instruction in %s. %s", this.annotationType, this, var5, var1, var7));
      } else {
         if (!this.isStatic) {
            var1.insns.insertBefore(var2, (AbstractInsnNode)(new VarInsnNode(25, 0)));
            var1.addToStack(1);
         }

         InsnList var6 = new InsnList();
         if (var4) {
            this.pushArgs(var1.arguments, var6, var1.getArgIndices(), 0, var1.arguments.length);
            var1.addToStack(Bytecode.getArgsSize(var1.arguments));
         }

         var1.replaceNode(var3, this.invokeHandler(var6), var6);
      }
   }

   public void injectAtScalarField(Target var1, FieldInsnNode var2, int var3, Type var4, Type var5) {
      AbstractInsnNode var6 = null;
      InsnList var7 = new InsnList();
      if (var3 != 178 && var3 != 180) {
         if (var3 != 179 && var3 != 181) {
            throw new InvalidInjectionException(this.info, String.format("Unspported opcode %s for %s", Bytecode.getOpcodeName(var3), this.info));
         }

         var6 = this.injectAtPutField(var7, var1, var2, var3 == 179, var4, var5);
      } else {
         var6 = this.injectAtGetField(var7, var1, var2, var3 == 178, var4, var5);
      }

      var1.replaceNode(var2, var6, var7);
   }

   private AbstractInsnNode injectAtGetField(InsnList var1, Target var2, FieldInsnNode var3, boolean var4, Type var5, Type var6) {
      String var7 = var4 ? Bytecode.generateDescriptor(var6) : Bytecode.generateDescriptor(var6, var5);
      boolean var8 = this.checkDescriptor(var7, var2, "getter");
      if (!this.isStatic) {
         var1.add((AbstractInsnNode)(new VarInsnNode(25, 0)));
         if (!var4) {
            var1.add((AbstractInsnNode)(new InsnNode(95)));
         }
      }

      if (var8) {
         this.pushArgs(var2.arguments, var1, var2.getArgIndices(), 0, var2.arguments.length);
         var2.addToStack(Bytecode.getArgsSize(var2.arguments));
      }

      var2.addToStack(this.isStatic ? 0 : 1);
      return this.invokeHandler(var1);
   }

   private AbstractInsnNode injectAtPutField(InsnList var1, Target var2, FieldInsnNode var3, boolean var4, Type var5, Type var6) {
      String var7 = var4 ? Bytecode.generateDescriptor((Object)null, var6) : Bytecode.generateDescriptor((Object)null, var5, var6);
      boolean var8 = this.checkDescriptor(var7, var2, "setter");
      if (!this.isStatic) {
         if (var4) {
            var1.add((AbstractInsnNode)(new VarInsnNode(25, 0)));
            var1.add((AbstractInsnNode)(new InsnNode(95)));
         } else {
            int var9 = var2.allocateLocals(var6.getSize());
            var1.add((AbstractInsnNode)(new VarInsnNode(var6.getOpcode(54), var9)));
            var1.add((AbstractInsnNode)(new VarInsnNode(25, 0)));
            var1.add((AbstractInsnNode)(new InsnNode(95)));
            var1.add((AbstractInsnNode)(new VarInsnNode(var6.getOpcode(21), var9)));
         }
      }

      if (var8) {
         this.pushArgs(var2.arguments, var1, var2.getArgIndices(), 0, var2.arguments.length);
         var2.addToStack(Bytecode.getArgsSize(var2.arguments));
      }

      var2.addToStack(!this.isStatic && !var4 ? 1 : 0);
      return this.invokeHandler(var1);
   }

   protected boolean checkDescriptor(String var1, Target var2, String var3) {
      if (this.methodNode.desc.equals(var1)) {
         return false;
      } else {
         int var4 = var1.indexOf(41);
         String var5 = String.format("%s%s%s", var1.substring(0, var4), Joiner.on("").join(var2.arguments), var1.substring(var4));
         if (this.methodNode.desc.equals(var5)) {
            return true;
         } else {
            throw new InvalidInjectionException(this.info, String.format("%s method %s %s has an invalid signature. Expected %s but found %s", this.annotationType, var3, this, var1, this.methodNode.desc));
         }
      }
   }

   protected void injectAtConstructor(Target var1, InjectionNodes.InjectionNode var2) {
      RedirectInjector.ConstructorRedirectData var3 = (RedirectInjector.ConstructorRedirectData)var2.getDecoration("ctor");
      if (var3 == null) {
         throw new InvalidInjectionException(this.info, String.format("%s ctor redirector has no metadata, the injector failed a preprocessing phase", this.annotationType));
      } else {
         TypeInsnNode var4 = (TypeInsnNode)var2.getCurrentTarget();
         AbstractInsnNode var5 = var1.get(var1.indexOf((AbstractInsnNode)var4) + 1);
         MethodInsnNode var6 = var1.findInitNodeFor(var4);
         if (var6 == null) {
            if (!var3.wildcard) {
               throw new InvalidInjectionException(this.info, String.format("%s ctor invocation was not found in %s", this.annotationType, var1));
            }
         } else {
            boolean var7 = var5.getOpcode() == 89;
            String var8 = var6.desc.replace(")V", ")L" + var4.desc + ";");
            boolean var9 = false;

            try {
               var9 = this.checkDescriptor(var8, var1, "constructor");
            } catch (InvalidInjectionException var12) {
               if (!var3.wildcard) {
                  throw var12;
               }

               return;
            }

            if (var7) {
               var1.removeNode(var5);
            }

            if (this.isStatic) {
               var1.removeNode(var4);
            } else {
               var1.replaceNode(var4, (AbstractInsnNode)(new VarInsnNode(25, 0)));
            }

            InsnList var10 = new InsnList();
            if (var9) {
               this.pushArgs(var1.arguments, var10, var1.getArgIndices(), 0, var1.arguments.length);
               var1.addToStack(Bytecode.getArgsSize(var1.arguments));
            }

            this.invokeHandler(var10);
            if (var7) {
               LabelNode var11 = new LabelNode();
               var10.add((AbstractInsnNode)(new InsnNode(89)));
               var10.add((AbstractInsnNode)(new JumpInsnNode(199, var11)));
               this.throwException(var10, "java/lang/NullPointerException", String.format("%s constructor handler %s returned null for %s", this.annotationType, this, var4.desc.replace('/', '.')));
               var10.add((AbstractInsnNode)var11);
               var1.addToStack(1);
            } else {
               var10.add((AbstractInsnNode)(new InsnNode(87)));
            }

            var1.replaceNode(var6, (InsnList)var10);
            ++var3.injected;
         }
      }
   }

   private static String getGetArrayHandlerDescriptor(AbstractInsnNode var0, Type var1, Type var2) {
      return var0 != null && var0.getOpcode() == 190 ? Bytecode.generateDescriptor(Type.INT_TYPE, (Object[])getArrayArgs(var2, 0)) : Bytecode.generateDescriptor(var1, (Object[])getArrayArgs(var2, 1));
   }

   private static Type[] getArrayArgs(Type var0, int var1, Type... var2) {
      int var3 = var0.getDimensions() + var1;
      Type[] var4 = new Type[var3 + var2.length];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var4[var5] = var5 == 0 ? var0 : (var5 < var3 ? Type.INT_TYPE : var2[var3 - var5]);
      }

      return var4;
   }

   static class RedirectedInvoke {
      final Target target;
      final MethodInsnNode node;
      final Type returnType;
      final Type[] args;
      final Type[] locals;
      boolean captureTargetArgs = false;

      RedirectedInvoke(Target var1, MethodInsnNode var2) {
         this.target = var1;
         this.node = var2;
         this.returnType = Type.getReturnType(var2.desc);
         this.args = Type.getArgumentTypes(var2.desc);
         this.locals = var2.getOpcode() == 184 ? this.args : (Type[])ObjectArrays.concat(Type.getType("L" + var2.owner + ";"), this.args);
      }
   }

   static class ConstructorRedirectData {
      public static final String KEY = "ctor";
      public boolean wildcard = false;
      public int injected = 0;
   }

   class Meta {
      public static final String KEY = "redirector";
      final int priority;
      final boolean isFinal;
      final String name;
      final String desc;

      public Meta(int var2, boolean var3, String var4, String var5) {
         this.priority = var2;
         this.isFinal = var3;
         this.name = var4;
         this.desc = var5;
      }

      RedirectInjector getOwner() {
         return RedirectInjector.this;
      }
   }
}
