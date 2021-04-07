package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.core.impl.PropertyTreeMarshallerServiceFactory;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigImplTest
{
    private SchedulerConfig schedulerConfig;

    private PropertyTreeMarshallerService propertyTreeMarshallerService;

    private CalendarService calendarService;

    @BeforeEach
    void setUp()
    {
        this.propertyTreeMarshallerService = PropertyTreeMarshallerServiceFactory.newInstance( Mockito.mock( MixinService.class ) );
        this.calendarService = new CalendarServiceImpl();
    }

    @Test
    void cronJob()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "job.landing1.enabled", "true" );
        properties.put( "job.landing1.description", "landing1 description" );
        properties.put( "job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "job.landing1.user", "user:system:user" );
        properties.put( "job.landing1.author", "user:system:author" );
        properties.put( "job.landing1.payload", "{\"a\":\"valueA\"}" );
        properties.put( "job.landing1.calendar.type", "cron" );
        properties.put( "job.landing1.calendar.value", "* * * * *" );
        properties.put( "job.landing1.calendar.timezone", "GMT+5:30" );

        schedulerConfig = new SchedulerConfigImpl( properties, propertyTreeMarshallerService, calendarService );
        final Set<CreateScheduledJobParams> jobs = schedulerConfig.jobs();

        assertEquals( 1, jobs.size() );

        final CreateScheduledJobParams job = jobs.stream().findFirst().orElseThrow( RuntimeException::new );

        assertTrue( job.isEnabled() );
        assertEquals( PrincipalKey.from( "user:system:author" ), job.getAuthor() );
        assertEquals( PrincipalKey.from( "user:system:user" ), job.getUser() );
        assertEquals( DescriptorKey.from( "com.enonic.app.features:landing" ), job.getDescriptor() );
        assertEquals( "landing1 description", job.getDescription() );
        assertEquals( "valueA", job.getPayload().getString( "a" ) );
        assertEquals( ScheduleCalendarType.CRON, job.getCalendar().getType() );
        assertEquals( "* * * * *", ( (CronCalendar) job.getCalendar() ).getCronValue() );
        assertEquals( TimeZone.getTimeZone( "GMT+5:30" ), ( (CronCalendar) job.getCalendar() ).getTimeZone() );

    }

    @Test
    void oneTimeJob()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "job.landing1.enabled", "false" );
        properties.put( "job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "job.landing1.calendar.type", "one_time" );
        properties.put( "job.landing1.calendar.value", "2012-01-01T00:00:00.00Z" );

        schedulerConfig = new SchedulerConfigImpl( properties, propertyTreeMarshallerService, calendarService );
        final Set<CreateScheduledJobParams> jobs = schedulerConfig.jobs();

        assertEquals( 1, jobs.size() );

        final CreateScheduledJobParams job = jobs.stream().findFirst().orElseThrow( RuntimeException::new );

        assertFalse( job.isEnabled() );
        assertNull( job.getAuthor() );
        assertNull( job.getUser() );
        assertEquals( DescriptorKey.from( "com.enonic.app.features:landing" ), job.getDescriptor() );
        assertNull( job.getDescription() );
        assertEquals( 0, job.getPayload().getTotalSize() );
        assertEquals( ScheduleCalendarType.ONE_TIME, job.getCalendar().getType() );
        assertEquals( Instant.parse( "2012-01-01T00:00:00.00Z" ), ( (OneTimeCalendar) job.getCalendar() ).getValue() );
    }


    @Test
    void invalidProperty()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "job.landing1.enabled", "true" );
        properties.put( "job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "job.landing1.calendar.type", "one_time" );
        properties.put( "job.landing1.calendar.value", "2012-01-01T00:00:00.00Z" );
        properties.put( "job.landing1.invalid", "some value" );

        schedulerConfig = new SchedulerConfigImpl( properties, propertyTreeMarshallerService, calendarService );

        final RuntimeException ex = assertThrows( RuntimeException.class, () -> schedulerConfig.jobs() );
        assertEquals( "[invalid] is invalid job property.", ex.getMessage() );

    }

    @Test
    void invalidCalendarProperty()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "job.landing1.enabled", "true" );
        properties.put( "job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "job.landing1.calendar.type", "one_time" );
        properties.put( "job.landing1.calendar.value", "2012-01-01T00:00:00.00Z" );
        properties.put( "job.landing1.calendar.invalid", "abc" );

        schedulerConfig = new SchedulerConfigImpl( properties, propertyTreeMarshallerService, calendarService );

        final RuntimeException ex = assertThrows( RuntimeException.class, () -> schedulerConfig.jobs() );
        assertEquals( "[invalid] is invalid calendar property.", ex.getMessage() );

    }

    @Test
    void invalidPayload()
    {
        Map<String, String> properties = new HashMap<>();

        properties.put( "job.landing1.enabled", "true" );
        properties.put( "job.landing1.descriptor", "com.enonic.app.features:landing" );
        properties.put( "job.landing1.calendar.type", "one_time" );
        properties.put( "job.landing1.calendar.value", "2012-01-01T00:00:00.00Z" );
        properties.put( "job.landing1.payload", "{'a':'b'}" );

        schedulerConfig = new SchedulerConfigImpl( properties, propertyTreeMarshallerService, calendarService );
        final RuntimeException ex = assertThrows( RuntimeException.class, () -> schedulerConfig.jobs() );

        assertEquals( "Unexpected character (''' (code 39)): was expecting double-quote to start field name\n" +
                          " at [Source: (String)\"{'a':'b'}\"; line: 1, column: 3]", ex.getCause().getMessage() );
    }
}
