package jibet.zenhao.melon.mixin.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkCache.class})
public class MixinChunkCache {
   public MixinChunkCache() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"getBlockState"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getState(BlockPos param1, CallbackInfoReturnable<IBlockState> param2) {
      // $FF: Couldn't be decompiled
   }
}
