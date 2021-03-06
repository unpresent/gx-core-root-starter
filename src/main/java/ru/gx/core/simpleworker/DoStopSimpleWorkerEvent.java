package ru.gx.core.simpleworker;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Объект-событие.<br/>
 * Публикация данного события останавливает Worker.
 * Слушателем данного является сам Worker.
 */
@SuppressWarnings("unused")
public class DoStopSimpleWorkerEvent extends ApplicationEvent {
    protected DoStopSimpleWorkerEvent(@NotNull final Object source) {
        super(source);
    }

    public static void publish(@NotNull final ApplicationEventPublisher publisher, @NotNull final Object source) {
        final var event = new DoStopSimpleWorkerEvent(source);
        publisher.publishEvent(event);
    }

    public static void publish(@NotNull final ApplicationContext context, @NotNull final Object source) {
        final var event = new DoStopSimpleWorkerEvent(source);
        context.publishEvent(event);
    }
}
