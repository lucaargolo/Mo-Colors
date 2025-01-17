package me.steven.mocolors.blocks.models;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ColoredBakedModel implements BakedModel, UnbakedModel, FabricBakedModel {

    public abstract BakedModel getBaseBakedModel();

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext ctx) {
        int rawColor = getColor(blockRenderView, blockPos);
        ctx.pushTransform((q) -> {
            int color = 255 << 24 | rawColor;
            q.spriteColor(0, color, color, color, color);
            return true;
        });
        ctx.fallbackConsumer().accept(getBaseBakedModel());
        ctx.popTransform();
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext ctx) {
        ctx.pushTransform((q) -> {
            int rawColor = itemStack.getOrCreateTag().getInt("Color");
            int color = 255 << 24 | rawColor;
            q.spriteColor(0, color, color, color, color);
            return true;
        });
        ctx.fallbackConsumer().accept(getBaseBakedModel());
        ctx.popTransform();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public ModelTransformation getTransformation() {
        return MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier(new Identifier("stone"), "")).getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }

    public int getColor(BlockRenderView view, BlockPos pos) {
        Object obj = ((RenderAttachedBlockView) view).getBlockEntityRenderAttachment(pos);
        if (obj == null) return -1;
        return (int) obj;
    }
}
