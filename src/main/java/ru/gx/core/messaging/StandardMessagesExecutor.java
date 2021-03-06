package ru.gx.core.messaging;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import ru.gx.core.worker.AbstractWorker;
import ru.gx.core.worker.AbstractWorkerStatisticsInfo;
import ru.gx.core.worker.AbstractOnIterationExecuteEvent;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class StandardMessagesExecutor extends AbstractWorker{
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    public static final String WORKER_DEFAULT_NAME = "standard-messages-executor";
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    @Getter(PROTECTED)
    @NotNull
    private final MessagesPrioritizedQueue messagesQueue;

    @Getter
    private final OnIterationExecuteEventInternal iterationExecuteEvent;

    @Getter
    @NotNull
    private final StandardMessagesExecutorOnStartingExecuteEvent startingExecuteEvent;

    @Getter
    @NotNull
    private final StandardMessagesExecutorOnStoppingExecuteEvent stoppingExecuteEvent;

    @Override
    public StandardMessagesExecutorStatisticsInfo getStatisticsInfo() {
        return (StandardMessagesExecutorStatisticsInfo)super.getStatisticsInfo();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    public StandardMessagesExecutor(
            @NotNull final String name,
            @NotNull final StandardMessagesExecutorSettingsContainer settingsContainer,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final MeterRegistry meterRegistry,
            @NotNull MessagesPrioritizedQueue messagesQueue,
            @NotNull ApplicationEventPublisher applicationEventPublisher
    ) {
        super(name, settingsContainer, meterRegistry, applicationEventPublisher);
        this.messagesQueue = messagesQueue;
        this.iterationExecuteEvent = new OnIterationExecuteEventInternal(this);
        this.startingExecuteEvent = new StandardMessagesExecutorOnStartingExecuteEvent(this);
        this.stoppingExecuteEvent = new StandardMessagesExecutorOnStoppingExecuteEvent(this);
    }

    @Override
    protected StandardMessagesExecutorStatisticsInfo createStatisticsInfo() {
        return new StandardMessagesExecutorStatisticsInfo(this, this.getMeterRegistry());
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="static class OnIterationExecuteEventInternal">
    protected static class OnIterationExecuteEventInternal extends AbstractOnIterationExecuteEvent {
        public OnIterationExecuteEventInternal(@NotNull Object source) {
            super(source);
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Iterations processing">
    @EventListener(OnIterationExecuteEventInternal.class)
    public void iterationExecute(@NotNull final OnIterationExecuteEventInternal iterationEvent) {
        log.debug("Starting iterationExecute()");
        try {
            this.runnerIsLifeSet();
            iterationEvent.setImmediateRunNextIteration(false);

            final var message = this.internalPollMessage(this.messagesQueue);
            if (message != null) {
                this.internalProcessMessage(message);
            }
            if (message != null || this.getMessagesQueue().queueSize() > 0) {
                iterationEvent.setImmediateRunNextIteration(true);
            }
        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(iterationEvent, e);
        } finally {
            log.debug("Finished iterationExecute()");
        }
    }

    /**
     * ?????????????????? ???????????? ?????? ???????????????????? ????????????????.
     *
     * @param event ????????????-?????????????? ?? ?????????????????????? ????????????????.
     * @param e     ????????????, ?????????????? ?????????????????? ????????????????????.
     */
    private void internalTreatmentExceptionOnDataRead(OnIterationExecuteEventInternal event, Exception e) {
        log.error("", e);
        if (e instanceof InterruptedException) {
            log.info("event.setStopExecution(true)");
            event.setStopExecution(true);
        } else {
            log.info("event.setNeedRestart(true)");
            event.setNeedRestart(true);
        }
    }

    /**
     * ?????????????? ?????????????? ???? ???????????????????? ???????????????? ?? ???????????????????? ?????? (?????????????? ????????????????????).
     * @param queue ?????????????????? ????????????????.
     * @return True - ?????????????? ???????? ?????????????????? ?? ????????????????????. False - ?????? ?????????????? ?? ??????????????.
     */
    protected Message<? extends MessageBody> internalPollMessage(@NotNull MessagesPrioritizedQueue queue) {
        final var event = queue.pollMessage();
        if (event == null) {
            log.debug("No messages in queue {}", queue.getName());
        } else {
            log.debug("Polled message " + event.getClass().getName());
        }
        return event;
    }

    /**
     * ?????????????????? ???????????? ??????????????????.
     * @param message ??????????????????, ?????????????? ?????????????? ???? ?????????????????? ?????????? this.eventPublisher.
     */
    protected void internalProcessMessage(@NotNull Message<? extends MessageBody> message) {
        try {
            this.getApplicationEventPublisher().publishEvent(message);
        } finally {
            this.getStatisticsInfo().messagesExecuteFinished(message);
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Start & Stop">
    @SuppressWarnings("unused")
    @EventListener(DoStartStandardMessagesExecutorEvent.class)
    public void doStartSimpleWorkerEvent(DoStartStandardMessagesExecutorEvent __) {
        this.start();
    }

    @SuppressWarnings("unused")
    @EventListener(DoStopStandardMessagesExecutorEvent.class)
    public void doStopSimpleWorkerEvent(DoStopStandardMessagesExecutorEvent __) {
        this.stop();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
