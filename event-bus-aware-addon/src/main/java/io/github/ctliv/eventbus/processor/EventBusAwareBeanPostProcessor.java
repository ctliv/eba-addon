package io.github.ctliv.eventbus.processor;

import io.github.ctliv.eventbus.EventBusAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class EventBusAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (EventBusAware.class.isAssignableFrom(bean.getClass())) {
            final EventBusAware eventBusAware = (EventBusAware) bean;
            eventBusAware.ebaRegister();
        }
        return DestructionAwareBeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (EventBusAware.class.isAssignableFrom(bean.getClass())) {
            final EventBusAware eventBusAware = (EventBusAware) bean;
            eventBusAware.ebaUnregister();
        }
    }

}
