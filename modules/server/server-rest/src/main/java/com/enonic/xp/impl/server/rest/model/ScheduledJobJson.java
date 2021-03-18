package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.security.PrincipalKey;

public class ScheduledJobJson
{
    private final String name;

    private final String description;

    private final ScheduleCalendar calendar;

    private final boolean enabled;

    private final String descriptor;

    private final List<PropertyArrayJson> payload;

    private final String user;

    private final String author;

    private final Instant lastRun;

    public ScheduledJobJson( final ScheduledJob job )
    {
        this.name = job.getName().getValue();
        this.description = job.getDescription();
        this.descriptor = job.getDescriptor().toString();
        this.calendar = job.getCalendar();
        this.enabled = job.isEnabled();
        this.payload = PropertyTreeJson.toJson( job.getPayload() );
        this.user = Optional.ofNullable( job.getUser() ).map( PrincipalKey::toString ).orElse( null );
        this.author = Optional.ofNullable( job.getAuthor() ).map( PrincipalKey::toString ).orElse( null );
        this.lastRun = job.getLastRun();
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public ScheduleCalendar getCalendar()
    {
        return calendar;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public List<PropertyArrayJson> getPayload()
    {
        return payload;
    }

    public String getUser()
    {
        return user;
    }

    public String getAuthor()
    {
        return author;
    }

    public Instant getLastRun()
    {
        return lastRun;
    }
}
