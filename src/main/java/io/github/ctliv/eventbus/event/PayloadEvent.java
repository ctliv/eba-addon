package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.Utl;

import java.util.Arrays;
import java.util.function.Predicate;

public class PayloadEvent<T> extends BaseEvent {

    public static Predicate<BaseEvent> withItem(Object... items) {
        if (items == null) return event -> Utl.cast(event, PayloadEvent.class).getItem() == null;
        return event -> {
            PayloadEvent<?> payloadEvent = Utl.cast(event, PayloadEvent.class);
            return Arrays.stream(items).anyMatch(item -> Utl.eq(item, payloadEvent.getItem()));
        };
    }

    public static Predicate<BaseEvent> withType(Class<?>... types) {
        if (types == null) return event -> Utl.cast(event, PayloadEvent.class).getType() == null;
        return event -> {
            PayloadEvent<?> payloadEvent = Utl.cast(event, PayloadEvent.class);
            return Arrays.stream(types).anyMatch(type -> Utl.eq(type, payloadEvent.getType()));
        };
    }

    protected final T payload;
    protected final Class<?> type;

    public PayloadEvent(Object source, Class<T> type, T payload) {
        super(source);
        this.payload = payload;
        this.type = type;
    }

    public PayloadEvent(Object source, T payload) {
        super(source);
        this.payload = payload;
        this.type = payload == null ? null : payload.getClass();
    }

    public T getItem() {
        return payload;
    }

    public Class<?> getType() {
        if (type != null) return type;
        if (payload == null) return null;
        return payload.getClass();
    }

}
