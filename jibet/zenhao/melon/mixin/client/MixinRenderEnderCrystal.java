package jibet.zenhao.melon.mixin.client;

import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderEnderCrystal.class})
public class MixinRenderEnderCrystal extends Render<EntityEnderCrystal> {
   @Shadow
   private static final ResourceLocation field_110787_a;
   @Shadow
   private final ModelBase field_76995_b;
   @Shadow
   private final ModelBase field_188316_g;

   protected MixinRenderEnderCrystal(RenderManager param1) {
      // $FF: Couldn't be decompiled
   }

   @Overwrite
   public void func_76986_a(EntityEnderCrystal param1, double param2, double param4, double param6, float param8, float param9) {
      // $FF: Couldn't be decompiled
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityEnderCrystal param1) {
      // $FF: Couldn't be decompiled
   }

   static {
      // $FF: Couldn't be decompiled
   }
}
