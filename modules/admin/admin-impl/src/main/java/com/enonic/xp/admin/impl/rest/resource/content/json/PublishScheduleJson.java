package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PublishScheduleJson
{
    private final Instant publishFrom;

    private final Instant publishTo;

    @JsonCreator
    public PublishScheduleJson( @JsonProperty(value = "from", required = true) final String publishFrom,
                                @JsonProperty("to") final String publishTo )
    {
        this.publishFrom = Instant.parse( publishFrom );
        this.publishTo = publishTo == null ? null : Instant.parse( publishTo );
    }

    public Instant getPublishFrom()
    {
        return publishFrom;
    }

    public Instant getPublishTo()
    {
        return publishTo;
    }
}
