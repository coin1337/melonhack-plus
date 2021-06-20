package jibet.zenhao.melon.mixin.client;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderLiving.class})
public class MixinRenderLiving {
   public MixinRenderLiving() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"doRender"},
      at = {@At("HEAD")}
   )
   private void injectChamsPre(EntityLiving param1, double param2, double param4, double param6, float param8, float param9, CallbackInfo param10) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"doRender"},
      at = {@At("RETURN")}
   )
   private <S extends EntityLivingBase> void injectChamsPost(EntityLiving param1, double param2, double param4, double param6, float param8, float param9, CallbackInfo param10) {
      // $FF: Couldn't be decompiled
   }
}
