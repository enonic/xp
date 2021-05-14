package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;

public class ScheduledJobJson
{
    private final String name;

    private final String description;

    private final ScheduleCalendar calendar;

    private final boolean enabled;

    private final String descriptor;

    private final List<PropertyArrayJson> config;

    private final String user;

    private final String creator;

    private final String modifier;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final Instant lastRun;

    private final String lastTaskId;

    public ScheduledJobJson( final ScheduledJob job )
    {
        this.name = job.getName().getValue();
        this.description = job.getDescription();
        this.descriptor = job.getDescriptor().toString();
        this.calendar = job.getCalendar();
        this.enabled = job.isEnabled();
        this.config = PropertyTreeJson.toJson( job.getConfig() );
        this.user = Optional.ofNullable( job.getUser() ).map( PrincipalKey::toString ).orElse( null );
        this.creator = Optional.ofNullable( job.getCreator() ).map( PrincipalKey::toString ).orElse( null );
        this.modifier = Optional.ofNullable( job.getCreator() ).map( PrincipalKey::toString ).orElse( null );
        this.lastRun = job.getLastRun();
        this.lastTaskId = Optional.ofNullable( job.getLastTaskId() ).map( TaskId::toString ).orElse( null );
        this.createdTime = job.getCreatedTime();
        this.modifiedTime = job.getModifiedTime();
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

    public List<PropertyArrayJson> getConfig()
    {
        return config;
    }

    public String getUser()
    {
        return user;
    }

    public String getCreator()
    {
        return creator;
    }

    public String getModifier()
    {
        return modifier;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public Instant getLastRun()
    {
        return lastRun;
    }

    public String getLastTaskId()
    {
        return lastTaskId;
    }
}
