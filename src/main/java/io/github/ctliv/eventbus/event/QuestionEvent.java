package io.github.ctliv.eventbus.event;

import io.github.ctliv.eventbus.util.EbaUtl;

import java.util.HashMap;
import java.util.function.Predicate;

public class QuestionEvent extends BaseEvent {

    public static Predicate<BaseEvent> withQuestions() {
        return event -> !EbaUtl.cast(event, QuestionEvent.class).getQuestions().isEmpty();
    }

    private final HashMap<Object, String> questions = new HashMap<>();

    public QuestionEvent(Object source) {
        super(source);
    }

    public void addQuestion(Object source, String message) {
        EbaUtl.notNull(source);
        EbaUtl.notEmpty(message);

        questions.put(source, message);
    }

    public boolean hasQuestions() {
        return !questions.isEmpty();
    }

    public HashMap<Object, String> getQuestions() {
        return questions;
    }

}
