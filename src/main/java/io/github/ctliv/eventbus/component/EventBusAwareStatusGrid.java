package io.github.ctliv.eventbus.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.shared.Registration;
import io.github.ctliv.eventbus.EventBusAwareScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EventBusAwareStatusGrid extends Grid<EventBusAwareScope> {

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private transient ScheduledFuture<?> scheduledFuture;

    private boolean attached = false;

    public EventBusAwareStatusGrid() {
        super(EventBusAwareScope.class);
        removeAllColumns();
        addColumn(EventBusAwareScope::name).setHeader("Scope");
        addColumn(EventBusAwareScope::isAsync).setHeader("Async");
        addColumn(EventBusAwareScope::busCount).setHeader("Bus#");
        addColumn(EventBusAwareScope::subscriberCount).setHeader("Subscriber#");
        addItemClickListener(event -> {
            Dialog dialog = new EventBusAwareScopeDialog();
            dialog.setWidth("40%");
            dialog.setHeight("80%");
            dialog.open();
        });

        addAttachListener(attachEvent -> {
            attached = true;
            startUpdate();
        });
        addDetachListener(detachEvent -> {
            stopUpdate();
            attached = false;
        });
    }

    public boolean isAttached() {
        return attached;
    }

    private int intervalSeconds = 3;

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        if (isAttached()) startUpdate();
    }

    public synchronized void updateStatus() {
        try {
            getUI().ifPresent(ui -> ui.access(() -> setItems(EventBusAwareScope.values())));
        } catch (Exception e) {
            log.debug("Error in updating status grid", e);
        }
    }

    private void startUpdate() {
        stopUpdate();
        int interval = getIntervalSeconds();
        if (interval <= 0) return;
        scheduledFuture = executorService.scheduleWithFixedDelay(
                this::updateStatus,
                1,
                interval,
                TimeUnit.SECONDS);
    }

    private void stopUpdate() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            try {
                if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                    log.debug("Awaited termination for 3 seconds, then interrupting");
                }
            } catch (InterruptedException e) {
                log.debug("Error while interrupting update", e);
            }
            scheduledFuture = null;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        startUpdate();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        stopUpdate();
        super.onDetach(detachEvent);
    }
}
