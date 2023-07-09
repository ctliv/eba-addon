package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.Utl;

import java.util.HashMap;
import java.util.function.Predicate;

public class RequestEvent<T extends BaseEvent> extends PayloadEvent<T> {

    public static Predicate<BaseEvent> withQuestions() {
        return event -> !Utl.cast(event, RequestEvent.class).getQuestions().isEmpty();
    }

    public static Predicate<BaseEvent> payload(Predicate<BaseEvent> predicate) {
        return event -> predicate.test((BaseEvent) Utl.cast(event, PayloadEvent.class).getItem());
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
        Utl.notNull(source);
        Utl.notEmpty(message);

        questions.put(source, message);
    }

}
