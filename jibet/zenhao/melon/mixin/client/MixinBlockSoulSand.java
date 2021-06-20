package jibet.zenhao.melon.mixin.client;

import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockSoulSand.class})
public class MixinBlockSoulSand {
   public MixinBlockSoulSand() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"onEntityCollision"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityCollidedWithBlock(World param1, BlockPos param2, IBlockState param3, Entity param4, CallbackInfo param5) {
      // $FF: Couldn't be decompiled
   }
}
