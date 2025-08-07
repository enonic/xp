package com.enonic.xp.lib.scheduler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.testing.ScriptTestSupport;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class BaseScheduledJobHandlerTest
    extends ScriptTestSupport
{
    protected SchedulerService schedulerService;

    protected CalendarService calendarService;

    protected PropertyTreeMarshallerService propertyTreeMarshallerService;

    private Map<ScheduledJobName, ScheduledJob> jobs;

    private void mockJob()
    {
        jobs = new HashMap<>();

        Mockito.when( schedulerService.create( Mockito.isA( CreateScheduledJobParams.class ) ) ).thenAnswer( invocation -> {
            final CreateScheduledJobParams params = invocation.getArgument( 0 );

            final ScheduledJob job = ScheduledJob.create().
                name( params.getName() ).
                descriptor( params.getDescriptor() ).
                description( params.getDescription() ).
                calendar( params.getCalendar() ).
                config( params.getConfig() ).
                enabled( params.isEnabled() ).
                user( params.getUser() ).
                creator( PrincipalKey.from( "user:system:creator" ) ).
                modifier( PrincipalKey.from( "user:system:creator" ) ).
                createdTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                modifiedTime( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                build();

            jobs.put( job.getName(), job );

            return job;
        } );

        Mockito.when( schedulerService.modify( Mockito.isA( ModifyScheduledJobParams.class ) ) ).thenAnswer( invocation -> {
            final ModifyScheduledJobParams params = invocation.getArgument( 0 );

            final EditableScheduledJob editableJob = new EditableScheduledJob( jobs.get( params.getName() ) );

            params.getEditor().edit( editableJob );

            ScheduledJob modifiedJob = editableJob.build();

            modifiedJob = ScheduledJob.create().
                name( modifiedJob.getName() ).
                description( modifiedJob.getDescription() ).
                calendar( modifiedJob.getCalendar() ).
                enabled( modifiedJob.isEnabled() ).
                descriptor( modifiedJob.getDescriptor() ).
                config( modifiedJob.getConfig() ).
                user( modifiedJob.getUser() ).
                creator( modifiedJob.getCreator() ).
                createdTime( modifiedJob.getCreatedTime() ).
                modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
                modifier( PrincipalKey.from( "user:system:modifier" ) ).
                build();

            jobs.put( params.getName(), modifiedJob );

            return modifiedJob;
        } );

        Mockito.when( schedulerService.delete( Mockito.isA( ScheduledJobName.class ) ) ).thenAnswer( invocation -> {
            final ScheduledJobName name = invocation.getArgument( 0 );

            final ScheduledJob job = jobs.remove( name );

            return job != null;
        } );

        Mockito.when( schedulerService.get( Mockito.isA( ScheduledJobName.class ) ) )
            .thenAnswer( invocation -> jobs.get( invocation.getArgument( 0 ) ) );

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

    protected void updateLastRun( final ScheduledJobName name )
    {
        final ScheduledJob existJob = jobs.get( name );

        final ScheduledJob modifiedJob = ScheduledJob.create().
            name( existJob.getName() ).
            description( existJob.getDescription() ).
            calendar( existJob.getCalendar() ).
            enabled( existJob.isEnabled() ).
            descriptor( existJob.getDescriptor() ).
            config( existJob.getConfig() ).
            user( existJob.getUser() ).
            creator( existJob.getCreator() ).
            createdTime( existJob.getCreatedTime() ).
            modifier( existJob.getModifier() ).
            modifiedTime( existJob.getModifiedTime() ).
            lastTaskId( TaskId.from( "task-id" ) ).
            lastRun( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            build();

        jobs.put( name, modifiedJob );
    }

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.schedulerService = Mockito.mock( SchedulerService.class );
        this.calendarService = Mockito.mock( CalendarService.class );
        this.propertyTreeMarshallerService = PropertyTreeMarshallerServiceFactory.newInstance();

        addService( CalendarService.class, this.calendarService );
        addService( SchedulerService.class, this.schedulerService );
        addService( PropertyTreeMarshallerService.class, this.propertyTreeMarshallerService );

        mockJob();
    }
}
