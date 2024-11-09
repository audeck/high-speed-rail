package me.audeck.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class HighSpeedRailBlock extends BaseRailBlock {
    public static final MapCodec<HighSpeedRailBlock> CODEC = simpleCodec(HighSpeedRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE;
    public static final BooleanProperty POWERED;

    public MapCodec<HighSpeedRailBlock> codec() {
        return CODEC;
    }

    public HighSpeedRailBlock(BlockBehaviour.Properties properties) {
        super(true, properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
    }

    protected boolean findPoweredRailSignal(Level level, BlockPos blockPos, BlockState blockState, boolean bl, int i) {
        if (i >= 8) {
            return false;
        } else {
            int j = blockPos.getX();
            int k = blockPos.getY();
            int l = blockPos.getZ();
            boolean bl2 = true;
            RailShape railShape = (RailShape)blockState.getValue(SHAPE);
            switch (railShape) {
                case NORTH_SOUTH:
                    if (bl) {
                        ++l;
                    } else {
                        --l;
                    }
                    break;
                case EAST_WEST:
                    if (bl) {
                        --j;
                    } else {
                        ++j;
                    }
                    break;
                case ASCENDING_EAST:
                    if (bl) {
                        --j;
                    } else {
                        ++j;
                        ++k;
                        bl2 = false;
                    }

                    railShape = RailShape.EAST_WEST;
                    break;
                case ASCENDING_WEST:
                    if (bl) {
                        --j;
                        ++k;
                        bl2 = false;
                    } else {
                        ++j;
                    }

                    railShape = RailShape.EAST_WEST;
                    break;
                case ASCENDING_NORTH:
                    if (bl) {
                        ++l;
                    } else {
                        --l;
                        ++k;
                        bl2 = false;
                    }

                    railShape = RailShape.NORTH_SOUTH;
                    break;
                case ASCENDING_SOUTH:
                    if (bl) {
                        ++l;
                        ++k;
                        bl2 = false;
                    } else {
                        --l;
                    }

                    railShape = RailShape.NORTH_SOUTH;
            }

            if (this.isSameRailWithPower(level, new BlockPos(j, k, l), bl, i, railShape)) {
                return true;
            } else {
                return bl2 && this.isSameRailWithPower(level, new BlockPos(j, k - 1, l), bl, i, railShape);
            }
        }
    }

    protected boolean isSameRailWithPower(Level level, BlockPos blockPos, boolean bl, int i, RailShape railShape) {
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is(this)) {
            return false;
        } else {
            RailShape railShape2 = (RailShape)blockState.getValue(SHAPE);
            if (railShape == RailShape.EAST_WEST && (railShape2 == RailShape.NORTH_SOUTH || railShape2 == RailShape.ASCENDING_NORTH || railShape2 == RailShape.ASCENDING_SOUTH)) {
                return false;
            } else if (railShape == RailShape.NORTH_SOUTH && (railShape2 == RailShape.EAST_WEST || railShape2 == RailShape.ASCENDING_EAST || railShape2 == RailShape.ASCENDING_WEST)) {
                return false;
            } else if ((Boolean)blockState.getValue(POWERED)) {
                return level.hasNeighborSignal(blockPos) ? true : this.findPoweredRailSignal(level, blockPos, blockState, bl, i + 1);
            } else {
                return false;
            }
        }
    }

    protected void updateState(BlockState blockState, Level level, BlockPos blockPos, Block block) {
        boolean bl = (Boolean)blockState.getValue(POWERED);
        boolean bl2 = level.hasNeighborSignal(blockPos) || this.findPoweredRailSignal(level, blockPos, blockState, true, 0) || this.findPoweredRailSignal(level, blockPos, blockState, false, 0);
        if (bl2 != bl) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, bl2), 3);
            level.updateNeighborsAt(blockPos.below(), this);
            if (((RailShape)blockState.getValue(SHAPE)).isAscending()) {
                level.updateNeighborsAt(blockPos.above(), this);
            }
        }

    }

    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    protected BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                switch ((RailShape)blockState.getValue(SHAPE)) {
                    case ASCENDING_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_NORTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case SOUTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                }
            case COUNTERCLOCKWISE_90:
                switch ((RailShape)blockState.getValue(SHAPE)) {
                    case NORTH_SOUTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_NORTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_SOUTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                }
            case CLOCKWISE_90:
                switch ((RailShape)blockState.getValue(SHAPE)) {
                    case NORTH_SOUTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_NORTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_SOUTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case SOUTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
            default:
                return blockState;
        }
    }

    protected BlockState mirror(BlockState blockState, Mirror mirror) {
        RailShape railShape = (RailShape)blockState.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                switch (railShape) {
                    case ASCENDING_NORTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST -> {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    default -> {
                        return super.mirror(blockState, mirror);
                    }
                }
            case FRONT_BACK:
                switch (railShape) {
                    case ASCENDING_EAST:
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                }
        }

        return super.mirror(blockState, mirror);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SHAPE, POWERED, WATERLOGGED});
    }

    static {
        SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
        POWERED = BlockStateProperties.POWERED;
    }
}
