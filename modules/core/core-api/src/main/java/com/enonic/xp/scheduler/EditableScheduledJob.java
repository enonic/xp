package com.enonic.xp.scheduler;

import java.time.Instant;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class EditableScheduledJob
{
    private final ScheduledJobName name;

    public String description;

    public ScheduleCalendar calendar;

    public boolean enabled;

    public DescriptorKey descriptor;

    public PropertyTree config;

    public PrincipalKey user;

    private final PrincipalKey creator;

    private final Instant createdTime;

    public EditableScheduledJob( final ScheduledJob source )
    {
        this.name = source.getName();
        this.description = source.getDescription();
        this.calendar = source.getCalendar();
        this.enabled = source.isEnabled();
        this.descriptor = source.getDescriptor();
        this.user = source.getUser();
        this.config = source.getConfig().copy();
        this.creator = source.getCreator();
        this.createdTime = source.getCreatedTime();
    }

    public ScheduledJob build()
    {
        return ScheduledJob.create().
            name( name ).
            description( description ).
            calendar( calendar ).
            enabled( enabled ).
            descriptor( descriptor ).
            config( config ).
            user( user ).
            creator( creator ).
            createdTime( createdTime ).
            build();
    }
}
