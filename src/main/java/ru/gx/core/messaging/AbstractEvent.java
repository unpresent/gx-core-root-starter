package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractEvent<B extends AbstractMessageBody>
        extends AbstractMessage<EventHeader, B>
        implements Event<B> {

    @JsonCreator
    public AbstractEvent(
            @JsonProperty("header") @NotNull final EventHeader header,
            @JsonProperty("body") @NotNull final B body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    protected AbstractEvent(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version,
            @NotNull final B body,
            @Nullable final MessageCorrelation correlation
    ) {
        super(new EventHeader(id, type, sourceSystem, createdDateTime, version), body, correlation);
    }
}