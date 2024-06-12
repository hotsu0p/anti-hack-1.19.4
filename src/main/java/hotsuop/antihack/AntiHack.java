package hotsuop.antihack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class AntiHack implements ModInitializer {
	private final Map<UUID, Vec3d> playerPositions = new HashMap<>();
    private final Map<UUID, Long> playerViolationTimes = new HashMap<>();
    private static final long VIOLATION_RESET_TIME = 5000; // 5 sec
    private static final double MAX_ALLOWED_VERTICAL_VELOCITY = 0.4;
    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(this::checkPlayerSpeed);
        });
		ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!player.isCreative() && !player.isSpectator()) {
                    UUID playerId = player.getUuid();
                    Vec3d currentPosition = player.getPos();

                    if (player.getAbilities().flying) {
                        player.getAbilities().flying = false;
                        player.sendAbilitiesUpdate();
                    }

                    if (playerPositions.containsKey(playerId)) {
                        Vec3d previousPosition = playerPositions.get(playerId);
                        double verticalVelocity = currentPosition.y - previousPosition.y;

                        if (Math.abs(verticalVelocity) > MAX_ALLOWED_VERTICAL_VELOCITY) {
                            long currentTime = System.currentTimeMillis();
                            long lastViolationTime = playerViolationTimes.getOrDefault(playerId, 0L);

                            if (currentTime - lastViolationTime < VIOLATION_RESET_TIME) {
                                player.teleport(previousPosition.x, previousPosition.y, previousPosition.z);
                            }

                            playerViolationTimes.put(playerId, currentTime);
                        }
                    }

                    playerPositions.put(playerId, currentPosition);
                }
            }
        });
    }
       private void checkPlayerSpeed(ServerPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        double maxAllowedSpeed = 0.193; 

		if (speed > maxAllowedSpeed) {
			System.out.println("speed is " + speed);
			player.teleport(player.getWorld(), player.getX() - 1, player.getY() , player.getZ() - 1 , player.getYaw(), player.getPitch());
		}
    }
}
