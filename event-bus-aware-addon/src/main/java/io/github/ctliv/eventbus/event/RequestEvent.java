package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.EbaUtl;

import java.util.HashMap;
import java.util.function.Predicate;

public class RequestEvent<T extends BaseEvent> extends PayloadEvent<T> {

    public static Predicate<BaseEvent> withQuestions() {
        return event -> !EbaUtl.cast(event, RequestEvent.class).getQuestions().isEmpty();
    }

    public static Predicate<BaseEvent> checkPayload(Predicate<BaseEvent> predicate) {
        return event -> predicate.test((BaseEvent) EbaUtl.cast(event, PayloadEvent.class).getItem());
    }

    private final HashMap<Object, String> questions = new HashMap<>();

    public RequestEvent(T event) {
        super(event.getSource(), event);
    }

    public boolean hasQuestions() {
        return !questions.isEmpty();
    }

    public HashMap<Object, String> getQuestions() {
        return questions;
    }

    public void addQuestion(Object source, String message) {
        EbaUtl.notNull(source);
        EbaUtl.notEmpty(message);

        questions.put(source, message);
    }

}
