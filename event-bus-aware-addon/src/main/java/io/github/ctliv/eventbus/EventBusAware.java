package io.github.ctliv.eventbus;

import com.google.common.eventbus.EventBus;

import java.util.Optional;

public interface EventBusAware {

    default Optional<EventBus> ebaGetBus(EventBusAwareScope scope) {
        return scope.getBus();
    }

    default boolean ebaListenOnAttachDetach() {
        return true;
    }

    default boolean ebaUnregisterWithContext() { return true; }

    default void ebaRegister() {
        EventBusAwareScope.register(this);
    }

    default void ebaUnregister() {
        EventBusAwareScope.unregister(this);
    }

    default void ebaAddSubscriber(Object subscriber) {
        EventBusAwareScope.COMPONENT.getBus(this).ifPresent(eventBus -> eventBus.register(subscriber));
    }

    default void ebaRemoveSubscriber(Object subscriber) {
        EventBusAwareScope.COMPONENT.getBus(this).ifPresent(eventBus -> eventBus.unregister(subscriber));
    }

    default void ebaNotifySubscribers(Object obj) {
        EventBusAwareScope.COMPONENT.getBus(this).ifPresent(eventBus -> eventBus.post(obj));
    }

}
