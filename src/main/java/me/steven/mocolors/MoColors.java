package me.steven.mocolors;

import me.steven.mocolors.blocks.*;
import me.steven.mocolors.gui.PainterScreenHandler;
import me.steven.mocolors.items.PainterItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class MoColors implements ModInitializer {

	public static final String MOD_ID = "mocolors";

	public static final Block COLORED_GLASS = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_glass"), new ColoredGlassBlock());
	public static final Item COLORED_GLASS_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_glass"), new BlockItem(COLORED_GLASS, new Item.Settings()));

	public static final Block COLORED_SLIME = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_slime"), new ColoredSlimeBlock());
	public static final Item COLORED_SLIME_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_slime"), new BlockItem(COLORED_SLIME, new Item.Settings()));

	public static final Block COLORED_GLASS_PANE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_glass_pane"), new ColoredGlassPaneBlock());
	public static final Item COLORED_GLASS_PANE_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_glass_pane"), new BlockItem(COLORED_GLASS_PANE, new Item.Settings()));

	public static final Block COLORED_WOOL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_wool"), new ColoredWoolBlock());
	public static final Item COLORED_WOOL_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_wool"), new BlockItem(COLORED_WOOL, new Item.Settings()));

	public static final Block COLORED_CONCRETE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_concrete"), new ColoredConcreteBlock());
	public static final Item COLORED_CONCRETE_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_concrete"), new BlockItem(COLORED_CONCRETE, new Item.Settings()));

	public static final Block COLORED_BRICKS = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_bricks"), new ColoredBrickBlock());
	public static final Item COLORED_BRICKS_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_bricks"), new BlockItem(COLORED_BRICKS, new Item.Settings()));

	public static final Block COLORED_BRICKS_SLAB = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_bricks_slab"), new ColoredBrickSlabBlock());
	public static final Item COLORED_BRICKS_SLAB_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_bricks_slab"), new BlockItem(COLORED_BRICKS_SLAB, new Item.Settings()));

	public static final Block COLORED_BRICKS_STAIRS = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "colored_bricks_stairs"), new ColoredBrickStairsBlock());
	public static final Item COLORED_BRICKS_STAIRS_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "colored_bricks_stairs"), new BlockItem(COLORED_BRICKS_STAIRS, new Item.Settings()));

	public static final Item PAINTER_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "painter"), new PainterItem());

	public static final BlockEntityType<ColoredBlockEntity> COLORED_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("mcs", "c"), FabricBlockEntityTypeBuilder.create(ColoredBlockEntity::new, COLORED_GLASS, COLORED_SLIME, COLORED_GLASS_PANE, COLORED_CONCRETE, COLORED_BRICKS, COLORED_BRICKS_STAIRS).build(null));
	public static final BlockEntityType<ColoredSlabBlockEntity> COLORED_SLAB_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("mcs", "cs"), FabricBlockEntityTypeBuilder.create(ColoredSlabBlockEntity::new, COLORED_BRICKS_SLAB).build(null));

	public static final ScreenHandlerType<PainterScreenHandler> DYE_MIXER_TYPE = ScreenHandlerRegistry.registerSimple(PainterScreenHandler.SCREEN_ID, PainterScreenHandler::new);

	public static final Identifier UPDATE_PAINTER_COLOR_PACKET = new Identifier(MOD_ID, "update_painter_color");

	public static final Identifier PAINTER_COLOR_PICK_PACKET = new Identifier(MOD_ID, "color_pick");

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(UPDATE_PAINTER_COLOR_PACKET, (server, player, handler, buf, responseSender) -> {
			int color = buf.readInt();
			server.execute(() -> {
				ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
				if (stack.getItem() == PAINTER_ITEM)
					stack.getOrCreateTag().putInt("Color", color);
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(PAINTER_COLOR_PICK_PACKET, (server, player, handler, buf, responseSender) -> {
			int color = buf.readInt();
			server.execute(() -> {
				ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
				if (stack.getItem() == PAINTER_ITEM) {
					stack.getOrCreateTag().putInt("Color", color);
					player.sendMessage(new LiteralText("Picked color #" + Integer.toHexString(color).toUpperCase(Locale.ROOT)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))), true);
				}
			});
		});
	}
}
