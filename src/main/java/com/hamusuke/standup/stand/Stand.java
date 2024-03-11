package com.hamusuke.standup.stand;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hamusuke.standup.invoker.LivingEntityInvoker;
import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.s2c.HoldOrReleaseStandOwnerNotify;
import com.hamusuke.standup.network.packet.s2c.StandOperationModeToggleNotify;
import com.hamusuke.standup.stand.ai.goal.AutoDoorGoal;
import com.hamusuke.standup.stand.ai.goal.AutoFenceGateGoal;
import com.hamusuke.standup.stand.ai.goal.FollowStandOwnerGoal;
import com.hamusuke.standup.stand.ai.goal.StandRushAttackGoal;
import com.hamusuke.standup.stand.ai.goal.target.MultipleAttackableTargetsGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandOwnerHurtTargetGoal;
import com.hamusuke.standup.util.MthH;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.hamusuke.standup.registries.RegisteredEntities.STAND_TYPE;
import static com.hamusuke.standup.registries.RegisteredSoundEvents.PUNCH;

public class Stand extends PathfinderMob implements MultipleTarget, OwnableEntity, IEntityAdditionalSpawnData {
    protected Player cachedOwner;
    protected UUID ownerUUID;
    protected StandOperationMode mode = StandOperationMode.AI;
    protected final Set<LivingEntity> targets = Sets.newHashSet();
    protected boolean holdingOwner;

    public Stand(Level level, @Nullable Player owner) {
        this(STAND_TYPE.get(), level, owner);
    }

    public Stand(EntityType<? extends Stand> type, Level p_21369_, @Nullable Player owner) {
        super(type, p_21369_);
        this.cachedOwner = owner;
        this.ownerUUID = owner == null ? null : owner.getUUID();
        this.moveControl = new FlyingMoveControl(this, 0, true) {
            @Override
            public void tick() {
                super.tick();

                if (this.operation != Operation.MOVE_TO) {
                    this.mob.setYya(this.mob.yya * 0.25F);
                }
            }
        };
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    public boolean isSlim() {
        return this instanceof SlimStand;
    }

    public double maxMovableDistanceFromPlayer() {
        return 16.0D;
    }

    public boolean isTooFarAway() {
        return this.getOwner() != null && !this.getOwner().position().closerThan(this.position(), this.maxMovableDistanceFromPlayer());
    }

    @Override
    protected PathNavigation createNavigation(Level p_21480_) {
        var navigation = new FlyingPathNavigation(this, p_21480_) {
            @Override
            public boolean isStableDestination(BlockPos p_26439_) {
                return true;
            }

            @Override
            protected boolean canMoveDirectly(Vec3 p_262585_, Vec3 p_262682_) {
                return true;
            }

            @Override
            public void tick() {
                super.tick();

                if (this.path == null) {
                    this.mob.setXxa(0.0F);
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    this.mob.setDeltaMovement(Vec3.ZERO);
                }
            }
        };
        navigation.setCanOpenDoors(true);
        navigation.setCanPassDoors(true);
        navigation.setCanFloat(true);

        return navigation;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new StandRushAttackGoal(this, 10.0D, 2, true));
        this.goalSelector.addGoal(5, new AutoDoorGoal(this));
        this.goalSelector.addGoal(5, new AutoFenceGateGoal(this));
        this.goalSelector.addGoal(10, new FollowStandOwnerGoal(this, 5.0D, 2.5F, 0.1F));

        this.targetSelector.addGoal(1, new StandOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new MultipleAttackableTargetsGoal<>(this, LivingEntity.class, false, livingEntity -> {
            return this.getOwner() != null && !this.getOwner().isCreative() && livingEntity instanceof Enemy;
        }));
    }

    @Override
    public boolean equals(Object p_20245_) {
        if (p_20245_ instanceof Stand stand && stand.getOwner() == this.getOwner()) {
            return true;
        }

        return super.equals(p_20245_);
    }

    @Override
    public boolean isAlive() {
        return this.getOwner() != null && super.isAlive();
    }

    @Override
    public float getPathfindingMalus(BlockPathTypes p_21440_) {
        return 0.0F;
    }

    @Override
    public float getFlyingSpeed() {
        var fly = this.getOwner().getAbilities().getFlyingSpeed();
        return this.isSprinting() ? fly * 2.0F : fly;
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FLYING_SPEED, 0.6D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.4D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE).build();
    }

    @Override
    public boolean isNoAi() {
        return this.isControlledByStandOwner() || super.isNoAi();
    }

    @Override
    public void tick() {
        if (this.getOwner() == null || !this.getOwner().isAlive() || this.getOwner().isSpectator()) {
            this.remove(RemovalReason.DISCARDED);
        }

        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;

        this.targets.removeIf(livingEntity -> (livingEntity instanceof Mob mob && mob.getTarget() != this.getOwner()) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) || livingEntity == this.getOwner() || this.getOwner() == null || !livingEntity.isAlive() || !this.getOwner().closerThan(livingEntity, this.maxMovableDistanceFromPlayer()));

        if (!this.isFollowingOwner()) {
            this.setNoGravity(true);
        } else if (!this.isAggressive()) {
            this.imitateOwner();
        }

        this.holdOwnerTick();
        this.followOwnerTick();
        this.updateSwingTime();

        if (this.getY() < this.level().dimensionType().minY()) {
            this.moveTo(this.getOwner().position().add(0.2D, 0.2D, 0.2D));
        }
    }

    @Override
    public void aiStep() {
        if (this.isControlledByStandOwner()) {
            this.lerpHeadSteps = 0;
        }

        super.aiStep();

        var aabb = this.getBoundingBox().inflate(5.0, 2.5, 5.0);
        var list = this.level().getEntities(this, aabb);
        var list1 = Lists.<Entity>newArrayList();

        for (var entity : list) {
            if (entity.getType() == EntityType.EXPERIENCE_ORB) {
                list1.add(entity);
            } else if (!entity.isRemoved() && this.getOwner() != null) {
                entity.playerTouch(this.getOwner());
            }
        }

        if (!list1.isEmpty() && this.getOwner() != null) {
            Util.getRandom(list1, this.random).playerTouch(this.getOwner());
        }

        if (this.isControlledByStandOwner()) {
            this.yya = 0.0F;
            int j = 0;
            if (this.isShiftKeyDown()) {
                --j;
            }

            if (this.jumping) {
                ++j;
            }

            if (j != 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, j * this.getFlyingSpeed(), 0.0D));
            }
        }
    }

    @Nullable
    @Override
    public PlayerTeam getTeam() {
        return this.getOwner() == null ? super.getTeam() : this.getOwner().getTeam();
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean canPickUpLoot() {
        return this.isAlive() && this.getOwner() != null;
    }

    @Override
    public void take(Entity p_21030_, int p_21031_) {
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.take(p_21030_, p_21031_);
        }
    }

    @Override
    public void onItemPickup(ItemEntity p_21054_) {
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.onItemPickup(p_21054_);
            return;
        }

        super.onItemPickup(p_21054_);
    }

    @Override
    public boolean canTakeItem(ItemStack p_21522_) {
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            return serverPlayer.canTakeItem(p_21522_);
        }

        return false;
    }

    @Override
    public ItemStack equipItemIfPossible(ItemStack p_255842_) {
        return p_255842_;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isControlledByStandOwner() || super.isControlledByLocalInstance();
    }

    protected void followOwnerTick() {
        this.hurtTime = this.getOwner().hurtTime;
        this.hurtDuration = this.getOwner().hurtDuration;
        this.fallFlyTicks = this.getOwner().getFallFlyingTicks();
        this.setSharedFlag(FLAG_FALL_FLYING, this.getOwner().isFallFlying());
    }

    @Override
    public float getHurtDir() {
        return this.getOwner().getHurtDir();
    }

    protected void imitateOwner() {
        if (!this.swinging) {
            this.swinging = this.getOwner().swinging;
            this.swingTime = this.getOwner().swingTime;
            this.swingingArm = this.getOwner().swingingArm;
            this.attackAnim = this.getOwner().attackAnim;
        }

        this.setPose(this.getOwner().getPose());
    }

    public void startHoldingOwner() {
        if (this.isHoldingOwner() || !this.isControlledByStandOwner()) {
            return;
        }

        this.holdingOwner = true;
        this.getOwner().setNoGravity(true);
        this.sendHoldInfoToClient();

        var pos = this.getOwner().position();
        this.absMoveTo(pos.x, pos.y, pos.z);
    }

    public void stopHoldingOwner() {
        this.holdingOwner = false;
        this.getOwner().setNoGravity(false);
        this.sendHoldInfoToClient();
    }

    protected final void sendHoldInfoToClient() {
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToClient(new HoldOrReleaseStandOwnerNotify(this, this.isHoldingOwner()), serverPlayer);
        }
    }

    protected void holdOwnerTick() {
        if (!this.isHoldingOwner()) {
            return;
        }

        var pos = this.position().add(MthH.behindVector(this).scale(0.15D));
        this.getOwner().absMoveTo(pos.x, pos.y, pos.z);
    }

    public boolean isHoldingOwner() {
        return this.holdingOwner;
    }

    @Override
    protected boolean canAddPassenger(Entity p_20354_) {
        return false;
    }

    @Override
    protected boolean canRide(Entity p_20339_) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return false;
    }

    public boolean isFollowingOwner() {
        return this.getOwner() != null && this.mode == StandOperationMode.AI;
    }

    @Override
    public boolean canBeLeashed(Player p_21418_) {
        return false;
    }

    @Override
    public void setLeashedTo(Entity p_21464_, boolean p_21465_) {
    }

    @Override
    protected void tickLeash() {
    }

    @Override
    public void dropLeash(boolean p_21456_, boolean p_21457_) {
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    public double getAttributeValue(Attribute p_21134_) {
        if (this.isControlledByStandOwner() && this.getOwner().getAttributes().hasAttribute(p_21134_)) {
            return this.getOwner().getAttributeValue(p_21134_);
        }

        return super.getAttributeValue(p_21134_);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21467_) {
        return this.isControlledByStandOwner() ? this.getOwner().getItemBySlot(p_21467_) : ItemStack.EMPTY;
    }

    public boolean isControlledByStandOwner() {
        return this.getOwner() != null && this.mode == StandOperationMode.OWNER;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!(entity instanceof LivingEntity) || !this.canAttack((LivingEntity) entity)) {
            return false;
        }

        entity.invulnerableTime = 0;

        if (entity instanceof LivingEntityInvoker invoker) {
            ((LivingEntity) entity).setLastHurtByPlayer(this.getOwner());
            invoker.setLastHurtByPlayerTime(100);
        }

        boolean flag = super.doHurtTarget(entity);
        if (!flag && entity instanceof LivingEntityInvoker invoker) {
            ((LivingEntity) entity).setLastHurtByPlayer(null);
            invoker.setLastHurtByPlayerTime(0);
        }

        if (this.level() instanceof ServerLevel serverLevel && flag) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), PUNCH.get(), this.getSoundSource(), 0.5F, 1.0F);
            serverLevel.getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 4));
        }

        return flag;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Nullable
    @Override
    public Player getOwner() {
        return this.cachedOwner != null ? this.cachedOwner : (this.cachedOwner = (Player) OwnableEntity.super.getOwner());
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return true;
    }

    @Override
    public boolean isInvisibleTo(Player p_20178_) {
        return this.getOwner() != p_20178_ || super.isInvisibleTo(p_20178_);
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    public StandOperationMode getMode() {
        return this.mode;
    }

    public void toggleMode() {
        this.toggleMode(this.mode.toggle());
    }

    public void toggleMode(StandOperationMode mode) {
        if (this.mode == StandOperationMode.AI && this.getOwner().isPassenger()) {
            return;
        }

        this.mode = mode;

        if (!this.level().isClientSide && this.getOwner() instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToDimension(new StandOperationModeToggleNotify(this, this.mode), serverPlayer);
            serverPlayer.xxa = 0.0F;
            serverPlayer.yya = 0.0F;
            serverPlayer.zza = 0.0F;
            serverPlayer.setDeltaMovement(Vec3.ZERO);

            this.setDeltaMovement(Vec3.ZERO);

            switch (this.mode) {
                case AI -> this.setToAI();
                case OWNER -> this.setToOwner();
            }
        }
    }

    protected void setToAI() {
        this.stopHoldingOwner();

        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetCameraPacket(this.getOwner()));
        }
    }

    protected void setToOwner() {
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetCameraPacket(this));
            serverPlayer.stopFallFlying();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setMode(StandOperationMode mode) {
        this.mode = mode;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.level().isClientSide && this.getOwner() instanceof PlayerInvoker invoker) {
            invoker.standUp(this);
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (!this.level().isClientSide && this.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetCameraPacket(this.getOwner()));
        }

        this.stopHoldingOwner();
    }

    @Override
    public boolean canAttack(LivingEntity p_21171_) {
        if (this.getOwner() == null) {
            return false;
        }

        if (p_21171_ instanceof Player player && !this.getOwner().canHarmPlayer(player)) {
            return false;
        }

        return p_21171_ != this.getOwner() && super.canAttack(p_21171_);
    }

    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(2.0D);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.ownerUUID);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {
        this.ownerUUID = friendlyByteBuf.readUUID();
    }

    @Override
    public void addTarget(LivingEntity entity) {
        if (entity == this.getOwner()) {
            return;
        }

        this.targets.add(entity);
    }

    @Nullable
    @Override
    public LivingEntity getNearestTarget() {
        return this.level().getNearestEntity(this.targets.stream().toList(), TargetingConditions.forCombat().range(this.maxMovableDistanceFromPlayer()).selector(livingEntity -> livingEntity instanceof Mob mob && mob.getTarget() == this.getOwner() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) && this.canAttack(livingEntity)), this.getOwner(), this.getOwner().getX(), this.getOwner().getEyeY(), this.getOwner().getZ());
    }

    @Override
    public Set<LivingEntity> getTargets() {
        return Collections.unmodifiableSet(this.targets);
    }

    @Override
    public boolean isTargetEmpty() {
        return this.targets.isEmpty();
    }

    public enum StandOperationMode {
        AI,
        OWNER;

        public StandOperationMode toggle() {
            return this == AI ? OWNER : AI;
        }
    }
}