package com.crabcode.factory.entities;

import org.bukkit.entity.Player;

public abstract class EntityBehavior {

    IFakeEntity attached;
    boolean remove;

    /**
     * Called when this EntityBehavior gets attached to a FEntity.
     */
    public void onAttach() {
    }


    /**
     * Called when this EntityBehavior gets detached from a FEntity.
     */
    public void onDetach() {
    }

    /**
     * Gets called when the entity gets ticked.
     */
    public void tick() {
    }

    /**
     * Gets called when the entity gets ticked.
     */
    public void tickFor(Player[] players) {
    }

    /**
     * Use tick0 to appease marks eyes.
     * @return remove Whether or not to remove this behavior.
     */
    boolean tick0(Player[] players) {
        tickFor(players);
        tick();
        return remove;
    }

    public IFakeEntity getAttached() {
        return this.attached;
    }
}
