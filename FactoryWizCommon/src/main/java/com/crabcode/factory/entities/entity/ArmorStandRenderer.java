package com.crabcode.factory.entities.entity;

import com.crabcode.factory.entities.Equipable;
import org.bukkit.util.Vector;

public interface ArmorStandRenderer extends LivingEntityRenderer, Equipable {

    public ArmorStandRenderer setSmall(boolean small);

    public ArmorStandRenderer setArms(boolean arms);

    public ArmorStandRenderer setBaseplate(boolean baseplate);

    public ArmorStandRenderer setMarker(boolean marker);

    public ArmorStandRenderer setHeadRotation(float yaw, float pitch, float roll);

    public ArmorStandRenderer setBodyRotation(float yaw, float pitch, float roll);

    public ArmorStandRenderer setLeftArmRotation(float yaw, float pitch, float roll);

    public ArmorStandRenderer setRightArmRotation(float yaw, float pitch, float roll);

    public ArmorStandRenderer setLeftLegRotation(float yaw, float pitch, float roll);

    public ArmorStandRenderer setRightLegRotation(float yaw, float pitch, float roll);

    public default ArmorStandRenderer setHeadRotation(Vector rotationVector) {
        return this.setHeadRotation((float) rotationVector.getX(), (float) rotationVector.getY(), (float) rotationVector.getZ());
    }

    public default ArmorStandRenderer setBodyRotation(Vector rotationVector) {
        return this.setBodyRotation((float) rotationVector.getX(), (float) rotationVector.getY(), (float) rotationVector.getZ());
    }

    public default ArmorStandRenderer setLeftArmRotation(Vector rotationVector) {
        return this.setLeftArmRotation((float) rotationVector.getX(), (float) rotationVector.getY(), (float) rotationVector.getZ());
    }

    public default ArmorStandRenderer setRightArmRotation(Vector rotationVector) {
        return this.setRightArmRotation((float) rotationVector.getX(), (float) rotationVector.getY(), (float) rotationVector.getZ());
    }

    public default ArmorStandRenderer setLeftLegRotation(Vector rotationVector) {
        return this.setLeftLegRotation((float) rotationVector.getX(), (float) rotationVector.getY(), (float) rotationVector.getZ());
    }

    public default ArmorStandRenderer setRightLegRotation(Vector rotationVector) {
        return this.setRightLegRotation((float) rotationVector.getX(), (float) rotationVector.getY(), (float) rotationVector.getZ());
    }

    public boolean isSmall();

    public boolean hasArms();

    public boolean hasBaseplate();

    public boolean isMarker();

    public Vector getHeadRotation();

    public Vector getBodyRotation();

    public Vector getLeftArmRotation();

    public Vector getRightArmRotation();

    public Vector getLeftLegRotation();

    public Vector getRightLegRotation();

}
