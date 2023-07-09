package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.Utl;

import java.util.HashMap;
import java.util.function.Predicate;

public class QuestionEvent extends BaseEvent {

    public static Predicate<BaseEvent> withQuestions() {
        return event -> !Utl.cast(event, QuestionEvent.class).getQuestions().isEmpty();
    }

    private final HashMap<Object, String> questions = new HashMap<>();

    public QuestionEvent(Object source) {
        super(source);
    }

    public void addQuestion(Object source, String message) {
        Utl.notNull(source);
        Utl.notEmpty(message);

        questions.put(source, message);
    }

    public boolean hasQuestions() {
        return !questions.isEmpty();
    }

    public HashMap<Object, String> getQuestions() {
        return questions;
    }

}
