package jibet.zenhao.melon.mixin.client;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockLiquid.class})
public class MixinBlockLiquid {
   public MixinBlockLiquid() {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"modifyAcceleration"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void modifyAcceleration(World param1, BlockPos param2, Entity param3, Vec3d param4, CallbackInfoReturnable param5) {
      // $FF: Couldn't be decompiled
   }

   @Inject(
      method = {"canCollideCheck"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void canCollideCheck(IBlockState param1, boolean param2, CallbackInfoReturnable<Boolean> param3) {
      // $FF: Couldn't be decompiled
   }
}
