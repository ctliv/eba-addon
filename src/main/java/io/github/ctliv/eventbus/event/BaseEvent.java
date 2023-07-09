package io.github.ctliv.eventbus.event;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import io.github.ctliv.eventbus.util.Utl;
import io.github.ctliv.eventbus.EventBusAwareScope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.EventObject;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class BaseEvent extends EventObject {

    private static final boolean SPRING_SECURITY_DETECTED;

    static {
        String basePackage = "org.springframework.security";
        SPRING_SECURITY_DETECTED =
                Utl.exists(basePackage + ".authentication.AnonymousAuthenticationToken") &&
                Utl.exists(basePackage + ".core.Authentication") &&
                Utl.exists(basePackage + ".core.context.SecurityContextHolder");
    }

    public static <T extends BaseEvent> Predicate<BaseEvent> isInstanceOf(BaseEvent event, Class<T> type) {
        Utl.allNotNull(event, type);
        return baseEvent -> type.isInstance(event);
    }

    public static Predicate<BaseEvent> generatedInCurrentThread() {
        return event -> Thread.currentThread().equals(event.getThread());
    }

    public static Predicate<BaseEvent> fromCurrentUI() {
        return fromUI(UI.getCurrent());
    }

    public static Predicate<BaseEvent> fromUI(UI... uis) {
        return event -> checkIsIn(event.getUi(), uis);
    }

    public static Predicate<BaseEvent> from(Object... objects) {
        return event -> checkIsIn(event.getSource(), objects);
    }

    public static Predicate<BaseEvent> withScope(EventBusAwareScope... scopes) {
        return event -> checkIsIn(event.getScope(), scopes);
    }

    public static Predicate<BaseEvent> withUiScope() {
        return withScope(EventBusAwareScope.VUI);
    }

    @SafeVarargs
    protected static <T> boolean checkIsIn(T val, T... ts) {
        if (ts == null) return val == null;
        if (!ts.getClass().isArray()) return ts.equals(val);
        return Arrays.stream(ts).filter(Objects::nonNull).anyMatch(t -> t.equals(val));
    }

    private EventBusAwareScope scope = null;
    private transient Object user;
    private String username;
    private final UI ui;
    private final transient Thread thread;

    public BaseEvent(Object source) {
        super(source);

        if (SPRING_SECURITY_DETECTED) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                try {
                    this.user = authentication.getPrincipal();
                } catch (Exception ignored) { /* Noop*/ }
                try {
                    this.username = authentication.getName();
                } catch (Exception ignored) { /* Noop*/ }
            }
        }
        this.ui = UI.getCurrent();
        thread = Thread.currentThread();
    }

    public EventBusAwareScope getScope() {
        return scope;
    }

    public void setScope(EventBusAwareScope scope) {
        this.scope = scope;
    }

    public Optional<Object> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public UI getUi() {
        return ui;
    }

    public Thread getThread() {
        return thread;
    }

    public boolean verify(Predicate<BaseEvent> predicate) {
        Utl.notNull(predicate);
        return predicate.test(this);
    }

    public Optional<BaseEvent> check(Predicate<BaseEvent> predicate) {
        return verify(predicate) ? Optional.of(this) : Optional.empty();
    }

    public void exec(Runnable runnable) {
        Utl.notNull(runnable);
        exec(UI.getCurrent(), runnable);
    }

    public void exec(UI ui, Runnable runnable) {
        Utl.notNull(runnable);
//        String caller = LogUtils.getCallerOf(this.getClass().getPackageName());
//        log.trace(LogUtils.getDescr(this) + " executing task for " + caller);
        if (ui == null)
            runnable.run();
        else if (!Thread.currentThread().equals(this.getThread()))
            ui.access(runnable::run);
        else if (ui.equals(UI.getCurrent()))
            runnable.run();
        else
            ui.access(runnable::run);
    }

    public boolean ifValid(Predicate<BaseEvent> predicate, Runnable runnable) {
        return ifValid(predicate, UI.getCurrent(), runnable);
    }

    public boolean ifValid(Predicate<BaseEvent> predicate, UI ui, Runnable runnable) {
        boolean result = verify(predicate);
        if (result) exec(ui, runnable);
        return result;
    }

    public boolean ifValid(Predicate<BaseEvent> predicate, Component component, Runnable runnable) {
        Utl.allNotNull(predicate, component, runnable);
        boolean result = verify(predicate);
        if (result) exec(component.getUI().orElse(null), runnable);
        return result;
    }

}
