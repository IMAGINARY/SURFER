package de.mfo.surfer.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleMonitor {

    private static final Logger logger = LoggerFactory.getLogger( IdleMonitor.class );

    private final SimpleObjectProperty<Duration> idleTimeOut;
    private Timeline idleTimeline;
    private final SimpleObjectProperty<Runnable> notifier;

    private final EventHandler<Event> userEventHandler ;

    public IdleMonitor(Duration idleTimeOut, Runnable notifier, boolean startMonitoring) {
        this.idleTimeOut = new SimpleObjectProperty<>(idleTimeOut);
        this.notifier = new SimpleObjectProperty<>(notifier);
        this.idleTimeline = new Timeline(new KeyFrame(idleTimeOut, this::runNotifier));
        this.idleTimeline.setCycleCount(Animation.INDEFINITE);

        this.idleTimeOut.addListener( (o, ov, nv) -> {
            Animation.Status status = this.idleTimeline.getStatus();
            Duration currentTime = this.idleTimeline.getCurrentTime();
            this.idleTimeline.stop();
            this.idleTimeline = new Timeline(new KeyFrame(nv, this::runNotifier));
            this.idleTimeline.setCycleCount(Animation.INDEFINITE);
            if(Animation.Status.RUNNING.equals(status))
                idleTimeline.playFrom(currentTime);
        });

        this.userEventHandler = e -> {
            logger.trace("not idle: {}", e);
            notIdle();
        };

        if (startMonitoring) {
            startMonitoring();
        }
    }

    public IdleMonitor(Duration idleTimeOut, Runnable notifier) {
        this(idleTimeOut, notifier, false);
    }

    public Duration getIdleTimeOut() {
        return this.idleTimeOut.get();
    }

    public void setIdleTimeOut(Duration idleTimeOut) {
        this.idleTimeOut.set(idleTimeOut);
    }

    public SimpleObjectProperty<Duration> idleTimeOutProperty() {
        return this.idleTimeOut;
    }

    public Runnable getNotifier() {
        return notifier.get();
    }

    public void setNotifier(Runnable notifier) {
        this.notifier.set(notifier);
    }

    public ObjectProperty<Runnable> notifierProperty() {
        return this.notifier;
    }

    public void register(Scene scene, EventType<? extends Event> eventType) {
        scene.addEventFilter(eventType, userEventHandler);
    }

    public void register(Node node, EventType<? extends Event> eventType) {
        node.addEventFilter(eventType, userEventHandler);
    }

    public void unregister(Scene scene, EventType<? extends Event> eventType) {
        scene.removeEventFilter(eventType, userEventHandler);
    }

    public void unregister(Node node, EventType<? extends Event> eventType) {
        node.removeEventFilter(eventType, userEventHandler);
    }

    public void notIdle() {
        idleTimeline.jumpTo(Duration.ZERO);
    }

    public void startMonitoring() {
        idleTimeline.playFromStart();
    }

    public void stopMonitoring() {
        idleTimeline.stop();
    }

    private void runNotifier(ActionEvent event) {
        logger.trace("idle for {}: run idle notifier", idleTimeOut.get());
        notifier.get().run();
    }
}
