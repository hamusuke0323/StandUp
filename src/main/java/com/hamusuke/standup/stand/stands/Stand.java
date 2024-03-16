package com.hamusuke.standup.stand.stands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hamusuke.standup.invoker.LivingEntityInvoker;
import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.s2c.HoldOrReleaseStandOwnerNotify;
import com.hamusuke.standup.network.packet.s2c.StandOperationModeToggleNotify;
import com.hamusuke.standup.stand.MultipleTarget;
import com.hamusuke.standup.stand.ai.goal.AutoDoorGoal;
import com.hamusuke.standup.stand.ai.goal.AutoFenceGateGoal;
import com.hamusuke.standup.stand.ai.goal.FollowStandOwnerGoal;
import com.hamusuke.standup.stand.card.StandCard;
import com.hamusuke.standup.util.MthH;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.*;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.hamusuke.standup.registry.RegisteredEntities.SLIM_STAND_TYPE;
import static com.hamusuke.standup.registry.RegisteredEntities.STAND_TYPE;
import static com.hamusuke.standup.registry.RegisteredMenus.CARD_MENU;
import static com.hamusuke.standup.registry.RegisteredSoundEvents.PUNCH;

public class Stand extends PathfinderMob implements MenuProvider, MultipleTarget, OwnableEntity, IEntityAdditionalSpawnData {
    protected final Player owner;
    protected final StandCard standCard;
    protected StandOperationMode mode = StandOperationMode.AI;
    protected final Set<LivingEntity> targets = Sets.newHashSet();
    protected boolean holdingOwner;

    public Stand(Level level, Player owner, boolean slim, StandCard standCard) {
        this(slim ? SLIM_STAND_TYPE.get() : STAND_TYPE.get(), level, owner, standCard);
    }

    public Stand(EntityType<? extends Stand> type, Level p_21369_, Player owner, StandCard standCard) {
        super(type, p_21369_);
        this.owner = owner;
        this.standCard = standCard;
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

    public double maxMovableDistanceFromPlayer() {
        return 32.0D;
    }

    public boolean isTooFarAway() {
        return !this.getOwner().position().closerThan(this.position(), this.maxMovableDistanceFromPlayer());
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
        this.goalSelector.addGoal(5, new AutoDoorGoal(this));
        this.goalSelector.addGoal(5, new AutoFenceGateGoal(this));
        this.goalSelector.addGoal(10, new FollowStandOwnerGoal(this, 5.0D, 2.5F, 0.1F));
    }

    @Override
    public boolean removeWhenFarAway(double p_21542_) {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (!this.getOwner().isAlive() || this.getOwner().level().dimension() != this.level().dimension()) {
            this.discard();
        }
    }

    @Override
    public boolean equals(Object p_20245_) {
        if (p_20245_ instanceof Stand stand && stand.getOwner() == this.getOwner()) {
            return true;
        }

        return super.equals(p_20245_);
    }

    @Override
    public float getPathfindingMalus(BlockPathTypes p_21440_) {
        return 0.0F;
    }

    @Override
    public float getFlyingSpeed() {
        var fly = this.getOwner().getAbilities().getFlyingSpeed();
        return this.isSprinting() && this.canSprint() ? fly * 2.0F : fly;
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FLYING_SPEED, 0.6D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.4D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE).build();
    }

    @Override
    public boolean isNoAi() {
        return this.isControlledByStandOwner() || super.isNoAi();
    }

    @Override
    public boolean isAlive() {
        return this.getOwner().level().dimension() == this.level().dimension() && super.isAlive();
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return null;
    }

    @Override
    public void tick() {
        if (!this.isAlive() || !this.getOwner().isAlive() || this.getOwner().isSpectator()) {
            this.discard();
        }

        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;

        this.targets.removeIf(livingEntity -> (livingEntity instanceof Mob mob && mob.getTarget() != this.getOwner()) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) || livingEntity == this.getOwner() || this.getOwner() == null || !livingEntity.isAlive() || !this.getOwner().closerThan(livingEntity, this.maxMovableDistanceFromPlayer()));

        if (!this.isFollowingOwner()) {
            this.setNoGravity(true);
        }

        if (!this.isAggressive()) {
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
            } else if (!entity.isRemoved()) {
                entity.playerTouch(this.getOwner());
            }
        }

        if (!list1.isEmpty()) {
            Util.getRandom(list1, this.random).playerTouch(this.getOwner());
        }
    }

    @Nullable
    @Override
    public PlayerTeam getTeam() {
        return this.getOwner().getTeam();
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean canPickUpLoot() {
        return this.isAlive();
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
        return (this.getOwner() instanceof LocalPlayer && this.isControlledByStandOwner()) || super.isControlledByLocalInstance();
    }

    protected void followOwnerTick() {
        this.setPose(this.getOwner().getPose());
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
    }

    public void startHoldingOwner() {
        if (this.isHoldingOwner() || this.getOwner().isPassenger()) {
            return;
        }

        if (!this.level().isClientSide && !this.isControlledByStandOwner()) {
            this.toggleMode(StandOperationMode.OWNER);
        }

        this.holdingOwner = true;
        this.getOwner().setNoGravity(true);
        this.sendHoldInfoToClient();

        var pos = this.getOwner().position();
        this.absMoveTo(pos.x, pos.y, pos.z);
        this.setDeltaMovement(Vec3.ZERO);
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
        return !p_20122_.is(DamageTypes.GENERIC_KILL);
    }

    public boolean isFollowingOwner() {
        return this.mode == StandOperationMode.AI;
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
    public boolean onGround() {
        return false;
    }

    @Override
    public double getAttributeValue(Attribute p_21134_) {
        if (this.isControlledByStandOwner() && this.getOwner().getAttributes().hasAttribute(p_21134_)) {
            return this.getOwner().getAttributeValue(p_21134_);
        }

        if (p_21134_.equals(Attributes.ATTACK_DAMAGE)) {
            return this.getAttackDamage();
        }

        return super.getAttributeValue(p_21134_);
    }

    public double getAttackDamage() {
        return 2.0D;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21467_) {
        return this.isControlledByStandOwner() ? this.getOwner().getItemBySlot(p_21467_) : ItemStack.EMPTY;
    }

    public boolean isControlledByStandOwner() {
        return this.mode == StandOperationMode.OWNER;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!(entity instanceof LivingEntity) || !this.canAttack((LivingEntity) entity)) {
            return false;
        }

        if (entity instanceof LivingEntityInvoker invoker) {
            ((LivingEntity) entity).setLastHurtByPlayer(this.getOwner());
            invoker.setLastHurtByPlayerTime(100);
        }

        boolean flag = super.doHurtTarget(entity);
        if (!flag && entity instanceof LivingEntityInvoker invoker) {
            ((LivingEntity) entity).setLastHurtByPlayer(null);
            invoker.setLastHurtByPlayerTime(0);
        }

        if (this.level() instanceof ServerLevel serverLevel && flag && this.shouldPlayPunchSound()) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), this.getPunchSound(), this.getSoundSource(), 0.5F, 1.0F);
            serverLevel.getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 4));
        }

        return flag;
    }

    public double getFollowRange() {
        return 10.0D;
    }

    public SoundEvent getPunchSound() {
        return PUNCH.get();
    }

    public boolean shouldPlayPunchSound() {
        return true;
    }

    @NotNull
    @Override
    public UUID getOwnerUUID() {
        return this.owner.getUUID();
    }

    @NotNull
    @Override
    public Player getOwner() {
        return this.owner;
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
        if (p_20178_ instanceof PlayerInvoker invoker && p_20178_ != this.getOwner()) {
            return !invoker.hasStandAbility();
        }

        return false;
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public void push(Entity p_21294_) {
    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {
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
            NetworkManager.sendToClient(new StandOperationModeToggleNotify(this, this.mode), serverPlayer);
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

        if (this.mode == StandOperationMode.AI) {
            this.getOwner().setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.level().isClientSide) {
            this.onStandUp();
        }
    }

    protected void onStandUp() {
        PlayerInvoker.invoker(this.getOwner()).standUp(this);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (!this.level().isClientSide) {
            this.onStandDown();
        }
    }

    protected void onStandDown() {
        if (this.getOwner() instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetCameraPacket(this.getOwner()));
        }

        this.stopHoldingOwner();
    }

    @Override
    public boolean canAttack(LivingEntity p_21171_) {
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
    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    public boolean skipAttackInteraction(Entity p_20357_) {
        return p_20357_ instanceof Player player && player == this.getOwner();
    }

    @Override
    public InteractionResult interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_) {
        if (p_19980_ != this.getOwner()) {
            return InteractionResult.PASS;
        }

        p_19980_.openMenu(this);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    public void onInteractAtBlock(BlockHitResult result, boolean checkReach) {
        if (this.level().isClientSide) {
            return;
        }

        if (!checkReach) {
            this.onInteractAtBlock(result);
        } else if (this.getOwner().canReach(result.getBlockPos(), 0.0D)) {
            this.onInteractAtBlock(result);
        }
    }

    public void onInteractAtBlock(BlockHitResult result) {
    }

    public void onInteractAt(EntityHitResult result, boolean checkReach) {
        if (this.level().isClientSide) {
            return;
        }

        if (!checkReach) {
            this.onInteractAt(result);
        } else if (this.getOwner().canReach(result.getLocation(), 0.0D)) {
            this.onInteractAt(result);
        }
    }

    public void onInteractAt(EntityHitResult result) {
    }

    public void onInteractAtAir() {
    }

    public StandCard getStandCard() {
        return this.standCard;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return this.getOwner() == player ? CARD_MENU.get().create(i, inventory) : null;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.getOwnerUUID());
        friendlyByteBuf.writeResourceLocation(this.standCard.getCardId());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {
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
