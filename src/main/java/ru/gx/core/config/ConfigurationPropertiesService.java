package ru.gx.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "service")
@Getter
@Setter
public class ConfigurationPropertiesService {
    private String name;

    @NestedConfigurationProperty
    private StandardSettingsController standardSettingsController = new StandardSettingsController();

    @NestedConfigurationProperty
    private SimpleWorker simpleWorker = new SimpleWorker();

    @NestedConfigurationProperty
    private Events events = new Events();

    @NestedConfigurationProperty
    private Channels channels = new Channels();

    @Getter
    @Setter
    public static class Channels {
        @NestedConfigurationProperty
        private ConfiguratorCaller configuratorCaller = new ConfiguratorCaller();
    }

    @Getter
    @Setter
    public static class ConfiguratorCaller {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class StandardSettingsController {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class SimpleWorker {
        public static final String NAME_DEFAULT = "simple-worker";
        public static final int WAIT_ON_STOP_MS_DEFAULT = 3000;
        public static final int WAIT_ON_RESTART_MS_DEFAULT = 30000;
        public static final int MIN_TIME_PER_ITERATION_MS_DEFAULT = 1000;
        public static final int TIMEOUT_RUNNER_LIFE_MS_DEFAULT = 20000;
        public static final int PRINT_STATISTICS_EVERY_MS_DEFAULT = 1000;

        private boolean enabled = false;
        private String name = NAME_DEFAULT;
        private int waitOnStopMs = WAIT_ON_STOP_MS_DEFAULT;
        private int waitOnRestartMs = WAIT_ON_RESTART_MS_DEFAULT;
        private int minTimePerIterationMs = MIN_TIME_PER_ITERATION_MS_DEFAULT;
        private int timeoutRunnerLifeMs = TIMEOUT_RUNNER_LIFE_MS_DEFAULT;
        private int printStatisticsEveryMs = PRINT_STATISTICS_EVERY_MS_DEFAULT;
    }

    @Getter
    @Setter
    public static class Events {
        @NestedConfigurationProperty
        private StandardExecutor standardExecutor = new StandardExecutor();

        @NestedConfigurationProperty
        private StandardQueue standardQueue = new StandardQueue();
    }

    @Getter
    @Setter
    public static class StandardExecutor {
        public static final String NAME_DEFAULT = "events-executor";
        public static final int WAIT_ON_STOP_MS_DEFAULT = 3000;
        public static final int WAIT_ON_RESTART_MS_DEFAULT = 30000;
        public static final int MIN_TIME_PER_ITERATION_MS_DEFAULT = 1000;
        public static final int TIMEOUT_RUNNER_LIFE_MS_DEFAULT = 20000;
        public static final int PRINT_STATISTICS_EVERY_MS_DEFAULT = 1000;

        private boolean enabled = false;
        private String name = NAME_DEFAULT;
        private int waitOnStopMs = WAIT_ON_STOP_MS_DEFAULT;
        private int waitOnRestartMs = WAIT_ON_RESTART_MS_DEFAULT;
        private int minTimePerIterationMs = MIN_TIME_PER_ITERATION_MS_DEFAULT;
        private int timeoutRunnerLifeMs = TIMEOUT_RUNNER_LIFE_MS_DEFAULT;
        private int printStatisticsEveryMs = PRINT_STATISTICS_EVERY_MS_DEFAULT;
    }

    @Getter
    @Setter
    public static class StandardQueue {
        public static final String NAME_DEFAULT = "events-queue";
        public static final int PRINT_STATISTICS_EVERY_MS_DEFAULT = 1000;
        public static final int MAX_QUEUE_SIZE_DEFAULT = 10000;
        public static final int PRIORITIES_COUNT_DEFAULT = 8;

        private boolean enabled = true;
        private String name = NAME_DEFAULT;
        private int printStatisticsEveryMs = PRINT_STATISTICS_EVERY_MS_DEFAULT;
        private int maxQueueSize = MAX_QUEUE_SIZE_DEFAULT;
        private int prioritiesCount = PRIORITIES_COUNT_DEFAULT;
    }
}