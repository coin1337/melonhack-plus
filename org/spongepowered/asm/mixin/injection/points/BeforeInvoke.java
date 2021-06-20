package org.spongepowered.asm.mixin.injection.points;

import java.util.Collection;
import java.util.ListIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.refmap.IMixinContext;

@InjectionPoint.AtCode("INVOKE")
public class BeforeInvoke extends InjectionPoint {
   protected final MemberInfo target;
   protected final boolean allowPermissive;
   protected final int ordinal;
   protected final String className;
   protected final IMixinContext context;
   protected final Logger logger = LogManager.getLogger("mixin");
   private boolean log = false;

   public BeforeInvoke(InjectionPointData var1) {
      super(var1);
      this.target = var1.getTarget();
      this.ordinal = var1.getOrdinal();
      this.log = var1.get("log", false);
      this.className = this.getClassName();
      this.context = var1.getContext();
      this.allowPermissive = this.context.getOption(MixinEnvironment.Option.REFMAP_REMAP) && this.context.getOption(MixinEnvironment.Option.REFMAP_REMAP_ALLOW_PERMISSIVE) && !this.context.getReferenceMapper().isDefault();
   }

   private String getClassName() {
      InjectionPoint.AtCode var1 = (InjectionPoint.AtCode)this.getClass().getAnnotation(InjectionPoint.AtCode.class);
      return String.format("@At(%s)", var1 != null ? var1.value() : this.getClass().getSimpleName().toUpperCase());
   }

   public BeforeInvoke setLogging(boolean var1) {
      this.log = var1;
      return this;
   }

   public boolean find(String var1, InsnList var2, Collection<AbstractInsnNode> var3) {
      this.log("{} is searching for an injection point in method with descriptor {}", this.className, var1);
      if (!this.find(var1, var2, var3, this.target, BeforeInvoke.SearchType.STRICT) && this.target.desc != null && this.allowPermissive) {
         this.logger.warn("STRICT match for {} using \"{}\" in {} returned 0 results, attempting permissive search. To inhibit permissive search set mixin.env.allowPermissiveMatch=false", new Object[]{this.className, this.target, this.context});
         return this.find(var1, var2, var3, this.target, BeforeInvoke.SearchType.PERMISSIVE);
      } else {
         return true;
      }
   }

   protected boolean find(String var1, InsnList var2, Collection<AbstractInsnNode> var3, MemberInfo var4, BeforeInvoke.SearchType var5) {
      if (var4 == null) {
         return false;
      } else {
         MemberInfo var6 = var5 == BeforeInvoke.SearchType.PERMISSIVE ? var4.transform((String)null) : var4;
         int var7 = 0;
         int var8 = 0;

         AbstractInsnNode var10;
         for(ListIterator var9 = var2.iterator(); var9.hasNext(); this.inspectInsn(var1, var2, var10)) {
            var10 = (AbstractInsnNode)var9.next();
            if (this.matchesInsn(var10)) {
               MemberInfo var11 = new MemberInfo(var10);
               this.log("{} is considering insn {}", this.className, var11);
               if (var6.matches(var11.owner, var11.name, var11.desc)) {
                  this.log("{} > found a matching insn, checking preconditions...", this.className);
                  if (this.matchesInsn(var11, var7)) {
                     this.log("{} > > > found a matching insn at ordinal {}", this.className, var7);
                     if (this.addInsn(var2, var3, var10)) {
                        ++var8;
                     }

                     if (this.ordinal == var7) {
                        break;
                     }
                  }

                  ++var7;
               }
            }
         }

         if (var5 == BeforeInvoke.SearchType.PERMISSIVE && var8 > 1) {
            this.logger.warn("A permissive match for {} using \"{}\" in {} matched {} instructions, this may cause unexpected behaviour. To inhibit permissive search set mixin.env.allowPermissiveMatch=false", new Object[]{this.className, var4, this.context, var8});
         }

         return var8 > 0;
      }
   }

   protected boolean addInsn(InsnList var1, Collection<AbstractInsnNode> var2, AbstractInsnNode var3) {
      var2.add(var3);
      return true;
   }

   protected boolean matchesInsn(AbstractInsnNode var1) {
      return var1 instanceof MethodInsnNode;
   }

   protected void inspectInsn(String var1, InsnList var2, AbstractInsnNode var3) {
   }

   protected boolean matchesInsn(MemberInfo var1, int var2) {
      this.log("{} > > comparing target ordinal {} with current ordinal {}", this.className, this.ordinal, var2);
      return this.ordinal == -1 || this.ordinal == var2;
   }

   protected void log(String var1, Object... var2) {
      if (this.log) {
         this.logger.info(var1, var2);
      }

   }

   public static enum SearchType {
      STRICT,
      PERMISSIVE;
   }
}
