package jibet.zenhao.melon.mixin.client;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({StateImplementation.class})
public class MixinStateImplementation {
   @Shadow
   @Final
   private Block field_177239_a;

   public MixinStateImplementation() {
      // $FF: Couldn't be decompiled
   }

   @Redirect(
      method = {"addCollisionBoxToList"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/Block;addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V"
)
   )
   public void addCollisionBoxToList(Block param1, IBlockState param2, World param3, BlockPos param4, AxisAlignedBB param5, List<AxisAlignedBB> param6, @Nullable Entity param7, boolean param8) {
      // $FF: Couldn't be decompiled
   }
}
