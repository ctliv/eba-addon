package io.github.ctliv.eventbus.bus;

import java.util.Set;

public interface EventBusAwareBus {

    Set<Object> getSubscriberSet();

    default int count() { return getSubscriberSet().size(); }

}
