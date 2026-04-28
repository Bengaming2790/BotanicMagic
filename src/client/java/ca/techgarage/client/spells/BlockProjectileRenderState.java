package ca.techgarage.client.spells;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockProjectileRenderState extends EntityRenderState {
    public BlockState blockState = Blocks.DIRT.defaultBlockState();
}
