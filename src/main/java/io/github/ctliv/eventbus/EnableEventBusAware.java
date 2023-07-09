package io.github.ctliv.eventbus;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EventBusAwareConfiguration.class)
public @interface EnableEventBusAware {
}
