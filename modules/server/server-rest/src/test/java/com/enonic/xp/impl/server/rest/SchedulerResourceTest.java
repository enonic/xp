package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;

import static org.mockito.Mockito.mock;

class SchedulerResourceTest
    extends JaxRsResourceTestSupport
{
    private SchedulerService schedulerService;

    @Override
    protected SchedulerResource getResourceInstance()
    {
        schedulerService = mock( SchedulerService.class );

        return new SchedulerResource( schedulerService );
    }

    @Test
    void list()
        throws Exception
    {
        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" );
        final CronCalendar cronCalendar = new CronCalendar()
        {
            @Override
            public String getCronValue()
            {
                return "* * * * *";
            }

            @Override
            public TimeZone getTimeZone()
            {
                return TimeZone.getTimeZone( "GMT+3:00" );
            }

            @Override
            public Optional<Instant> nextExecution( final Instant instant )
            {
                return Optional.empty();
            }

            @Override
            public ScheduleCalendarType getType()
            {
                return ScheduleCalendarType.CRON;
            }
        };

        final OneTimeCalendar oneTimeCalendar = new OneTimeCalendar()
        {
            @Override
            public Instant getValue()
            {
                return Instant.parse( "2016-11-02T10:36:00Z" );
            }

            @Override
            public ScheduleCalendarType getType()
            {
                return ScheduleCalendarType.ONE_TIME;
            }

            @Override
            public Optional<Instant> nextExecution( final Instant instant )
            {
                return Optional.empty();
            }
        };

        final PropertyTree config = new PropertyTree();
        config.addString( "string", "value" );

        final ScheduledJob job1 = ScheduledJob.create().
            name( ScheduledJobName.from( "test1" ) ).
            descriptor( descriptor ).
            calendar( cronCalendar ).
            config( config ).
            enabled( true ).
            description( "description" ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:modifier" ) ).
            user( PrincipalKey.from( "user:system:user" ) ).
            lastRun( Instant.parse( "2012-01-01T00:00:00.00Z" ) ).
            lastTaskId( TaskId.from( "task-id" ) ).
            createdTime( Instant.parse( "2010-01-01T00:00:00.00Z" ) ).
            modifiedTime( Instant.parse( "2011-02-01T00:00:00.00Z" ) ).
            lastRun( Instant.parse( "2012-01-01T00:00:00.00Z" ) ).
            build();

        final ScheduledJob job2 = ScheduledJob.create().
            name( ScheduledJobName.from( "test2" ) ).
            descriptor( descriptor ).
            calendar( oneTimeCalendar ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:modifier" ) ).
            createdTime( Instant.parse( "2010-01-01T00:00:00.00Z" ) ).
            modifiedTime( Instant.parse( "2011-02-01T00:00:00.00Z" ) ).
            build();

        Mockito.when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );

        final String result = request().path( "scheduler/list" ).
            get().
            getAsString();

        assertJson( "list_scheduled_jobs.json", result );
    }

}
