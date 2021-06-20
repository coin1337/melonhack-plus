package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
   public MixinItemRenderer() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"transformSideFirstPerson"},
      at = {@At("HEAD")}
   )
   public void transformSideFirstPerson(EnumHandSide param1, float param2, CallbackInfo param3) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"transformEatFirstPerson"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void transformEatFirstPerson(float param1, EnumHandSide param2, ItemStack param3, CallbackInfo param4) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"transformFirstPerson"},
      at = {@At("HEAD")}
   )
   public void transformFirstPerson(EnumHandSide param1, float param2, CallbackInfo param3) {
      // $FF: Couldn't be decompiled
   }
}
