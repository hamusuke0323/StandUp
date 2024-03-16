package com.hamusuke.standup.stand.stands;

import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.s2c.DeadlyQueenWantsToKnowNewBombInfoReq;
import com.hamusuke.standup.stand.ability.deadly_queen.BlockBomb;
import com.hamusuke.standup.stand.ability.deadly_queen.Bomb;
import com.hamusuke.standup.stand.ability.deadly_queen.Bomb.When;
import com.hamusuke.standup.stand.ability.deadly_queen.EntityBomb;
import com.hamusuke.standup.stand.ai.goal.StandRushAttackGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandMultipleAttackableTargetsGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandOwnerHurtTargetGoal;
import com.hamusuke.standup.stand.card.StandCard;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import static com.hamusuke.standup.StandUp.MOD_ID;
import static com.hamusuke.standup.registry.RegisteredSoundEvents.APPEAR;
import static com.hamusuke.standup.registry.RegisteredSoundEvents.CLICK;

public class DeadlyQueen extends Stand {
    protected static final Component RELEASE_BOMB = Component.translatable(MOD_ID + ".stand.release.bomb");
    @Nullable
    protected Bomb bomb;

    public DeadlyQueen(Level level, Player owner, boolean slim, StandCard standCardId) {
        super(level, owner, slim, standCardId);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new StandRushAttackGoal(this, 3, true));

        this.targetSelector.addGoal(1, new StandOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new StandMultipleAttackableTargetsGoal<>(this, LivingEntity.class, false, livingEntity -> {
            return !this.getOwner().isCreative() && livingEntity instanceof Enemy;
        }));
    }

    @Override
    public double maxMovableDistanceFromPlayer() {
        return 8.0D;
    }

    @Override
    public double getFollowRange() {
        return 8.0D;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.bomb != null && !this.bomb.isStillValid()) {
            this.bomb = null;
        }

        if (this.bomb != null) {
            this.bomb.tick();
        }
    }

    @Override
    public void onInteractAtBlock(BlockHitResult result) {
        if (this.bomb instanceof BlockBomb blockBomb && blockBomb.getBlockPos().equals(result.getBlockPos())) {
            this.releaseBomb();
            ((ServerPlayer) this.getOwner()).sendSystemMessage(RELEASE_BOMB, true);
            return;
        }

        NetworkManager.sendToClient(new DeadlyQueenWantsToKnowNewBombInfoReq(result), (ServerPlayer) this.getOwner());
    }

    @Override
    public void onInteractAt(EntityHitResult result) {
        if (this.bomb instanceof EntityBomb entityBomb && entityBomb.getTarget() == result.getEntity()) {
            this.releaseBomb();
            ((ServerPlayer) this.getOwner()).sendSystemMessage(RELEASE_BOMB, true);
            return;
        }

        if (result.getEntity() instanceof Stand) {
            return;
        }

        NetworkManager.sendToClient(new DeadlyQueenWantsToKnowNewBombInfoReq(result), (ServerPlayer) this.getOwner());
    }

    @Override
    public void onInteractAtAir() {
        if (this.bomb != null && this.bomb.getExplodeWhen() == When.PUSH_SWITCH) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), CLICK.get(), this.getSoundSource(), 1.0F, 1.0F);
            this.bomb.ignite();
        }
    }

    public void placeBomb(Bomb bomb) {
        this.bomb = bomb;
    }

    public void releaseBomb() {
        this.bomb = null;
    }

    @Override
    protected void onStandUp() {
        super.onStandUp();

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), APPEAR.get(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
