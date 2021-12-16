package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class DataPublishHeader extends AbstractMessageHeader {
    /**
     * Вид сообщения. @see MessageKind.
     */
    @NotNull
    public MessageKind getKind() {
        return MessageKind.DataPublish;
    }

    @JsonCreator
    public DataPublishHeader(
            @JsonProperty("id") @NotNull final String id,
            @JsonProperty("kind") @NotNull final MessageKind kind,
            @JsonProperty("type") @NotNull final String type,
            @JsonProperty("systemSource") @Nullable final String sourceSystem,
            @JsonProperty("createdDateTime") @NotNull final LocalDateTime createdDateTime,
            @JsonProperty("version") final int version
    ) {
        super(id, kind, type, sourceSystem, createdDateTime, version);
    }

    public DataPublishHeader(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version
    ) {
        super(id, type, sourceSystem, createdDateTime, version);
    }
}