package ru.gx.core.channels;

import io.micrometer.core.instrument.MeterRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;

@SuppressWarnings("unused")
public interface ChannelsConfiguration extends InternalDescriptorsRegistrator {
    @NotNull
    String getConfigurationName();

    /**
     * Проверка регистрации описателя канала в конфигурации.
     *
     * @param channelName Имя канала.
     * @return true - описатель канала зарегистрирован.
     */
    boolean contains(@NotNull final String channelName);

    /**
     * Получение описателя канала по имени канала.
     *
     * @param channelName Имя канала, для которого требуется получить описатель.
     * @return Описатель канала.
     * @throws ChannelConfigurationException В случае отсутствия описателя канала с заданным именем бросается ошибка.
     */
    @NotNull
    ChannelHandlerDescriptor get(@NotNull final String channelName) throws ChannelConfigurationException;

    /**
     * Получение описателя канала по имени.
     *
     * @param channelName Имя канала, для которого требуется получить описатель.
     * @return Описатель канала. Если не найден, то возвращается null.
     */
    @Nullable
    ChannelHandlerDescriptor tryGet(@NotNull final String channelName);

    /**
     * Регистрация описателя обработчика канала.
     *
     * @param channelApi      API канала.
     * @param descriptorClass Класс описателя.
     * @return this.
     */
    @NotNull
    <M extends Message<? extends MessageBody>, D extends ChannelHandlerDescriptor>
    D newDescriptor(
            @NotNull final ChannelApiDescriptor<M> channelApi,
            @NotNull final Class<D> descriptorClass
    ) throws ChannelConfigurationException;

    @NotNull
    <D extends ChannelHandlerDescriptor>
    D newDescriptor(
            @NotNull final String channelName,
            @NotNull final Class<D> descriptorClass
    ) throws ChannelConfigurationException;

    /**
     * @return Настройки по умолчанию для новых описателей загрузки из топиков.
     */
    @NotNull
    AbstractChannelDescriptorsDefaults getDescriptorsDefaults();

    /**
     * @return Количество приоритетов.
     */
    int prioritiesCount();

    /**
     * Получение списка описателей обработчиков очередей по приоритету.
     *
     * @param priority Приоритет.
     * @return Список описателей обработчиков.
     */
    @Nullable
    Iterable<ChannelHandlerDescriptor> getByPriority(int priority);

    /**
     * @return Список всех описателей обработчиков очередей.
     */
    @NotNull
    Iterable<ChannelHandlerDescriptor> getAll();

    @NotNull
    MeterRegistry getMeterRegistry();
}
