package ca.techgarage.items.seeds;

import ca.techgarage.ModBlocks;
import ca.techgarage.blocks.GrowableFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GlaciolusSeedsItem extends Item {

    public GlaciolusSeedsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState clicked = level.getBlockState(pos);

        if (!clicked.is(Blocks.DIRT) && !clicked.is(Blocks.GRASS_BLOCK)) {
            return InteractionResult.FAIL;
        }

        BlockState plantState = ModBlocks.GLACIOLUS.defaultBlockState()
                .setValue(GrowableFlowerBlock.AGE, 0);

        level.setBlock(pos.above(), plantState, 3);

        context.getItemInHand().shrink(1);
        level.setBlock(pos.above(), plantState, 3);

        level.playSound(
                null,
                pos.above(),
                net.minecraft.sounds.SoundEvents.GRASS_PLACE,
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0f,
                1.0f
        );
        return InteractionResult.SUCCESS;
    }
}