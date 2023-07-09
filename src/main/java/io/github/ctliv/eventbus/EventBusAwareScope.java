package io.github.ctliv.eventbus;

import com.google.common.collect.MapMaker;
import com.google.common.eventbus.EventBus;
import com.vaadin.flow.component.AttachNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachNotifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import io.github.ctliv.eventbus.bus.EventBusAwareAsyncBus;
import io.github.ctliv.eventbus.bus.EventBusAwareBus;
import io.github.ctliv.eventbus.bus.EventBusAwareSyncBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public enum EventBusAwareScope {
    GLOBAL,
    GLOBAL_ASYNC {
        @Override
        public boolean isAsync() {
            return true;
        }
    },
    VSESSION {
        @Override
        public Object getKey() {
            return VaadinSession.getCurrent();
        }

        @Override
        public synchronized void remove(Object key) {
            super.remove(key);
            ((VaadinSession) key).getUIs().forEach(VUI::remove);
        }
    },
    VUI {
        @Override
        public Object getKey() {
            return UI.getCurrent();
        }

        @Override
        public synchronized EventBusAwareBus create(Object key) {
            if (key != null) ((UI) key).addDetachListener(event -> remove(event.getUI()));
            return super.create(key);
        }
    },
    COMPONENT {
        @Override
        public Object getKey() {
            return null;
        }

        @Override
        public boolean allowsRegister() {
            return false;
        }
    };

    private static final Logger log = LoggerFactory.getLogger(EventBusAwareScope.class);

    public static String objHash(Object obj) {
        if (obj == null) return "null";
        return "[" + obj.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(obj)) + "]";
    }

    public static void register(EventBusAware eba) {
        Objects.requireNonNull(eba);
        Arrays.stream(EventBusAwareScope.values())
                .filter(EventBusAwareScope::allowsRegister)
                .forEach(scope -> scope.getBus(true).ifPresent(bus -> {
                    try {
                        bus.register(eba);
                    } catch (IllegalArgumentException ignored) {
                        log.debug("Bus {} is unable to register {}", scope.name(), objHash(eba));
                    }
                }));
        if (eba.ebaListenOnAttachDetach() && Component.class.isAssignableFrom(eba.getClass())) {
            ((AttachNotifier) eba).addAttachListener(event -> EventBusAwareScope.register(eba));
            ((DetachNotifier) eba).addDetachListener(event -> EventBusAwareScope.unregister(eba));
        }
        log.trace("Registered {}", objHash(eba));
    }

    public static void unregister(EventBusAware eba) {
        Objects.requireNonNull(eba);
        Arrays.stream(EventBusAwareScope.values())
                .filter(EventBusAwareScope::allowsRegister)
                .forEach(scope -> scope.getBus(false).ifPresent(bus -> {
                    try {
                        bus.unregister(eba);
                    } catch (IllegalArgumentException ignored) { /* Noop*/ }
                }));
        log.trace("Unregistered {}", objHash(eba));
    }

    private final transient ConcurrentMap<Object, EventBusAwareBus> map = new MapMaker().weakKeys().makeMap();

    public boolean isAsync() {
        return false;
    }

    protected boolean allowsRegister() {
        return true;
    }

    protected Object getKey() {
        return "(default)";
    }

    public Optional<EventBus> getBus() {
        return getBus(true);
    }

    public Optional<EventBus> getBus(boolean createIfMissing) {
        Object key = getKey();
        if (key == null) {
            log.trace("Returning empty {} EventBus", name());
            return Optional.empty();
        }
        return getBus(key, createIfMissing);
    }

    public Optional<EventBus> getBus(Object key) {
        return getBus(key, true);
    }

    public Optional<EventBus> getBus(Object key, boolean createIfMissing) {
        if (key == null) return Optional.empty();
        EventBusAwareBus bus = map.get(key);
        if (bus != null || !createIfMissing) return Optional.ofNullable((EventBus) bus);
        return Optional.ofNullable((EventBus) create(key));
    }

    public synchronized EventBusAwareBus create(Object key) {
        if (key == null) return null;
        EventBusAwareBus bus = map.get(key);
        if (bus == null) {
            bus = isAsync() ? new EventBusAwareAsyncBus(this) : new EventBusAwareSyncBus(this);
            map.put(key, bus);
            log.trace("Created {} EventBus for {}", name(), objHash(key));
        }
        return bus;
    }

    public synchronized void remove(Object key) {
        if (key == null) return;
        EventBusAwareBus bus = map.remove(key);
        if (bus != null) {
            bus.getSubscriberSet().stream().map(EventBusAware.class::cast)
                    .filter(EventBusAware::ebaUnregisterWithContext)
                    .forEach(EventBusAware::ebaUnregister);
            log.trace("Removed {} EventBus for {}", name(), objHash(key));
        }
    }

    public ConcurrentMap<Object, EventBusAwareBus> getMap() {
        return map;
    }

    public Collection<EventBusAwareBus> getBusList() {
        return new ArrayList<>(map.values());
    }

    public int busCount() {
        return map.size();
    }

    public int subscriberCount() {
        return map.values().stream()
                .map(EventBusAwareBus.class::cast)
                .map(EventBusAwareBus::count)
                .mapToInt(Integer::intValue).sum();
    }

}
