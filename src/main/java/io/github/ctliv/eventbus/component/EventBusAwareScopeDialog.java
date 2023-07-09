package io.github.ctliv.eventbus.component;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.treegrid.TreeGrid;
import io.github.ctliv.eventbus.EventBusAwareScope;
import io.github.ctliv.eventbus.bus.EventBusAwareBus;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EventBusAwareScopeDialog extends Dialog {

    public static class Envelope {
        private Object obj;

        public static Collection<Envelope> of(Object[] values) {
            if (values == null) return List.of();
            return of(Arrays.stream(values).collect(Collectors.toList()));
        }

        public static Collection<Envelope> of(Collection<Object> objects) {
            if (objects == null) return List.of();
            return objects.stream().map(Envelope::new).collect(Collectors.toList());
        }

        public static Collection<Envelope> of() {
            return List.of();
        }

        public Envelope(Object obj) {
            this.obj = obj;
        }

        public Object get() { return obj; }

        @Override
        public String toString() {
            if (obj == null) {
                return "null";
            } else if (obj instanceof EventBusAwareScope) {
                EventBusAwareScope scope = (EventBusAwareScope) obj;
                return "Scope: " + scope.name() + " (" + (scope.isAsync() ? "Async/" : "") +
                        scope.busCount() + "/" + scope.subscriberCount() + ")";
            } else if (obj instanceof BusDetail) {
                BusDetail detail = (BusDetail) obj;
                return "Bus (Key: " + new Envelope(detail.key) + " )";
            } else if (obj instanceof EventBusAwareBus) {
                EventBusAwareBus bus = (EventBusAwareBus) obj;
                return "Bus: " + EventBusAwareScope.objHash(bus);
            } else if (obj instanceof String) {
                String str = (String) obj;
                return str;
            } else if (obj instanceof Envelope) {
                return obj.toString();
            } else {
                return EventBusAwareScope.objHash(obj);
            }
        }
    }

    public static class BusDetail {
        Object key;
        EventBusAwareBus bus;

        public BusDetail(Object key, EventBusAwareBus bus) {
            this.key = key;
            this.bus = bus;
        }
    }

    public EventBusAwareScopeDialog() {
        setHeaderTitle("EventBusAware Scopes");
        setModal(true);
        setCloseOnOutsideClick(true);
        setCloseOnEsc(true);
        setDraggable(true);
        TreeGrid<Envelope> grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.setItems(Envelope.of(EventBusAwareScope.values()), this::getSubItems);
        grid.removeAllColumns();
        grid.addHierarchyColumn(Envelope::toString).setHeader("Scope > Bus > Subscribers");
        add(grid);
    }

    private Collection<Envelope> getSubItems(Object obj) {
        if (obj == null) {
            return Envelope.of();
        } else if (obj instanceof Envelope) {
            Envelope envelope = (Envelope) obj;
            return getSubItems((envelope).get());
        } else if (obj instanceof EventBusAwareScope) {
            EventBusAwareScope scope = (EventBusAwareScope) obj;
            return scope.getMap().entrySet().stream()
                    .map(o -> new BusDetail(o.getKey(), o.getValue()))
                    .map(Envelope::new)
                    .collect(Collectors.toList());
        } else if (obj instanceof BusDetail) {
            BusDetail detail = (BusDetail) obj;
            if (detail.bus == null) return List.of();
            return Envelope.of(detail.bus.getSubscriberSet());
        } else if (obj instanceof EventBusAwareBus) {
            EventBusAwareBus bus = (EventBusAwareBus) obj;
            return Envelope.of(bus.getSubscriberSet());
        } else {
            return Envelope.of();
        }
    }

}
