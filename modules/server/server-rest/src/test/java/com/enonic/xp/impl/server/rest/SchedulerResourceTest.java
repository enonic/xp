package com.enonic.xp.impl.server.rest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;

public class SchedulerResourceTest
    extends ServerRestTestSupport
{
    private SchedulerService schedulerService;

    @Override
    protected SchedulerResource getResourceInstance()
    {
        schedulerService = Mockito.mock( SchedulerService.class );

        return new SchedulerResource( schedulerService );
    }

    @Test
    public void list()
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

            public Optional<Duration> nextExecution()
            {
                return Optional.of( Duration.ofSeconds( 50 ) );
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
            public Optional<Duration> nextExecution()
            {
                return Optional.of( Duration.ofSeconds( 50 ) );
            }

            @Override
            public ScheduleCalendarType getType()
            {
                return ScheduleCalendarType.ONE_TIME;
            }
        };

        final PropertyTree payload = new PropertyTree();
        payload.addString( "string", "value" );

        final ScheduledJob job1 = ScheduledJob.create().
            name( SchedulerName.from( "test1" ) ).
            descriptor( descriptor ).
            calendar( cronCalendar ).
            payload( payload ).
            enabled( true ).
            description( "description" ).
            author( PrincipalKey.from( "user:system:author" ) ).
            user( PrincipalKey.from( "user:system:user" ) ).
            lastRun( Instant.parse( "2012-01-01T00:00:00.00Z" ) ).
            build();

        final ScheduledJob job2 = ScheduledJob.create().
            name( SchedulerName.from( "test2" ) ).
            descriptor( descriptor ).
            calendar( oneTimeCalendar ).
            build();

        Mockito.when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );

        final String result = request().path( "scheduler/list" ).
            get().
            getAsString();

        assertJson( "list_scheduled_jobs.json", result );
    }

}
