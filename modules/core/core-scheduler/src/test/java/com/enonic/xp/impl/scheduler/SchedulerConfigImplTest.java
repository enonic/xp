package com.enonic.xp.impl.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.security.PrincipalKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigImplTest
{
    private SchedulerConfig schedulerConfig;


    private CalendarService calendarService;

    @BeforeEach
    void setUp()
    {
        this.calendarService = new CalendarServiceImpl();
    }

    @Test
    void cronJob()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "init-job.landing1.enabled", "true" );
        properties.put( "init-job.landing1.description", "landing1 description" );
        properties.put( "init-job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "init-job.landing1.user", "system:user" );
        properties.put( "init-job.landing1.config", "{\"a\":\"valueA\"}" );
        properties.put( "init-job.landing1.cron", "* * * * *" );
        properties.put( "init-job.landing1.timezone", "GMT+5:30" );

        schedulerConfig = new SchedulerConfigImpl( properties, calendarService );
        final Set<CreateScheduledJobParams> jobs = schedulerConfig.jobs();

        assertEquals( 2, jobs.size() );

        final CreateScheduledJobParams job = jobs.stream().
            filter( params -> params.getName().getValue().equals( "landing1" ) ).
            findAny().orElseThrow( RuntimeException::new );

        assertTrue( job.isEnabled() );
        assertEquals( PrincipalKey.from( "user:system:user" ), job.getUser() );
        assertEquals( DescriptorKey.from( "com.enonic.app.features:landing" ), job.getDescriptor() );
        assertEquals( "landing1 description", job.getDescription() );
        assertEquals( "valueA", job.getConfig().getString( "a" ) );
        assertEquals( ScheduleCalendarType.CRON, job.getCalendar().getType() );
        assertEquals( "* * * * *", ( (CronCalendar) job.getCalendar() ).getCronValue() );
        assertEquals( TimeZone.getTimeZone( "GMT+5:30" ), ( (CronCalendar) job.getCalendar() ).getTimeZone() );
    }

    @Test
    void defaultJob()
    {
        final Map<String, String> properties = new HashMap<>();

        schedulerConfig = new SchedulerConfigImpl( properties, calendarService );
        final Set<CreateScheduledJobParams> jobs = schedulerConfig.jobs();

        assertEquals( 1, jobs.size() );

        final CreateScheduledJobParams job = jobs.stream().
            findAny().orElseThrow( RuntimeException::new );

        assertFalse( job.isEnabled() );
        assertEquals( DescriptorKey.from( "com.enonic.xp.app.system:audit-log-cleanup" ), job.getDescriptor() );
        assertEquals( ScheduleCalendarType.CRON, job.getCalendar().getType() );
        assertEquals( "0 5 * * *", ( (CronCalendar) job.getCalendar() ).getCronValue() );
        assertEquals( 1, job.getConfig().getTotalSize() );
        assertEquals( "PT2s", job.getConfig().getProperty( "ageThreshold" ).getString() );
        assertEquals( "user:system:custom", job.getUser().toString() );
    }


    @Test
    void invalidProperty()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "init-job.landing1.enabled", "true" );
        properties.put( "init-job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "init-job.landing1.calendar.type", "one_time" );
        properties.put( "init-job.landing1.calendar.value", "2012-01-01T00:00:00.00Z" );
        properties.put( "init-job.landing1.invalid", "some value" );

        schedulerConfig = new SchedulerConfigImpl( properties, calendarService );

        // the entry is left out rather than taken as a reason to ignore the rest of the file
        assertThat( jobNames( schedulerConfig ) ).doesNotContain( "landing1" ).contains( "audit-log-cleanup" );
    }

    @Test
    void invalidConfig()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "init-job.landing1.enabled", "true" );
        properties.put( "init-job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "init-job.landing1.cron", "* * * * *" );
        properties.put( "init-job.landing1.config", "{'a':'b'}" );

        schedulerConfig = new SchedulerConfigImpl( properties, calendarService );

        assertThat( jobNames( schedulerConfig ) ).doesNotContain( "landing1" ).contains( "audit-log-cleanup" );
    }

    @Test
    void reconfigured()
    {
        final Map<String, String> properties = new HashMap<>();
        properties.put( "init-job.landing1.enabled", "true" );
        properties.put( "init-job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "init-job.landing1.cron", "* * * * *" );
        properties.put( "auditlog.enabled", "false" );

        final SchedulerConfigImpl schedulerConfig = new SchedulerConfigImpl( properties, calendarService );

        assertThat( jobNames( schedulerConfig ) ).contains( "landing1" ).doesNotContain( "landing2" );
        assertFalse( schedulerConfig.auditlogEnabled() );

        final Map<String, String> reconfigured = new HashMap<>();
        reconfigured.put( "init-job.landing2.enabled", "true" );
        reconfigured.put( "init-job.landing2.descriptor", "com.enonic.app.features:landing" );
        reconfigured.put( "init-job.landing2.cron", "0 5 * * *" );

        // applied in place, so what reads this sees the new values without being restarted
        schedulerConfig.modify( reconfigured );

        assertThat( jobNames( schedulerConfig ) ).contains( "landing2" ).doesNotContain( "landing1" );
        assertTrue( schedulerConfig.auditlogEnabled() );
    }

    private static Set<String> jobNames( final SchedulerConfig config )
    {
        return config.jobs().stream().map( params -> params.getName().getValue() ).collect( Collectors.toSet() );
    }
}
