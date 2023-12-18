package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.EbaUtl;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class ModelEvent<T> extends PayloadEvent<T> {

	public enum ModelAction {
		CREATE("Creazione"),
		READ("Lettura"),
		SELECT("Selezione"),
		UPDATE("Modifica"),
		DELETE("Cancellazione");

		ModelAction(String descr) {
			this.descr = descr;
		}

		private String descr;

		public String getDescr() {
			return descr;
		}

		public static ModelAction[] readActions() {
			return new ModelAction[]{ModelAction.READ, ModelAction.SELECT};
		}

		public static ModelAction[] writeActions() {
			return new ModelAction[]{ModelAction.CREATE, ModelAction.UPDATE, ModelAction.DELETE};
		}
	}

	public static Predicate<BaseEvent> withAction(ModelAction... actions) {
		EbaUtl.allNotNull((Object[]) actions);
		return event -> {
			ModelEvent<?> modelEvent = EbaUtl.cast(event, ModelEvent.class);
			return Arrays.stream(actions).anyMatch(action -> Objects.equals(action, modelEvent.getAction()));
		};
	}

	public static Predicate<BaseEvent> written() {
		return withAction(ModelAction.writeActions());
	}

	protected final ModelAction action;

	public ModelEvent(Object source, T payload, ModelAction action) {
		super(source, payload);
		this.action = action;
	}

	public ModelEvent(Object source, Class<T> type, T payload, ModelAction action) {
		super(source, type, payload);
		this.action = action;
	}

	public ModelAction getAction() {
		return action;
	}

}
