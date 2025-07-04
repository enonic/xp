package com.enonic.xp.scheduler;

import java.io.Serializable;
import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;

@PublicApi
public final class ScheduledJob
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final ScheduledJobName name;

    private final String description;

    private final ScheduleCalendar calendar;

    private final boolean enabled;

    private final DescriptorKey descriptor;

    private final PropertyTree config;

    private final PrincipalKey user;

    private final PrincipalKey creator;

    private final PrincipalKey modifier;

    private final Instant lastRun;

    private final TaskId lastTaskId;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private ScheduledJob( final Builder builder )
    {
        this.name = builder.name;
        this.description = builder.description;
        this.calendar = builder.calendar;
        this.enabled = builder.enabled;
        this.descriptor = builder.descriptor;
        this.config = builder.config;
        this.user = builder.user;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.lastRun = builder.lastRun;
        this.lastTaskId = builder.lastTaskId;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ScheduledJobName getName()
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

    public DescriptorKey getDescriptor()
    {
        return descriptor;
    }

    public PropertyTree getConfig()
    {
        return config;
    }

    public PrincipalKey getUser()
    {
        return user;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKey getModifier()
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

    public TaskId getLastTaskId()
    {
        return lastTaskId;
    }

    public static final class Builder
    {
        private ScheduledJobName name;

        private String description;

        private ScheduleCalendar calendar;

        private boolean enabled;

        private DescriptorKey descriptor;

        private PropertyTree config = new PropertyTree();

        private PrincipalKey user;

        private PrincipalKey creator;

        private PrincipalKey modifier;

        private Instant createdTime;

        private Instant modifiedTime;

        private Instant lastRun;

        private TaskId lastTaskId;

        public Builder name( final ScheduledJobName name )
        {
            this.name = name;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder calendar( final ScheduleCalendar calendar )
        {
            this.calendar = calendar;
            return this;
        }

        public Builder enabled( final boolean enabled )
        {
            this.enabled = enabled;
            return this;
        }

        public Builder descriptor( final DescriptorKey descriptor )
        {
            this.descriptor = descriptor;
            return this;
        }

        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }

        public Builder user( final PrincipalKey user )
        {
            this.user = user;
            return this;
        }

        public Builder creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder createdTime( final Instant createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder lastRun( final Instant lastRun )
        {
            this.lastRun = lastRun;
            return this;
        }

        public Builder lastTaskId( final TaskId lastTaskId )
        {
            this.lastTaskId = lastTaskId;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name must be set." );
            Preconditions.checkNotNull( calendar, "calendar must be set." );
            Preconditions.checkNotNull( descriptor, "descriptor must be set." );
            Preconditions.checkNotNull( config, "config must be set." );
        }

        public ScheduledJob build()
        {
            validate();
            return new ScheduledJob( this );
        }
    }
}
