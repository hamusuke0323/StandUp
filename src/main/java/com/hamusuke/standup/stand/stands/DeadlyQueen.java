package com.hamusuke.standup.stand.stands;

import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.s2c.AskBombInfoReq;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.BlockBomb;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb.When;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.EntityBomb;
import com.hamusuke.standup.stand.ai.goal.StandRushAttackGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandOwnerHurtTargetGoal;
import com.hamusuke.standup.stand.card.StandCard;
import com.hamusuke.standup.util.TickTimer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import static com.hamusuke.standup.StandUp.MOD_ID;
import static com.hamusuke.standup.registry.RegisteredItems.IGNITION_SWITCH;
import static com.hamusuke.standup.registry.RegisteredSoundEvents.APPEAR;

public class DeadlyQueen extends Stand {
    protected static final Component RELEASE_BOMB = Component.translatable(MOD_ID + ".stand.release.bomb");
    @Nullable
    protected Bomb bomb;
    protected final TickTimer<DeadlyQueen> bombTimer;
    @Nullable
    protected SheerHeartAttack sheerHeartAttack;

    public DeadlyQueen(Level level, Player owner, boolean slim, StandCard standCardId) {
        super(level, owner, slim, standCardId);
        this.bombTimer = this.createTimer();
    }

    protected TickTimer<DeadlyQueen> createTimer() {
        return new TickTimer<>(this, deadlyQueen -> {
            if (deadlyQueen.bomb != null) {
                deadlyQueen.bomb.explode();
            }
        });
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new StandRushAttackGoal(this, 3, true));
        this.targetSelector.addGoal(1, new StandOwnerHurtTargetGoal(this));
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

        this.bombTimer.tick();
    }

    @Override
    public void onInteractAtBlock(BlockHitResult result) {
        if (this.bomb instanceof BlockBomb blockBomb && blockBomb.getBlockPos().equals(result.getBlockPos())) {
            this.releaseBomb();
            ((ServerPlayer) this.getOwner()).sendSystemMessage(RELEASE_BOMB, true);
            return;
        }

        NetworkManager.sendToClient(new AskBombInfoReq(result), (ServerPlayer) this.getOwner());
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

        NetworkManager.sendToClient(new AskBombInfoReq(result), (ServerPlayer) this.getOwner());
    }

    @Override
    public void onInteractAtAir() {
        if (this.bomb != null && !this.getOwner().isShiftKeyDown() && this.bomb.getExplodeWhen() == When.PUSH_SWITCH) {
            this.bomb.ignite();
        }

        if (!this.getOwner().isShiftKeyDown()) {
            return;
        }

        if (this.sheerHeartAttack == null) {
            this.sheerHeartAttack = new SheerHeartAttack(this);
            this.sheerHeartAttack.setPos(this.position());
            this.level().addFreshEntity(this.sheerHeartAttack);
        } else {
            this.sheerHeartAttack.discard();
            this.sheerHeartAttack = null;
        }
    }

    public TickTimer<DeadlyQueen> getBombTimer() {
        return this.bombTimer;
    }

    public void placeBomb(Bomb bomb) {
        this.bomb = bomb;
        this.giveSwitchToOwner();
    }

    protected void giveSwitchToOwner() {
        if (!this.level().isClientSide && this.bomb != null && this.bomb.getExplodeWhen() == When.PUSH_SWITCH) {
            var switch_ = new ItemStack(IGNITION_SWITCH.get());
            var copied = switch_.copy();
            if (this.getOwner().getInventory().add(switch_)) {
                this.level().playSound(null, this.getOwner().getX(), this.getOwner().getY(), this.getOwner().getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((this.getOwner().getRandom().nextFloat() - this.getOwner().getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                int slot = this.getOwner().getInventory().findSlotMatchingItem(copied);
                if (Inventory.isHotbarSlot(slot)) {
                    this.getOwner().getInventory().selected = slot;
                    ((ServerPlayer) this.getOwner()).connection.send(new ClientboundSetCarriedItemPacket(slot));
                }
                this.getOwner().containerMenu.broadcastChanges();
            } else {
                var e = this.getOwner().drop(switch_, false);
                if (e != null) {
                    e.setNoPickUpDelay();
                    e.setTarget(this.getOwner().getUUID());
                }
            }
        }
    }

    public void igniteBomb() {
        if (this.bomb == null) {
            return;
        }

        this.bomb.ignite();
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
