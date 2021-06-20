package jibet.zenhao.melon.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public class MixinEntity {
   @Shadow
   public double field_70159_w;
   @Shadow
   public double field_70181_x;
   @Shadow
   public double field_70179_y;

   public MixinEntity() {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"applyEntityCollision"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"
)
   )
   public void addVelocity(Entity param1, double param2, double param4, double param6) {
      // $FF: Couldn't be decompiled
   }

   @Shadow
   public void func_70091_d(MoverType param1, double param2, double param4, double param6) {
      // $FF: Couldn't be decompiled
   }

   public void jump() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"isEntityInsideOpaqueBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> param1) {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"move"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;isSneaking()Z"
)
   )
   public boolean isSneaking(Entity param1) {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"applyEntityCollision"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"
)
   )
   public void addVelocityHook(Entity param1, double param2, double param4, double param6) {
      // $FF: Couldn't be decompiled
   }
}
