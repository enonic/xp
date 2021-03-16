package com.enonic.xp.lib.scheduler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.common.collect.Maps;

import com.enonic.xp.core.impl.PropertyTreeMarshallerServiceFactory;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.EditableScheduledJob;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.testing.ScriptTestSupport;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class BaseScheduledJobHandlerTest
    extends ScriptTestSupport
{
    protected SchedulerService schedulerService;

    protected CalendarService calendarService;

    protected PropertyTreeMarshallerService propertyTreeMarshallerService;

    private void mockJob()
    {
        final Map<SchedulerName, ScheduledJob> jobs = Maps.newHashMap();

        Mockito.when( schedulerService.create( Mockito.isA( CreateScheduledJobParams.class ) ) ).thenAnswer( invocation -> {
            final CreateScheduledJobParams params = invocation.getArgument( 0 );

            final ScheduledJob job = ScheduledJob.create().
                name( params.getName() ).
                descriptor( params.getDescriptor() ).
                description( params.getDescription() ).
                calendar( params.getCalendar() ).
                payload( params.getPayload() ).
                enabled( params.isEnabled() ).
                user( params.getUser() ).
                author( params.getAuthor() ).
                build();

            jobs.put( job.getName(), job );

            return job;
        } );

        Mockito.when( schedulerService.modify( Mockito.isA( ModifyScheduledJobParams.class ) ) ).thenAnswer( invocation -> {
            final ModifyScheduledJobParams params = invocation.getArgument( 0 );

            final EditableScheduledJob editableJob = new EditableScheduledJob( jobs.get( params.getName() ) );

            params.getEditor().edit( editableJob );

            final ScheduledJob modifiedJob = editableJob.build();
            jobs.put( params.getName(), modifiedJob );

            return modifiedJob;
        } );

        Mockito.when( schedulerService.delete( Mockito.isA( SchedulerName.class ) ) ).thenAnswer( invocation -> {
            final SchedulerName name = invocation.getArgument( 0 );

            final ScheduledJob job = jobs.remove( name );

            return job != null;
        } );

        Mockito.when( schedulerService.get( Mockito.isA( SchedulerName.class ) ) ).thenAnswer(
            invocation -> jobs.get( invocation.getArgument( 0 ) ) );

        Mockito.when( schedulerService.list() ).thenAnswer( invocation -> new ArrayList<>( jobs.values() ) );
    }

    protected void mockOneTimeCalendar()
    {
        Mockito.when( calendarService.oneTime( Mockito.isA( Instant.class ) ) ).thenAnswer( invocation -> {
            final OneTimeCalendar oneTime = Mockito.mock( OneTimeCalendar.class );

            Mockito.when( oneTime.getType() ).thenReturn( ScheduleCalendarType.ONE_TIME );
            Mockito.when( oneTime.getValue() ).thenReturn( invocation.getArgument( 0 ) );

            return oneTime;
        } );
    }

    protected void mockCronCalendar()
    {
        Mockito.when( calendarService.cron( Mockito.anyString(), Mockito.isA( TimeZone.class ) ) ).thenAnswer( invocation -> {
            final CronCalendar cron = Mockito.mock( CronCalendar.class );

            Mockito.when( cron.getType() ).thenReturn( ScheduleCalendarType.CRON );
            Mockito.when( cron.getCronValue() ).thenReturn( invocation.getArgument( 0 ) );
            Mockito.when( cron.getTimeZone() ).thenReturn( invocation.getArgument( 1 ) );

            return cron;
        } );
    }

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.schedulerService = Mockito.mock( SchedulerService.class );
        this.calendarService = Mockito.mock( CalendarService.class );
        this.propertyTreeMarshallerService = PropertyTreeMarshallerServiceFactory.newInstance( Mockito.mock( MixinService.class ) );

        addService( CalendarService.class, this.calendarService );
        addService( SchedulerService.class, this.schedulerService );
        addService( PropertyTreeMarshallerService.class, this.propertyTreeMarshallerService );

        mockJob();
    }
}
