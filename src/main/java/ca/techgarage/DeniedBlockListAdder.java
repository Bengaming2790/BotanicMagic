package ca.techgarage;

import ca.techgarage.entity.BlockProjectileEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeniedBlockListAdder {

    private static final String FILE_NAME = "deniedblocks_botanic.yaml";


    public static void loadDeniedBlocks(Path serverRoot) {
        try {
            Path configDir = serverRoot.resolve("config");
            Path configFile = configDir.resolve(FILE_NAME);

            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            if (!Files.exists(configFile)) {
                createDefaultConfig(configFile);
            }

            Yaml yaml = new Yaml();

            try (InputStream inputStream = Files.newInputStream(configFile)) {

                Map<String, Object> data = yaml.load(inputStream);

                BlockProjectileEntity.deniedBlocks.clear();

                if (data != null && data.containsKey("denied_blocks")) {

                    Object rawList = data.get("denied_blocks");

                    if (rawList instanceof List<?> list) {

                        for (Object obj : list) {

                            if (!(obj instanceof String blockId)) {
                                continue;
                            }

                            Identifier id = Identifier.tryParse(blockId);

                            if (id == null) {
                                System.err.println("[Botanic Magic] Invalid block id in denied block list: " + blockId);
                                continue;
                            }

                            if (BuiltInRegistries.BLOCK.containsKey(id)) {

                                Block block = BuiltInRegistries.BLOCK.getValue(id);

                                if (!BlockProjectileEntity.deniedBlocks.contains(block)) {
                                    BlockProjectileEntity.deniedBlocks.add(block);
                                }

                            } else {
                                System.err.println("[Botanic Magic] Unknown block in denied block list: " + blockId);
                            }
                        }
                    }
                }

                System.out.println("[Botanic Magic] Loaded " + BlockProjectileEntity.deniedBlocks.size() + " denied blocks.");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDeniedBlocks(Path serverRoot) {

        try {

            Path configDir = serverRoot.resolve("config");
            Path configFile = configDir.resolve(FILE_NAME);

            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            Yaml yaml = new Yaml();

            List<String> blockIds = new ArrayList<>();

            for (Block block : BlockProjectileEntity.deniedBlocks) {

                Identifier id = BuiltInRegistries.BLOCK.getKey(block);

                if (id != null) {
                    blockIds.add(id.toString());
                }
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("denied_blocks", blockIds);

            try (Writer writer = Files.newBufferedWriter(configFile)) {
                yaml.dump(data, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void createDefaultConfig(Path configFile) throws IOException {

        List<String> defaultBlocks = List.of(
                BuiltInRegistries.BLOCK.getKey(Blocks.BEDROCK).toString(),
                BuiltInRegistries.BLOCK.getKey(Blocks.END_PORTAL_FRAME).toString(),
                BuiltInRegistries.BLOCK.getKey(Blocks.END_PORTAL).toString(),
                BuiltInRegistries.BLOCK.getKey(Blocks.NETHER_PORTAL).toString(),
                BuiltInRegistries.BLOCK.getKey(Blocks.REINFORCED_DEEPSLATE).toString()
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("denied_blocks", defaultBlocks);

        Yaml yaml = new Yaml();

        try (Writer writer = Files.newBufferedWriter(configFile)) {
            yaml.dump(data, writer);
        }
    }
}