package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PublishScheduleJson
{

    private Instant publishFrom;

    private Instant publishTo;

    @JsonCreator
    public PublishScheduleJson( @JsonProperty("from") final String publishFrom, @JsonProperty("to") final String publishTo )
    {
        this.publishFrom = Instant.parse( publishFrom );
        if ( publishTo != null )
        {
            this.publishTo = Instant.parse( publishTo );
        }
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
