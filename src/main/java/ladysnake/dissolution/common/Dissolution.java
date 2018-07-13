package ladysnake.dissolution.common;


import ladylib.LLibContainer;
import ladylib.LadyLib;
import ladysnake.dissolution.client.ClientProxy;
import ladysnake.dissolution.common.capabilities.CapabilityIncorporealHandler;
import ladysnake.dissolution.common.capabilities.CapabilitySoulHandler;
import ladysnake.dissolution.common.commands.CommandDissolutionTree;
import ladysnake.dissolution.common.compat.ThaumcraftCompat;
import ladysnake.dissolution.common.config.DissolutionConfig;
import ladysnake.dissolution.common.config.DissolutionConfigManager;
import ladysnake.dissolution.common.handlers.EventHandlerCommon;
import ladysnake.dissolution.common.handlers.InteractEventsHandler;
import ladysnake.dissolution.common.handlers.LivingDeathHandler;
import ladysnake.dissolution.common.handlers.PlayerTickHandler;
import ladysnake.dissolution.common.init.CommonProxy;
import ladysnake.dissolution.common.inventory.DissolutionTab;
import ladysnake.dissolution.common.inventory.GuiProxy;
import ladysnake.dissolution.common.networking.PacketHandler;
import ladysnake.dissolution.common.tileentities.TileEntitySepulture;
import ladysnake.dissolution.common.tileentities.TileEntityWispInAJar;
import ladysnake.dissolution.unused.common.capabilities.CapabilityDistillateHandler;
import ladysnake.dissolution.unused.common.capabilities.CapabilityGenericInventoryProvider;
import ladysnake.dissolution.unused.common.tileentities.TileEntityLamentStone;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION,
        acceptedMinecraftVersions = Reference.MCVERSION, dependencies = Reference.DEPENDENCIES,
        guiFactory = Reference.GUI_FACTORY_CLASS)
public class Dissolution {

    @Instance(Reference.MOD_ID)
    public static Dissolution instance;

    public static DissolutionConfig config = new DissolutionConfig();

    public static final CreativeTabs CREATIVE_TAB = new DissolutionTab();
    public static final Logger LOGGER = LogManager.getLogger("Dissolution");

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    /**True if the last server checked does not have the mod installed*/
    public static boolean noServerInstall;

    @LadyLib.LLInstance
    private static LLibContainer lib;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityIncorporealHandler.register();
        CapabilitySoulHandler.register();
        CapabilityDistillateHandler.register();
        CapabilityGenericInventoryProvider.register();

        DissolutionConfigManager.init(event.getSuggestedConfigurationFile());

        lib.setCreativeTab(CREATIVE_TAB);
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        MinecraftForge.EVENT_BUS.register(new LivingDeathHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerTickHandler());
        MinecraftForge.EVENT_BUS.register(new InteractEventsHandler());

        OreDictHelper.registerOres();

        GameRegistry.registerTileEntity(TileEntitySepulture.class, Reference.MOD_ID + ":tileentitysepulture");
        GameRegistry.registerTileEntity(TileEntityLamentStone.class, Reference.MOD_ID + ":tileentityancienttomb");
        GameRegistry.registerTileEntity(TileEntityWispInAJar.class, Reference.MOD_ID + ":tileentitywispinajar");

        LootTableList.register(new ResourceLocation(Reference.MOD_ID, "inject/human"));

        NetworkRegistry.INSTANCE.registerGuiHandler(Dissolution.instance, new GuiProxy());
        PacketHandler.initPackets();

        if (FMLCommonHandler.instance().getSide().isClient()) {
            ClientProxy.init();
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("thaumcraft")) {
            ThaumcraftCompat.assignAspects();
        }
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDissolutionTree());
    }

    /**
     * This is just here to store whether the current connected server has dissolution installed, used in {@link ladysnake.dissolution.client.handlers.EventHandlerClient}
     * @see NetworkCheckHandler for signature information
     */
    @NetworkCheckHandler
    public boolean checkModLists(Map<String,String> modList, Side side) {
        boolean modInstalled = Reference.VERSION.equals(modList.get(Reference.MOD_ID));
        if (side.isServer()) {
            noServerInstall = !modInstalled;
        }
        return side.isServer() || modInstalled;
    }

}
