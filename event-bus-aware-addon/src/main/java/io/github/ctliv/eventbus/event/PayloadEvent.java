package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.EbaUtl;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class PayloadEvent<T> extends BaseEvent {

    public static Predicate<BaseEvent> withItem(Object... items) {
        if (items == null) return event -> EbaUtl.cast(event, PayloadEvent.class).getItem() == null;
        return event -> {
            PayloadEvent<?> payloadEvent = EbaUtl.cast(event, PayloadEvent.class);
            return Arrays.stream(items).anyMatch(item -> Objects.equals(item, payloadEvent.getItem()));
        };
    }

    public static Predicate<BaseEvent> withType(Class<?>... types) {
        if (types == null) return event -> EbaUtl.cast(event, PayloadEvent.class).getType() == null;
        return event -> {
            PayloadEvent<?> payloadEvent = EbaUtl.cast(event, PayloadEvent.class);
            if (payloadEvent.getType() == null) return Arrays.stream(types).anyMatch(Objects::isNull);
            return Arrays.stream(types).filter(Objects::nonNull)
                    .anyMatch(type -> type.isAssignableFrom(payloadEvent.getType()));
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

/*
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != PayloadEvent.class) return false;
        PayloadEvent<?> other = (PayloadEvent<?>) obj;
        if (payload == null) return other.payload == null;
        if (type != other.type) return false;
        return payload.equals(other.payload);
    }
*/
}
