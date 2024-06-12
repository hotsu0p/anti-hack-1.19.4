package hotsuop.antihack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AntiHack implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(this::checkPlayerSpeed);
        });
    }

    private void checkPlayerSpeed(ServerPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        double maxAllowedSpeed = 0.23085341576773576; 

		if (speed > maxAllowedSpeed) {
			System.out.println("Player  has a speed of " + speed + " which is higher than the allowed speed of " + maxAllowedSpeed);
			player.teleport(player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
		}
    }
}
