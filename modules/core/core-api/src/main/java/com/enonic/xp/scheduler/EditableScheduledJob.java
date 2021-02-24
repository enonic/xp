package com.enonic.xp.scheduler;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class EditableScheduledJob
{
    private final SchedulerName name;

    public String description;

    public ScheduleCalendar calendar;

    public boolean enabled;

    public DescriptorKey descriptor;

    public PropertyTree payload;

    public PrincipalKey user;

    public PrincipalKey author;

    public EditableScheduledJob( final ScheduledJob source )
    {
        this.name = source.getName();
        this.description = source.getDescription();
        this.calendar = source.getCalendar();
        this.enabled = source.isEnabled();
        this.descriptor = source.getDescriptor();
        this.user = source.getUser();
        this.author = source.getAuthor();
        this.payload = source.getPayload().copy();
    }

    public ScheduledJob build()
    {
        return ScheduledJob.create().
            name( name ).
            description( description ).
            calendar( calendar ).
            enabled( enabled ).
            descriptor( descriptor ).
            payload( payload ).
            user( user ).
            author( author ).
            build();
    }
}
