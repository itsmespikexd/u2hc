package com.u2hc;

import com.google.gson.JsonObject;
import com.u2hc.config.U2HCConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Path;
import java.util.Set;

public class U2HCMod implements ModInitializer {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static U2HCConfig ACTIVE_CONFIG;
	public static U2HCConfig PENDING_CONFIG;

	public static boolean isComplete = false;

	@Override
	public void onInitialize() {
		AutoConfig.register(U2HCConfig.class, GsonConfigSerializer::new);

		PayloadTypeRegistry.playS2C().register(U2HCPayload.ID, U2HCPayload.CODEC);

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			isComplete = false;

			U2HCState.reset();

			File file = server.getWorldPath(LevelResource.ROOT).resolve("u2hc_state.json").toFile();
			if (file.exists()) {
				try (Reader reader = new FileReader(file)) {
					JsonObject json = GSON.fromJson(reader, JsonObject.class);
					U2HCState.deathCount = json.has("deathCount") ? json.get("deathCount").getAsInt() : 0;
					U2HCState.lastDayApplied = json.has("lastDay") ? json.get("lastDay").getAsLong() : -1;
				} catch (Exception e) {}
			} else {
				U2HCState.deathCount = 0;
				U2HCState.lastDayApplied = -1;
			}

			Path configPath = server.getWorldPath(LevelResource.ROOT).resolve("u2hc_config.json");
			File configFile = configPath.toFile();


			if (configFile.exists()) {
				try (Reader reader = new FileReader(configFile)) {
					ACTIVE_CONFIG = GSON.fromJson(reader, U2HCConfig.class);
				} catch (Exception e) {
					ACTIVE_CONFIG = new U2HCConfig();
				}
			} else {
				ACTIVE_CONFIG = (PENDING_CONFIG != null) ? PENDING_CONFIG : AutoConfig.getConfigHolder(U2HCConfig.class).getConfig().copy();
				try (Writer writer = new FileWriter(configFile)) {
					GSON.toJson(ACTIVE_CONFIG, writer);
				} catch (IOException ignored) {}
			}
			PENDING_CONFIG = null;

			File stateFile = server.getWorldPath(LevelResource.ROOT).resolve("u2hc_state.json").toFile();
			if (stateFile.exists()) {
				try (Reader reader = new FileReader(stateFile)) {
					com.google.gson.JsonObject json = GSON.fromJson(reader, com.google.gson.JsonObject.class);
					if (json.has("lastDayApplied")) {
						U2HCState.lastDayApplied = json.get("lastDayApplied").getAsLong();
					}
				} catch (Exception e) {
					U2HCState.lastDayApplied = -1;
				}
			} else {
				U2HCState.lastDayApplied = -1;
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (entity instanceof ServerPlayer player && !isComplete) {
				U2HCState.deathCount++;

				ServerPlayNetworking.send(player, new U2HCPayload(-1, U2HCState.deathCount));

				File file = player.server.getWorldPath(LevelResource.ROOT).resolve("u2hc_state.json").toFile();
				try (Writer writer = new FileWriter(file)) {
					JsonObject json = new JsonObject();

					json.addProperty("deathCount", U2HCState.deathCount);
					GSON.toJson(json, writer);
				} catch (IOException ignored) {}
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayNetworking.send(handler.player, new U2HCPayload(-1, U2HCState.deathCount));
		});

		ServerTickEvents.START_WORLD_TICK.register(level -> {
			U2HCState.currentDimension = level.dimension();

			if (ACTIVE_CONFIG != null) {
				level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_TNT_EXPLOSION_DROP_DECAY)
						.set(!ACTIVE_CONFIG.globalExplosionDropRate, level.getServer());
			}

			if (level.getDifficulty() != Difficulty.HARD) {
				level.getServer().setDifficulty(Difficulty.HARD, true);
			}

			if (ACTIVE_CONFIG.noNaturalRegen) level.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, level.getServer());

			long timeOfDay = level.getDayTime() % 24000;
			long currentDay = level.getDayTime() / 24000;


			if (ACTIVE_CONFIG.increasedWeatherIntensity && !level.isThundering() && level.random.nextInt(5000) == 0) {
				level.setWeatherParameters(8000, 6000, true, true);
			}

			if (ACTIVE_CONFIG.negativeEffectsAtNight && timeOfDay >= 13000 && timeOfDay < 13020 && U2HCState.lastDayApplied != currentDay) {

				for (ServerPlayer p : level.players()) {
					p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3600, 0));
					p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 3600, 0));
					p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 3600, 0));
				}

				U2HCState.lastDayApplied = currentDay;
				saveStateToFile(level.getServer());
			}

			for (ServerPlayer player : level.players()) {

				if (ACTIVE_CONFIG.versionLock10) {
					for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
						ItemStack s = player.getInventory().getItem(i);
						if (s.isEmpty()) continue;
						String id = BuiltInRegistries.ITEM.getKey(s.getItem()).getPath();
						if (!WHITELIST_10.contains(id) || (ACTIVE_CONFIG.noArmor && (id.contains("_helmet") || id.contains("_chestplate") || id.contains("_leggings") || id.contains("_boots")))) {
							s.setCount(0);
							continue;
						}

						net.minecraft.world.item.alchemy.PotionContents potion = s.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
						if (potion != null) {
							potion.potion().ifPresent(potionHolder -> {
								String pId = potionHolder.unwrapKey().get().location().getPath();

								if (pId.contains("luck") || pId.contains("slow_falling") ||
										pId.contains("wind_charged") || pId.contains("weaving") ||
										pId.contains("infestation") || pId.contains("oozing")) {

									s.setCount(0);
								}
							});
						}
					}
				}

				if (ACTIVE_CONFIG.endermanAlwaysHostile)
					level.getEntitiesOfClass(EnderMan.class, player.getBoundingBox().inflate(32)).forEach(e -> e.setTarget(player));
				if (ACTIVE_CONFIG.hostileWolves)
					level.getEntitiesOfClass(Wolf.class, player.getBoundingBox().inflate(32)).forEach(w -> w.setTarget(player));
				if (ACTIVE_CONFIG.noSprint) player.setSprinting(false);

			}
		});

		UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
			return (ACTIVE_CONFIG.noVillagerTrading && entity.getType() == EntityType.VILLAGER) ? InteractionResult.FAIL : InteractionResult.PASS;
		});


	}

	private void saveWorldData(File file) {
		try (Writer writer = new FileWriter(file)) {
			JsonObject json = new JsonObject();
			json.addProperty("deathCount", U2HCState.deathCount);
			json.addProperty("lastDay", U2HCState.lastDayApplied);
			GSON.toJson(json, writer);
		} catch (IOException ignored) {}
	}

	private void loadWorldData(File file) {
		if (!file.exists()) { U2HCState.deathCount = 0; return; }
		try (Reader reader = new FileReader(file)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class);
			if (json.has("deathCount")) U2HCState.deathCount = json.get("deathCount").getAsInt();
		} catch (Exception e) { U2HCState.deathCount = 0; }
	}

	private void saveStateToFile(net.minecraft.server.MinecraftServer server) {
		File stateFile = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("u2hc_state.json").toFile();
		try (java.io.Writer writer = new java.io.FileWriter(stateFile)) {
			com.google.gson.JsonObject json = new com.google.gson.JsonObject();
			json.addProperty("lastDayApplied", U2HCState.lastDayApplied);
			new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
		} catch (java.io.IOException ignored) {}
	}

	public static void exportLog(net.minecraft.server.level.ServerLevel level, String status) {
		if (isComplete) return;
		try (FileWriter fw = new FileWriter("U2HC_Run.log", true)) {
			fw.write(String.format("[%s] Time: %d | World: %s\n", status, level.getGameTime(), level.getServer().getWorldData().getLevelName()));
		} catch (IOException ignored) {}
		isComplete = true;
	}

	public static final Set<String> WHITELIST_10 = Set.of("stone","grass_block","dirt","cobblestone","oak_planks","oak_sapling","spruce_sapling","birch_sapling","bedrock","water","lava","sand","gravel","gold_ore","iron_ore","coal_ore","oak_log","oak_leaves","spruce_log","spruce_leaves","birch_log","birch_leaves","sponge","glass","lapis_ore","lapis_block","dispenser","sandstone","note_block","red_bed","powered_rail","detector_rail","sticky_piston","cobweb","short_grass","dead_bush","fern","piston","white_wool","orange_wool","magenta_wool","light_blue_wool","yellow_wool","lime_wool","pink_wool","gray_wool","light_gray_wool","cyan_wool","purple_wool","blue_wool","brown_wool","green_wool","red_wool","black_wool","dandelion","poppy","brown_mushroom","red_mushroom","gold_block","iron_block","smooth_stone_slab","sandstone_slab","oak_slab","cobblestone_slab","brick_slab","stone_brick_slab","smooth_stone","bricks","tnt","bookshelf","mossy_cobblestone","obsidian","torch","fire","spawner","oak_stairs","chest","redstone_wire","diamond_ore","diamond_block","crafting_table","wheat","farmland","furnace","ladder","rail","cobblestone_stairs","lever","stone_pressure_plate","iron_door","oak_pressure_plate","redstone_ore","redstone_torch","stone_button","snow","ice","snow_block","cactus","clay","sugar_cane","jukebox","oak_fence","pumpkin","netherrack","soul_sand","glowstone","nether_portal","jack_o_lantern","cake","oak_trapdoor","stone_bricks","mossy_stone_bricks","cracked_stone_bricks","iron_bars","glass_pane","repeater","melon","pumpkin_stem","melon_stem","vine","oak_fence_gate","brick_stairs","stone_brick_stairs","mycelium","lily_pad","nether_brick","nether_brick_fence","nether_brick_stairs","nether_wart","enchanting_table","brewing_stand","cauldron","end_portal","end_portal_frame","end_stone","dragon_egg","redstone_lamp","iron_shovel","iron_pickaxe","iron_axe","flint_and_steel","apple","bow","arrow","coal","charcoal","diamond","iron_ingot","gold_ingot","iron_sword","wooden_sword","wooden_shovel","wooden_pickaxe","wooden_axe","stone_sword","stone_shovel","stone_pickaxe","stone_axe","diamond_sword","diamond_shovel","diamond_pickaxe","diamond_axe","stick","bowl","mushroom_stew","golden_sword","golden_shovel","golden_pickaxe","golden_axe","string","feather","gunpowder","wooden_hoe","stone_hoe","iron_hoe","golden_hoe","diamond_hoe","wheat_seeds","bread","leather_helmet","leather_chestplate","leather_leggings","leather_boots","chainmail_helmet","chainmail_chestplate","chainmail_leggings","chainmail_boots","iron_helmet","iron_chestplate","iron_leggings","iron_boots","diamond_helmet","diamond_chestplate","diamond_leggings","diamond_boots","golden_helmet","golden_chestplate","golden_leggings","golden_boots","gold_nugget","flint","porkchop","cooked_porkchop","painting","golden_apple","sign","oak_door","bucket","water_bucket","lava_bucket","minecart","saddle","redstone","snowball","oak_boat","leather","milk_bucket","brick","clay_ball","paper","book","slime_ball","chest_minecart","furnace_minecart","egg","compass","fishing_rod","clock","glowstone_dust","cod","cooked_cod","bone","sugar","cookie","map","shears","melon_slice","pumpkin_seeds","melon_seeds","beef","cooked_beef","chicken","cooked_chicken","rotten_flesh","ender_pearl","blaze_rod","ghast_tear","potion","splash_potion","glass_bottle","spider_eye","fermented_spider_eye","blaze_powder","magma_cream","ender_eye","glistering_melon_slice","red_dye","green_dye","cocoa_beans","lapis_lazuli","purple_dye","cyan_dye","light_gray_dye","gray_dye","pink_dye","lime_dye","yellow_dye","light_blue_dye","magenta_dye","orange_dye","bone_meal","music_disc_13","music_disc_cat","music_disc_blocks","music_disc_chirp","music_disc_far","music_disc_mall","music_disc_mellohi","music_disc_stal","music_disc_strad","music_disc_ward","music_disc_11");
}