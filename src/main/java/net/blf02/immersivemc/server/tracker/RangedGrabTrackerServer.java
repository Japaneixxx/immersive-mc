package net.blf02.immersivemc.server.tracker;

import net.blf02.immersivemc.common.tracker.AbstractTracker;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RangedGrabTrackerServer extends AbstractTracker {

    public static final double moveMultiplier = 2d/3d;

    public final List<RangedGrabInfo> infos = new ArrayList<>();

    protected final List<RangedGrabInfo> toRemove = new LinkedList<>();

    public RangedGrabTrackerServer() {
        ServerTrackerInit.globalTrackers.add(this);
    }

    @Override
    protected void tick(PlayerEntity player) {
        for (RangedGrabInfo info : infos) {
            if (info.item == null || !info.item.isAlive() || info.tickTime <= 0) {
                toRemove.add(info);
            } else {
                info.tickTime--;
                info.item.setPickUpDelay(0);
                Vector3d baseVelocity = new Vector3d(0, 0, 0);
                if (info.tickTime > 35) {
                    baseVelocity = baseVelocity.add(0, 0.25, 0);
                }
                info.item.lookAt(EntityAnchorArgument.Type.EYES, info.player.position().add(0, 1, 0));
                Vector3d move = info.item.getLookAngle().multiply(moveMultiplier, moveMultiplier, moveMultiplier).add(baseVelocity);
                info.item.setDeltaMovement(move.x, move.y, move.z);
                info.item.hurtMarked = true; // velocityChanged from MCP
            }
        }

        for (RangedGrabInfo toRem : toRemove) {
            infos.remove(toRem);
        }
    }

    @Override
    protected boolean shouldTick(PlayerEntity player) {
        return infos.size() > 0;
    }

    public static class RangedGrabInfo {

        public final ItemEntity item;
        public final ServerPlayerEntity player;
        public int tickTime = 40;

        public RangedGrabInfo(ItemEntity item, ServerPlayerEntity player) {
            this.item = item;
            this.player = player;
        }
    }
}
