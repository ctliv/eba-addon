package io.github.ctliv.eventbus;

import io.github.ctliv.eventbus.listener.EventBusAwareInitListener;
import io.github.ctliv.eventbus.processor.EventBusAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class EventBusAwareConfiguration {

    @Bean
    public EventBusAwareInitListener getEventBusAwareInitListener() {
        return new EventBusAwareInitListener();
    }

    @Bean
    public EventBusAwareBeanPostProcessor getEventBusAwareBeanPostProcessor() {
        return new EventBusAwareBeanPostProcessor(); }

}
