package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractRequest<B extends AbstractMessageBody>
        extends AbstractMessage<RequestHeader, B>
        implements Request<RequestHeader, B> {

    @JsonCreator
    public AbstractRequest(
            @JsonProperty("header") @NotNull final RequestHeader header,
            @JsonProperty("body") @Nullable final B body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    public AbstractRequest(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version,
            @Nullable final B body,
            @Nullable final MessageCorrelation correlation
    ) {
        super(new RequestHeader(id, type, sourceSystem, createdDateTime, version), body, correlation);
    }
}