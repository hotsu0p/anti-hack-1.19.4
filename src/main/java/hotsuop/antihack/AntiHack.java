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
        double maxAllowedSpeed = 0.1838147611607687; 

		if (speed > maxAllowedSpeed) {
			System.out.println("speed is " + speed);
			player.teleport(player.getWorld(), player.getX() - 2, player.getY(), player.getZ(), player.getYaw(), player.getPitch());
		}
    }
}
