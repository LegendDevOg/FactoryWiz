package com.crabcode.factory.entities;

import com.crabcode.factory.reflect.FieldMatcher;
import com.crabcode.factory.util.Logger;
import com.crabcode.factory.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

import static com.crabcode.factory.reflect.Reflection.findField;
import static com.crabcode.factory.util.MinecraftVersion.findMcClass;
import static java.util.stream.Collectors.toList;

public class FakeEntityAPI {

    static final int CLUSTER_COUNT = 50;
    private static FakeEntityAPI INSTANCE;
    private static final long ONE_MILLIS = 1000000;
    private static final FakeEntity[] EMPTY_ENTITY_ARRAY = new FakeEntity[0];
    private static final AtomicInteger ENTITY_ID = new AtomicInteger(Integer.MAX_VALUE / 2);

    private static boolean setListeners;
    private FakeEntity[] snapshot = EMPTY_ENTITY_ARRAY;
    private final Queue<FakeEntity> toSpawn = new ConcurrentLinkedQueue<>();
    private final Queue<FakeEntity> toRemove = new ConcurrentLinkedQueue<>();
    private final Map<Integer, FakeEntity> entities = new HashMap<>();

    private final Object actionLock = new Object();
    private final Queue<Runnable> actionQueue = new ConcurrentLinkedQueue<>();

    private Player[] playerCache = new Player[0];

    FakeEntityAPI() {

        long waitTime = TimeUnit.MILLISECONDS.toNanos(50);
        Thread thread = new Thread(() -> {

            while (true) {

                try {
                    playerCache = Bukkit.getOnlinePlayers().toArray(new Player[0]);
                } catch (Throwable t) {
                    Logger.severe(t);
                }

                long start = System.nanoTime();
                this.tick();
                // Wait the time minus the elapsed time
                LongSupplier wait = () -> waitTime - (System.nanoTime() - start);
                if (wait.getAsLong() >= ONE_MILLIS) { // Avoid synchronization if we don't have to

                    synchronized (actionLock) {

                        do {

                            this.tickActions();
                            long waitMs = TimeUnit.NANOSECONDS.toMillis(wait.getAsLong());
                            if (waitMs > 0) {

                                try {
                                    actionLock.wait(waitMs);
                                } catch (InterruptedException e) {
                                    Logger.severe(e);
                                }
                            } else {
                                break;
                            }
                        } while (wait.getAsLong() >= ONE_MILLIS);
                    }
                }
            }
        });

        thread.setName("Fake Entities");
        // Start it in 50ms
        Scheduler.later(thread::start, TimeUnit.MILLISECONDS.toMillis(50));


        setListeners = true;
    }

    public FakeEntity spawn(UUID uuid, Location location) {
        FakeEntity entity = new FakeEntity(ENTITY_ID.incrementAndGet(), uuid, location);
        this.toSpawn.add(entity);
        return entity;
    }

    private void tick() {
        this.tickActions();
        boolean refreshSnapshot = !toSpawn.isEmpty();
        FakeEntity tAdd = null;
        while ((tAdd = toSpawn.poll()) != null) {
            entities.put(tAdd.getEntityID(), tAdd);
        }

        entities.values().forEach(fe -> {
            if (fe.gameplayTick0(this.playerCache)) {
                toRemove.add(fe);
            }
        });

        entities.values().forEach(fe -> fe.renderTick0(this.playerCache));

        refreshSnapshot |= !toRemove.isEmpty();
        FakeEntity tRemove = null;
        while ((tRemove = toRemove.poll()) != null) {
            if (entities.remove(tRemove.getEntityID()) != null) {
                tRemove.despawn();
            }
        }

        if (refreshSnapshot) {
            this.snapshot = this.entities.values().toArray(EMPTY_ENTITY_ARRAY);
        }
    }

    private void tickActions() {

        Runnable action;
        while ((action = actionQueue.poll()) != null) {

            try {
                action.run();
            } catch (Throwable e) {
                Logger.severe(e, "Exception caught while running action on Fake Entity");
            }
        }
    }

    public IFakeEntity get(int entityId) {
        return entities.get(entityId);
    }

    public List<IFakeEntity> getAll(Predicate<IFakeEntity> test) {
        return Arrays.stream(snapshot).filter(test).collect(toList());
    }

    public void forEach(Consumer<IFakeEntity> test) {
        Arrays.stream(snapshot).forEach(test);
    }

    //@DynamicIgnore
    public static class FakeEntityAPIEvents implements Listener {
        @EventHandler
        public void onPlayerSpawn(PlayerJoinEvent event) {
            SpawnBuffs.update(event.getPlayer().getUniqueId(), 1500);
            /*FakeEntityAPI.get().forEach(ife -> {
                EntityRenderer render = ife.getRenderer();
                if (render != null) {
                    render.resetRenderedFor(event.getPlayer());
                }
            });*/
        }

        @EventHandler
        public void onPlayerReSpawn(PlayerRespawnEvent event) {
            SpawnBuffs.update(event.getPlayer().getUniqueId(), 0);
            /*FakeEntityAPI.get().forEach(ife -> {
                EntityRenderer render = ife.getRenderer();
                if (render != null) {
                    render.resetRenderedFor(event.getPlayer());
                }
            });*/
        }

        @EventHandler
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            SpawnBuffs.update(event.getPlayer().getUniqueId(), 0);
            /*FakeEntityAPI.get().forEach(ife -> {
                EntityRenderer render = ife.getRenderer();
                if (render != null) {
                    render.resetRenderedFor(event.getPlayer());
                }
            });*/
        }
    }

    public static FakeEntityAPI get() {
        return INSTANCE;
    }

    static void remove(FakeEntity entity) {
        if (INSTANCE != null) {
            INSTANCE.toRemove.add(entity);
        }
    }

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new FakeEntityAPI();
            //Registry.populateInstances(INSTANCE);
        }
    }
}

