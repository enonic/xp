package com.enonic.xp.scheduler;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class CreateScheduledJobParams
{
    private final ScheduledJobName name;

    private final String description;

    private final ScheduleCalendar calendar;

    private final boolean enabled;

    private final DescriptorKey descriptor;

    private final PropertyTree config;

    private final PrincipalKey user;

    private CreateScheduledJobParams( final Builder builder )
    {
        this.name = builder.name;
        this.description = builder.description;
        this.calendar = builder.calendar;
        this.enabled = builder.enabled;
        this.descriptor = builder.descriptor;
        this.config = builder.config;
        this.user = builder.user;
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

    public static final class Builder
    {
        private ScheduledJobName name;

        private String description;

        private ScheduleCalendar calendar;

        private boolean enabled;

        private DescriptorKey descriptor;

        private PropertyTree config = new PropertyTree();

        private PrincipalKey user;

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

        private void validate()
        {
            Preconditions.checkNotNull( name, "name must be set." );
            Preconditions.checkNotNull( calendar, "calendar must be set." );
            Preconditions.checkNotNull( descriptor, "descriptor must be set." );
            Preconditions.checkNotNull( config, "config must be set." );
        }

        public CreateScheduledJobParams build()
        {
            validate();
            return new CreateScheduledJobParams( this );
        }
    }
}
