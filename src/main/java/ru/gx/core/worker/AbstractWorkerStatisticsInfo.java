package ru.gx.core.worker;

import io.micrometer.core.instrument.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static lombok.AccessLevel.PROTECTED;

public abstract class AbstractWorkerStatisticsInfo implements StatisticsInfo {
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
    @NotNull
    private final AbstractWorker owner;

    @Getter(PROTECTED)
    @NotNull
    private final MeterRegistry meterRegistry;

    /**
     * Общие для всех метрик ярлыки.
     */
    @Getter(PROTECTED)
    private final List<Tag> metricsTags;

    /**
     * Метрика: количество исполнений с момента запуска.
     */
    @Getter(PROTECTED)
    @NotNull
    private final Counter metricExecutionsCount;

    /**
     * Метрика: общее время на исполнения с момента запуска.
     */
    @Getter(PROTECTED)
    @NotNull
    private final Timer metricExecutionsTime;

    /**
     * Количество исполнений с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private long executionsCount;

    /**
     * Общее время затраченное на обработку событий с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private long executionsTotalTimeMs;

    /**
     * Максимальное время затраченное на обработку одного событий с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private long executionMaxTimePerIterationMs;

    /**
     * Общее время затраченное на sleep с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private long executionsTotalSleepTimeMs;

    /**
     * Момент начала работы последней итерации.
     */
    @Getter(PROTECTED)
    private long lastIterationStartedMs;

    /**
     * Момент начала последнего Sleep-а.
     */
    @Getter(PROTECTED)
    private long lastSleepStartedMs;

    /**
     * Признак того, что с момента последнего сброса не было зафиксировано ни одной обработки события.
     */
    @Getter(PROTECTED)
    private volatile boolean isEmpty;

    @Getter
    private volatile long lastResetMs;

    @Getter(PROTECTED)
    private AtomicInteger internalResetLocksCount;

    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractWorkerStatisticsInfo(
            @NotNull final AbstractWorker worker,
            @NotNull final MeterRegistry meterRegistry
    ) {
        super();
        this.owner = worker;
        this.meterRegistry = meterRegistry;
        this.metricsTags = List.of(Tag.of(METRIC_TAG_WORKER_NAME, worker.getWorkerName()));
        this.metricExecutionsCount = Counter.builder(METRIC_EXECUTIONS_COUNT)
                .tags(this.metricsTags)
                .register(this.meterRegistry);
        this.metricExecutionsTime = Timer.builder(METRIC_EXECUTIONS_TIME)
                .tags(this.metricsTags)
                .register(this.meterRegistry);
        final var m = Gauge.builder(METRIC_WORKER_NAME, () -> 0)
                .tags(List.of(Tag.of(METRIC_TAG_WORKER_NAME, this.owner.getWorkerName())))
                .register(this.meterRegistry);
        this.privateReset();
    }

    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements StatisticsInfo">
    @Override
    public String getPrintableInfo() {

        if (this.isEmpty) {
            return "Stat for last " +
                    lastResetMsAgo() +
                    " ms for the worker " +
                    this.owner.getWorkerName() +
                    " is empty";
        }
        return "Stat for last " +
                lastResetMsAgo() +
                " ms for the worker iterations " +
                this.owner.getWorkerName() +
                " is: count = " +
                this.executionsCount +
                ", SLEEPms = " +
                this.executionsTotalSleepTimeMs +
                ", totalMs = " +
                this.executionsTotalTimeMs +
                ", maxTime = " +
                this.executionMaxTimePerIterationMs +
                ", avgTime = " +
                (this.executionsCount > 0 ? this.executionsTotalTimeMs / this.executionsCount : "NaN");
    }

    /**
     * Сброс статистики и метрик.
     */
    @Override
    public void reset() {
        internalPushMetrics();
        internalReset();
    }

    @Override
    public long lastResetMsAgo() {
        return System.currentTimeMillis() - this.lastResetMs;
    }
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main logic">

    /**
     * Собственно сброс метрик. <br/>
     * Вызывается в т.ч. и в конструкторе!
     */
    private void privateReset() {
        this.isEmpty = true;
        this.executionsCount = 0;
        this.executionsTotalSleepTimeMs = 0;
        this.executionsTotalTimeMs = 0;
        this.executionMaxTimePerIterationMs = 0;
        this.lastSleepStartedMs = 0;
        this.lastIterationStartedMs = 0;
        this.lastResetMs = System.currentTimeMillis();
    }

    protected void internalReset() {
        privateReset();
    }

    /**
     * Запись значений в метрики.
     */
    protected void internalPushMetrics() {
        this.meterRegistry.gauge(METRIC_EXECUTIONS_BUSY_PERCENTS, this.metricsTags, this.executionsTotalTimeMs * 100 / this.lastResetMsAgo());
    }

    public void iterationStarted() {
        this.lastIterationStartedMs = System.currentTimeMillis();
    }

    /**
     * Фиксирование факта исполнения итерации
     */
    public void iterationExecuted() {
        final var curTimeMsPerIteration = System.currentTimeMillis() - getLastIterationStartedMs();
        this.executionsCount++;
        this.executionsTotalTimeMs += curTimeMsPerIteration;
        if (curTimeMsPerIteration > this.getExecutionMaxTimePerIterationMs()) {
            this.executionMaxTimePerIterationMs = curTimeMsPerIteration;
        }
        this.isEmpty = false;
        this.getMetricExecutionsTime().record(Duration.ofMillis(curTimeMsPerIteration));
        this.getMetricExecutionsCount().increment();
    }

    public void sleepStarted() {
        this.lastSleepStartedMs = System.currentTimeMillis();
    }

    public void sleepFinished() {
        this.executionsTotalSleepTimeMs += System.currentTimeMillis() - getLastSleepStartedMs();
    }
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
}
