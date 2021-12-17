package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class QueryResultHeader extends AbstractMessageHeader {
    /**
     * Вид сообщения. @see MessageKind.
     */
    @NotNull
    public MessageKind getKind() {
        return MessageKind.QueryResult;
    }

    @Getter
    @NotNull
    public final String queryId;

    /**
     * Конструктор заголовка ответа на запрос.
     * @param id Идентификатор сообщения.
     * @param kind Вид сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTime Дата и время создания сообщения.
     * @param version Версия сообщения.
     * @param queryId Идентификатор исходного запроса, для которого сформирован данный ответ.
     */
    @SuppressWarnings("unused")
    @JsonCreator
    public QueryResultHeader(
            @JsonProperty("id") @NotNull final String id,
            @JsonProperty("kind") @NotNull final MessageKind kind,
            @JsonProperty("type") @NotNull final String type,
            @JsonProperty("systemSource") @Nullable final String sourceSystem,
            @JsonProperty("createdDateTime") @NotNull final LocalDateTime createdDateTime,
            @JsonProperty("version") final int version,
            @JsonProperty("queryId") @NotNull final String queryId
    ) {
        super(id, kind, type, sourceSystem, createdDateTime, version);
        this.queryId = queryId;
    }

    /**
     * Конструктор заголовка ответа на запрос.
     * @param id Идентификатор сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTime Дата и время создания сообщения.
     * @param version Версия сообщения.
     * @param queryId Идентификатор исходного запроса, для которого сформирован данный ответ.
     */
    public QueryResultHeader(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version,
            @NotNull final String queryId
    ) {
        super(id, type, sourceSystem, createdDateTime, version);
        this.queryId = queryId;
    }
}
