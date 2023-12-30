package io.github.ctliv.eventbus.bus;

import com.google.common.collect.MapMaker;
import com.google.common.eventbus.EventBus;
import io.github.ctliv.eventbus.EventBusAwareScope;
import io.github.ctliv.eventbus.event.BaseEvent;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class EventBusAwareSyncBus extends EventBus implements EventBusAwareBus {

    public EventBusAwareSyncBus(EventBusAwareScope scope) {
        this.scope = scope;
    }

    private final ConcurrentMap<Object, Object> map = new MapMaker().weakKeys().makeMap();

    protected EventBusAwareScope scope;

    @Override
    public void post(Object event) {
        Objects.requireNonNull(event);
        if (BaseEvent.class.isAssignableFrom(event.getClass())) {
            ((BaseEvent) event).setScope(scope);
        }
        super.post(event);
        if (BaseEvent.class.isAssignableFrom(event.getClass())) {
            ((BaseEvent) event).getFollowers().forEach(this::post);
        }
    }

    @Override
    public void register(Object object) {
        map.put(object, "");
        super.register(object);
    }

    @Override
    public void unregister(Object object) {
        super.unregister(object);
        map.remove(object);
    }

    @Override
    public Set<Object> getSubscriberSet() {
        return map.keySet();
    }

}
