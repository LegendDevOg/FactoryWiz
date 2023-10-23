package com.crabcode.factory.entities.entity;

import com.crabcode.factory.entities.EntityRenderer;

public interface LivingEntityRenderer extends EntityRenderer {

    public LivingEntityRenderer setHandActive(boolean handActive);

    public LivingEntityRenderer setHealth(float health);

    public LivingEntityRenderer setPotionEffectColor(int color);

    public LivingEntityRenderer setArrowCount(int arrows);

    //1.9+
    public LivingEntityRenderer setActiveHand(ActiveHand hand);

    //1.13+
    public LivingEntityRenderer setInRiptideSpinAttack(boolean riptideSpinAttack);

    public boolean isHandActive();

    public float getHealth();

    public int getActivePotionEffectColor();

    public int getArrowCount();

    //1.9+
    public ActiveHand getActiveHand();

    //1.13+
    public boolean isInRiptideSpinAttack();

    public static enum ActiveHand {
        MAIN, OFF;
    }
}
