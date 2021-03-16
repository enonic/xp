package com.enonic.xp.lib.scheduler.mapper;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ScheduledJobMapper
    implements MapSerializable
{
    private final ScheduledJob job;

    public ScheduledJobMapper( final Builder builder )
    {
        this.job = builder.job;
    }

    public static ScheduledJobMapper from( final ScheduledJob job )
    {
        return ScheduledJobMapper.create().job( job ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        doSerialize( gen );
    }

    private void doSerialize( final MapGenerator gen )
    {
        gen.value( "name", job.getName().getValue() );
        gen.value( "descriptor", job.getDescriptor().toString() );
        gen.value( "description", job.getDescription() );
        gen.value( "enabled", job.isEnabled() );
        gen.value( "payload", job.getPayload() );
        gen.value( "user", job.getUser() != null ? job.getUser().toString() : null );
        gen.value( "author", job.getAuthor() != null ? job.getAuthor().toString() : null );

        serializeCalendar( gen, job.getCalendar() );
        serializePayload( gen, job.getPayload() );
    }

    private void serializePayload( final MapGenerator gen, final PropertyTree payload )
    {
        gen.map( "payload" );
        new PropertyTreeMapper( payload ).serialize( gen );
        gen.end();
    }

    private void serializeCalendar( final MapGenerator gen, final ScheduleCalendar calendar )
    {
        gen.map( "calendar" );
        switch ( calendar.getType() )
        {
            case ONE_TIME:
                new OneTimeCalendarMapper( (OneTimeCalendar) calendar ).serialize( gen );
                break;
            case CRON:
                new CronCalendarMapper( (CronCalendar) calendar ).serialize( gen );
                break;
            default:
                throw new IllegalArgumentException( String.format( "invalid calendar type: [%s]", calendar.getType() ) );
        }
        gen.end();
    }

    public static class Builder
    {
        private ScheduledJob job;

        public Builder()
        {
            super();
        }

        public Builder job( final ScheduledJob job )
        {
            this.job = job;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( job, "job must be set." );
        }

        public ScheduledJobMapper build()
        {
            validate();
            return new ScheduledJobMapper( this );
        }
    }
}

