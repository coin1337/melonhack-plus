package jibet.zenhao.melon.mixin.client;

import java.util.Set;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VisGraph.class})
public class MixinVisGraph {
   public MixinVisGraph() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"getVisibleFacings"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getVisibleFacings(CallbackInfoReturnable<Set<EnumFacing>> param1) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"setOpaqueCube"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void setOpaqueCube(BlockPos param1, CallbackInfo param2) {
      // $FF: Couldn't be decompiled
   }
}
