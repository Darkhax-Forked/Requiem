package ladysnake.dissolution.common.commands;

import ladysnake.dissolution.api.corporeality.IIncorporealHandler;
import ladysnake.dissolution.common.Dissolution;
import ladysnake.dissolution.common.capabilities.CapabilityIncorporealHandler;
import ladysnake.dissolution.common.handlers.CustomDissolutionTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class CommandStuck extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "stuck";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("helpmeplz");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayer && Dissolution.config.ghost.allowStuckCommand;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "commands.dissolution.stuck.usage";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        IIncorporealHandler handler = CapabilityIncorporealHandler.getHandler(player);
        if (sender.canUseCommand(getRequiredPermissionLevel(), getName()) || handler.getCorporealityStatus().isIncorporeal()) {
            if (player.dimension != player.getSpawnDimension()) {
                CustomDissolutionTeleporter.transferPlayerToDimension(player, player.getSpawnDimension());
            }
            BlockPos spawnPos = player.getBedLocation();
            if(spawnPos == null) {
                spawnPos = player.getServerWorld().getSpawnPoint();
            }
            player.connection.setPlayerLocation(spawnPos.getX(), spawnPos.getY(),
                    spawnPos.getZ(), player.cameraYaw, player.cameraPitch);
        } else {
            throw new CommandException("commands.dissolution.stuck.soulrequired");
        }
    }
}
