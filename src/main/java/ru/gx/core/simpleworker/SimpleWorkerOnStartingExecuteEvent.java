package ru.gx.core.simpleworker;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractOnStartingExecuteEvent;

public class SimpleWorkerOnStartingExecuteEvent extends AbstractOnStartingExecuteEvent {
    public SimpleWorkerOnStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}
