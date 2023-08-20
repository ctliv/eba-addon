package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.EbaUtl;

import java.util.function.Predicate;

public class CancellableEvent extends BaseEvent {

	public static Predicate<BaseEvent> cancelled() {
		return event -> EbaUtl.cast(event, CancellableEvent.class).isCancelled();
	}

	protected boolean cancel = false;

	public CancellableEvent(Object source) {
		super(source);
	}

	public void cancel() {
		cancel = true;
	}

	public boolean isCancelled() {
		return cancel;
	}

}
