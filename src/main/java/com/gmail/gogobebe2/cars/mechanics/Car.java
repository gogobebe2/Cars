package com.gmail.gogobebe2.cars.mechanics;

import com.gmail.gogobebe2.cars.Cars;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

class Car {
    private final float MAX_SPEED;
    private final float REVERSE_MAX_SPEED;
    private final float ACCELERATION;
    private final float BREAK_DECELERATION;
    private float speed = 0;
    private boolean isAccelerating = false;
    private boolean isDecelerating = false;
    private CarSteering carSteering;
    private Minecart minecart;
    private Player driver = null; // If this is null it means no driver present.

    Car(float maxSpeed, float reverseMaxSpeed, float acceleration, float breakDeceleration, float turnDisplacement,
        float tiltDisplacement, Player driver) {
        this.MAX_SPEED = maxSpeed;
        this.REVERSE_MAX_SPEED = -reverseMaxSpeed; // Converted to negative since it's going backwards.
        this.ACCELERATION = acceleration;
        this.BREAK_DECELERATION = breakDeceleration;
        this.carSteering = new CarSteering(this, turnDisplacement, tiltDisplacement);
        this.driver = driver; // TODO listener to see if player gets out and if so, set driver to null and set speed to 0.
        this.createMinecart();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cars.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                // TODO Constantly move the car's location in the direction and tilt +speed blocks.
            }
        }, 1, 1);
    }

    void createMinecart() {
        minecart = (Minecart) driver.getWorld().spawnEntity(driver.getLocation(), EntityType.MINECART);
        minecart.setMaxSpeed(MAX_SPEED); // Not sure if this is needed since I'm going to do it myself anyway.
        minecart.setSlowWhenEmpty(true); // Not sure if this is needed since I'm going to do it myself anyway.
        minecart.setPassenger(driver);
    }

    void stopChangingSpeed() { // TODO call this if player stops moving forward.
        isAccelerating = false;
        isDecelerating = false;
    }

    void startAccelerating() { // TODO call this is player moves.
        isAccelerating = true;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cars.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!isAccelerating) {
                    cancel();
                    return;
                }
                speed += ACCELERATION;
                if (speed >= MAX_SPEED) {
                    isAccelerating = false;
                    speed = MAX_SPEED; // Incase it goes over max speed.
                }
            }
        }, 1, 1);
    }

    void pushBreak() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cars.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!isDecelerating) {
                    cancel();
                    return;
                }
                speed -= BREAK_DECELERATION;
                if (speed <= REVERSE_MAX_SPEED) {
                    // Now it is going backwards because negative speed. ('s' is being pressed).
                    isDecelerating = false;
                    speed = REVERSE_MAX_SPEED; // Incase it goes over (below since it's negative) max reverse speed.
                }
            }
        }, 1, 1);
    }

    void letGoBreak() {
        isDecelerating = false;
    }

    float getAcceleration() {
        return ACCELERATION;
    }

    boolean isAccelerating() {
        return isAccelerating;
    }

    float breakDeceleration() {
        return BREAK_DECELERATION;
    }

    float getSpeed() {
        return speed;
    }

    float getMaxSpeed() {
        return MAX_SPEED;
    }

    Minecart getMinecart() {
        return minecart;
    }
}
