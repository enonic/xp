package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.PublishRequestIssueSchedule;

public class PublishRequestScheduleJson
{

    private Instant from;

    private Instant to;

    @JsonCreator
    public PublishRequestScheduleJson( @JsonProperty("from") final String from, @JsonProperty("to") final String to )
    {
        this( from != null ? Instant.parse( from ) : null, to != null ? Instant.parse( to ) : null );
    }

    private PublishRequestScheduleJson( Instant from, Instant to )
    {
        this.from = from;
        this.to = to;
    }

    public static PublishRequestScheduleJson from( final PublishRequestIssueSchedule schedule )
    {
        return new PublishRequestScheduleJson( schedule.getFrom(), schedule.getTo() );
    }

    public Instant getFrom()
    {
        return from;
    }

    public Instant getTo()
    {
        return to;
    }

    public PublishRequestIssueSchedule toSchedule()
    {
        return PublishRequestIssueSchedule.create().
            from( from ).
            to( to ).
            build();
    }
}
