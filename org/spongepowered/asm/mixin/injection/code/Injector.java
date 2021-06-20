package org.spongepowered.asm.mixin.injection.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.LdcInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.util.Bytecode;

public abstract class Injector {
   protected static final Logger logger = LogManager.getLogger("mixin");
   protected InjectionInfo info;
   protected final ClassNode classNode;
   protected final MethodNode methodNode;
   protected final Type[] methodArgs;
   protected final Type returnType;
   protected final boolean isStatic;

   public Injector(InjectionInfo var1) {
      this(var1.getClassNode(), var1.getMethod());
      this.info = var1;
   }

   private Injector(ClassNode var1, MethodNode var2) {
      this.classNode = var1;
      this.methodNode = var2;
      this.methodArgs = Type.getArgumentTypes(this.methodNode.desc);
      this.returnType = Type.getReturnType(this.methodNode.desc);
      this.isStatic = Bytecode.methodIsStatic(this.methodNode);
   }

   public String toString() {
      return String.format("%s::%s", this.classNode.name, this.methodNode.name);
   }

   public final List<InjectionNodes.InjectionNode> find(InjectorTarget var1, List<InjectionPoint> var2) {
      this.sanityCheck(var1.getTarget(), var2);
      ArrayList var3 = new ArrayList();
      Iterator var4 = this.findTargetNodes(var1, var2).iterator();

      while(var4.hasNext()) {
         Injector.TargetNode var5 = (Injector.TargetNode)var4.next();
         this.addTargetNode(var1.getTarget(), var3, var5.insn, var5.nominators);
      }

      return var3;
   }

   protected void addTargetNode(Target var1, List<InjectionNodes.InjectionNode> var2, AbstractInsnNode var3, Set<InjectionPoint> var4) {
      var2.add(var1.addInjectionNode(var3));
   }

   public final void inject(Target var1, List<InjectionNodes.InjectionNode> var2) {
      Iterator var3 = var2.iterator();

      InjectionNodes.InjectionNode var4;
      while(var3.hasNext()) {
         var4 = (InjectionNodes.InjectionNode)var3.next();
         if (var4.isRemoved()) {
            if (this.info.getContext().getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) {
               logger.warn("Target node for {} was removed by a previous injector in {}", new Object[]{this.info, var1});
            }
         } else {
            this.inject(var1, var4);
         }
      }

      var3 = var2.iterator();

      while(var3.hasNext()) {
         var4 = (InjectionNodes.InjectionNode)var3.next();
         this.postInject(var1, var4);
      }

   }

   private Collection<Injector.TargetNode> findTargetNodes(InjectorTarget var1, List<InjectionPoint> var2) {
      IMixinContext var3 = this.info.getContext();
      MethodNode var4 = var1.getMethod();
      TreeMap var5 = new TreeMap();
      ArrayList var6 = new ArrayList(32);
      Iterator var7 = var2.iterator();

      while(true) {
         InjectionPoint var8;
         do {
            if (!var7.hasNext()) {
               return var5.values();
            }

            var8 = (InjectionPoint)var7.next();
            var6.clear();
            if (var1.isMerged() && !var3.getClassName().equals(var1.getMergedBy()) && !var8.checkPriority(var1.getMergedPriority(), var3.getPriority())) {
               throw new InvalidInjectionException(this.info, String.format("%s on %s with priority %d cannot inject into %s merged by %s with priority %d", var8, this, var3.getPriority(), var1, var1.getMergedBy(), var1.getMergedPriority()));
            }
         } while(!this.findTargetNodes(var4, var8, var1.getSlice(var8), var6));

         Injector.TargetNode var12;
         for(Iterator var9 = var6.iterator(); var9.hasNext(); var12.nominators.add(var8)) {
            AbstractInsnNode var10 = (AbstractInsnNode)var9.next();
            Integer var11 = var4.instructions.indexOf(var10);
            var12 = (Injector.TargetNode)var5.get(var11);
            if (var12 == null) {
               var12 = new Injector.TargetNode(var10);
               var5.put(var11, var12);
            }
         }
      }
   }

   protected boolean findTargetNodes(MethodNode var1, InjectionPoint var2, InsnList var3, Collection<AbstractInsnNode> var4) {
      return var2.find(var1.desc, var3, var4);
   }

   protected void sanityCheck(Target var1, List<InjectionPoint> var2) {
      if (var1.classNode != this.classNode) {
         throw new InvalidInjectionException(this.info, "Target class does not match injector class in " + this);
      }
   }

   protected abstract void inject(Target var1, InjectionNodes.InjectionNode var2);

   protected void postInject(Target var1, InjectionNodes.InjectionNode var2) {
   }

   protected AbstractInsnNode invokeHandler(InsnList var1) {
      return this.invokeHandler(var1, this.methodNode);
   }

   protected AbstractInsnNode invokeHandler(InsnList var1, MethodNode var2) {
      boolean var3 = (var2.access & 2) != 0;
      int var4 = this.isStatic ? 184 : (var3 ? 183 : 182);
      MethodInsnNode var5 = new MethodInsnNode(var4, this.classNode.name, var2.name, var2.desc, false);
      var1.add((AbstractInsnNode)var5);
      this.info.addCallbackInvocation(var2);
      return var5;
   }

   protected void throwException(InsnList var1, String var2, String var3) {
      var1.add((AbstractInsnNode)(new TypeInsnNode(187, var2)));
      var1.add((AbstractInsnNode)(new InsnNode(89)));
      var1.add((AbstractInsnNode)(new LdcInsnNode(var3)));
      var1.add((AbstractInsnNode)(new MethodInsnNode(183, var2, "<init>", "(Ljava/lang/String;)V", false)));
      var1.add((AbstractInsnNode)(new InsnNode(191)));
   }

   public static boolean canCoerce(Type var0, Type var1) {
      return var0.getSort() == 10 && var1.getSort() == 10 ? canCoerce(ClassInfo.forType(var0), ClassInfo.forType(var1)) : canCoerce(var0.getDescriptor(), var1.getDescriptor());
   }

   public static boolean canCoerce(String var0, String var1) {
      return var0.length() <= 1 && var1.length() <= 1 ? canCoerce(var0.charAt(0), var1.charAt(0)) : false;
   }

   public static boolean canCoerce(char var0, char var1) {
      return var1 == 'I' && "IBSCZ".indexOf(var0) > -1;
   }

   private static boolean canCoerce(ClassInfo var0, ClassInfo var1) {
      return var0 != null && var1 != null && (var1 == var0 || var1.hasSuperClass(var0));
   }

   public static final class TargetNode {
      final AbstractInsnNode insn;
      final Set<InjectionPoint> nominators = new HashSet();

      TargetNode(AbstractInsnNode var1) {
         this.insn = var1;
      }

      public AbstractInsnNode getNode() {
         return this.insn;
      }

      public Set<InjectionPoint> getNominators() {
         return Collections.unmodifiableSet(this.nominators);
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == Injector.TargetNode.class) {
            return ((Injector.TargetNode)var1).insn == this.insn;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.insn.hashCode();
      }
   }
}
