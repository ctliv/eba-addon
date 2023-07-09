package io.github.ctliv.eventbus.listener;

import com.vaadin.flow.server.*;
import io.github.ctliv.eventbus.EventBusAwareScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBusAwareInitListener implements
        VaadinServiceInitListener, SessionInitListener, SessionDestroyListener, UIInitListener {

    private final transient Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(this);
        event.getSource().addSessionDestroyListener(this);
        event.getSource().addUIInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        EventBusAwareScope.VSESSION.create(event.getSession());
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        EventBusAwareScope.VSESSION.remove(event.getSession());
    }

    @Override
    public void uiInit(UIInitEvent event) {
        EventBusAwareScope.VUI.create(event.getUI());
    }

}
