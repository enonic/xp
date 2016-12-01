package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.data.ValueTypes;

public class PublishScheduleJson
{

    private LocalDateTime publishFrom;

    private LocalDateTime publishTo;

    @JsonCreator
    public PublishScheduleJson( @JsonProperty("from") final String publishFrom, @JsonProperty("to") final String publishTo )
    {
        this.publishFrom = ValueTypes.LOCAL_DATE_TIME.convert( publishFrom );
        if ( publishTo != null )
        {
            this.publishTo = ValueTypes.LOCAL_DATE_TIME.convert( publishTo );
        }
    }

    public LocalDateTime getPublishFrom()
    {
        return publishFrom;
    }

    public LocalDateTime getPublishTo()
    {
        return publishTo;
    }
}
