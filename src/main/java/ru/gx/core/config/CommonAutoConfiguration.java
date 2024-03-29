package ru.gx.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.gx.core.channels.ChannelExecuteStatisticsManager;
import ru.gx.core.channels.ChannelsConfiguration;
import ru.gx.core.messaging.*;
import ru.gx.core.metrics.MetricsInitializer;
import ru.gx.core.settings.StandardSettingsController;
import ru.gx.core.simpleworker.SimpleWorker;
import ru.gx.core.simpleworker.SimpleWorkerSettingsContainer;
import ru.gx.core.utils.OffsetDateTimeDeserializer;
import ru.gx.core.utils.OffsetDateTimeSerializer;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.TimeZone;

import static lombok.AccessLevel.PROTECTED;

@Configuration
@EnableConfigurationProperties(ConfigurationPropertiesService.class)
@ComponentScan("ru.gx.core.longtime")
public class CommonAutoConfiguration {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    public static final String CHANNELS_STATISTICS_MANAGER = "service.channels-statistics";
    private final static String DOT_ENABLED = ".enabled";
    private final static String DOT_NAME = ".name";
    private final static String SERVICE_NAME = "service.name";

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="ObjectMapper">
    @Getter(PROTECTED)
    private ObjectMapper objectMapper;

    @Autowired
    protected void setObjectMapper(@NotNull final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.objectMapper.setTimeZone(TimeZone.getDefault());

        final var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
        javaTimeModule.addDeserializer(OffsetDateTime.class, OffsetDateTimeDeserializer.INSTANCE);
        this.objectMapper.registerModule(javaTimeModule);
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Standard Settings Controller">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            value = StandardSettingsController.STANDARD_SETTINGS_CONTROLLER_PREFIX + DOT_ENABLED,
            havingValue = "true"
    )
    @Autowired
    public StandardSettingsController standardSettingsController(
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final Environment environment
    ) {
        return new StandardSettingsController(eventPublisher, environment);
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Simple Worker">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED,
            havingValue = "true"
    )
    @Autowired
    public SimpleWorker simpleWorker(
            @Value("${"
                    + SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_NAME
                    + ":" + SimpleWorker.WORKER_DEFAULT_NAME + "}"
            ) final String name,
            @NotNull final SimpleWorkerSettingsContainer settingsContainer,
            @NotNull final MeterRegistry meterRegistry,
            @NotNull final ApplicationEventPublisher eventPublisher
    ) {
        return new SimpleWorker(name, settingsContainer, meterRegistry, eventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED,
            havingValue = "true"
    )
    @Autowired
    public SimpleWorkerSettingsContainer simpleWorkerSettingsContainer(
            @NotNull final StandardSettingsController standardSettingsController
    ) {
        return new SimpleWorkerSettingsContainer(standardSettingsController);
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Standard Events Executor">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            value = StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED,
            havingValue = "true"
    )
    @Autowired
    public StandardMessagesExecutorSettingsContainer standardEventsExecutorSettingsContainer(
            @NotNull final StandardSettingsController standardSettingsController
    ) {
        return new StandardMessagesExecutorSettingsContainer(standardSettingsController);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            value = StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED,
            havingValue = "true"
    )
    @Autowired
    public StandardMessagesExecutor standardMessagesExecutor(
            @Value("${"
                    + StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_NAME
                    + ":" + StandardMessagesExecutor.WORKER_DEFAULT_NAME + "}"
            ) String name,
            @NotNull final StandardMessagesExecutorSettingsContainer settingsContainer,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final MeterRegistry meterRegistry,
            @NotNull final MessagesPrioritizedQueue messagesQueue
    ) {
        return new StandardMessagesExecutor(
                name,
                settingsContainer,
                meterRegistry,
                eventPublisher,
                messagesQueue
        );
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            value = StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_ENABLED,
            havingValue = "true"
    )
    @Autowired
    public StandardMessagesPrioritizedQueue standardMessagesPrioritizedQueue(
            @Value("${" +
                    StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_NAME
                    + ":" + StandardMessagesPrioritizedQueue.DEFAULT_NAME + "}"
            ) final String name,
            @NotNull final StandardMessagesExecutorSettingsContainer executorSettings
    ) {
        final var queue = new StandardMessagesPrioritizedQueue(name);
        queue.init(executorSettings.maxQueueSize(), executorSettings.prioritiesCount());
        return queue;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Standard Settings Controller">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SERVICE_NAME)
    @Autowired
    public DefaultMessagesFactory defaultMessagesFactory(
            @Value("${service.name}") final String serviceName
    ) {
        return new DefaultMessagesFactory(serviceName);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SERVICE_NAME)
    @Autowired
    public MetricsInitializer metricsInitializer(
            @Value("${" + SERVICE_NAME + "}") final String serviceName,
            @NotNull final MeterRegistry meterRegistry
    ) {
        return new MetricsInitializer(serviceName, meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            value = CHANNELS_STATISTICS_MANAGER + DOT_ENABLED,
            havingValue = "true"
    )
    @Autowired
    public ChannelExecuteStatisticsManager channelExecuteStatisticsManager(
            @NotNull final List<ChannelsConfiguration> configurations,
            @Value("${" + CHANNELS_STATISTICS_MANAGER + ".print-statistics-every-ms}") final int printStatisticsEveryMs,
            @NotNull final MeterRegistry meterRegistry
    ) {
        return new ChannelExecuteStatisticsManager(configurations, printStatisticsEveryMs);
    }
}