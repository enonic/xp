package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.impl.scheduler.distributed.CronCalendarImpl;
import com.enonic.xp.impl.scheduler.distributed.OneTimeCalendarImpl;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScheduleAuditLogSupportImplTest
{
    @Mock
    private AuditLogService auditLogService;

    @Mock
    private SchedulerConfig config;

    private ExecutorService executor;

    private ScheduleAuditLogSupportImpl support;

    private ScheduledJobName name;

    private User user;

    private Context context;

    @BeforeEach
    void setUp()
    {
        Mockito.when( config.auditlogEnabled() ).thenReturn( true );

        executor = Executors.newSingleThreadExecutor();

        support = new ScheduleAuditLogSupportImpl( config, executor, auditLogService );

        name = ScheduledJobName.from( "job-name" );

        user = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "testUser" ) ).
            displayName( "Test User" ).
            modifiedTime( Instant.now() ).
            email( "test-user@enonic.com" ).
            login( "test-user" ).
            build();

        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( user ).
            build();

        context = ContextBuilder.create().
            authInfo( authInfo ).
            build();

    }

    @Test
    void testCreateJob()
        throws Exception
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "property", "value" );

        final CronCalendarImpl calendar = CronCalendarImpl.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getTimeZone( "GMT+5:30" ) ).
            build();

        final DescriptorKey descriptor = DescriptorKey.from( "appKey:descriptorName" );

        final String jobDescription = "Job description";

        final PrincipalKey userKey = PrincipalKey.from( "user:system:user" );

        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( name ).
            descriptor( descriptor ).
            description( jobDescription ).
            calendar( calendar ).
            config( config ).
            user( userKey ).
            enabled( true ).
            build();

        final ScheduledJob job = ScheduledJob.create().
            name( name ).
            calendar( calendar ).
            descriptor( descriptor ).
            description( jobDescription ).
            config( config ).
            enabled( true ).
            user( userKey ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            lastRun( Instant.parse( "2021-03-25T10:44:33.170079900Z" ) ).
            lastTaskId( TaskId.from( "task-id" ) ).
            build();

        context.runWith( () -> support.create( params, job ) );

        executor.shutdown();
        executor.awaitTermination( 1, TimeUnit.MINUTES );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        assertEquals( user.getKey(), argumentCaptor.getValue().getUser() );
        assertEquals( "system.scheduler:master:job-name", argumentCaptor.getValue().getObjectUris().first().getValue() );
        assertEquals( "com.enonic.xp.core-scheduler", argumentCaptor.getValue().getSource() );
        assertEquals( userKey.toString(), argumentCaptor.getValue().getData().getSet( "result" ).getString( "user" ) );
        assertEquals( job.getCreator().toString(), argumentCaptor.getValue().getData().getSet( "result" ).getString( "creator" ) );
        assertEquals( job.getModifier().toString(), argumentCaptor.getValue().getData().getSet( "result" ).getString( "modifier" ) );
        assertEquals( job.getCreatedTime(), argumentCaptor.getValue().getData().getSet( "result" ).getInstant( "createdTime" ) );
        assertEquals( job.getModifiedTime(), argumentCaptor.getValue().getData().getSet( "result" ).getInstant( "modifiedTime" ) );
        assertEquals( name.getValue(), argumentCaptor.getValue().getData().getSet( "result" ).getString( "name" ) );
        assertEquals( jobDescription, argumentCaptor.getValue().getData().getSet( "result" ).getString( "description" ) );
        assertEquals( descriptor.toString(), argumentCaptor.getValue().getData().getSet( "result" ).getString( "descriptor" ) );
        assertEquals( calendar.getType().name(),
                      argumentCaptor.getValue().getData().getSet( "result" ).getSet( "calendar" ).getString( "type" ) );
        assertEquals( calendar.getCronValue(),
                      argumentCaptor.getValue().getData().getSet( "result" ).getSet( "calendar" ).getString( "value" ) );
        assertEquals( calendar.getTimeZone().getID(),
                      argumentCaptor.getValue().getData().getSet( "result" ).getSet( "calendar" ).getString( "timezone" ) );
        assertEquals( true, argumentCaptor.getValue().getData().getSet( "result" ).getBoolean( "enabled" ) );
        assertEquals( config.getRoot(), argumentCaptor.getValue().getData().getSet( "result" ).getSet( "config" ) );
        assertNull( argumentCaptor.getValue().getData().getSet( "result" ).getInstant( "lastRun" ) );
        assertNull( argumentCaptor.getValue().getData().getSet( "result" ).getString( "lastTaskId" ) );
    }

    @Test
    void testUpdateContent()
        throws Exception
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "property", "value" );

        final CronCalendarImpl calendar = CronCalendarImpl.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getTimeZone( "GMT+5:30" ) ).
            build();

        final DescriptorKey descriptor = DescriptorKey.from( "appKey:descriptorName" );

        final String jobDescription = "Job description";

        final PrincipalKey userKey = PrincipalKey.from( "user:system:user" );

        final OneTimeCalendarImpl oneTimeCalendar =
            OneTimeCalendarImpl.create().value( Instant.parse( "2021-04-25T10:44:33.170079900Z" ) ).build();

        final ModifyScheduledJobParams params = ModifyScheduledJobParams.create().
            name( name ).
            editor( edit -> edit.calendar = oneTimeCalendar ).
            build();

        final ScheduledJob job = ScheduledJob.create().
            name( name ).
            calendar( oneTimeCalendar ).
            descriptor( descriptor ).
            description( jobDescription ).
            config( config ).
            enabled( true ).
            user( userKey ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            build();

        context.runWith( () -> support.modify( params, job ) );

        executor.shutdown();
        executor.awaitTermination( 1, TimeUnit.MINUTES );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        assertEquals( user.getKey().toString(), argumentCaptor.getValue().getData().getSet( "params" ).getString( "modifier" ) );
        assertEquals( "2021-04-25T10:44:33.170079900Z",
                      argumentCaptor.getValue().getData().getSet( "result" ).getSet( "calendar" ).getString( "value" ) );
    }

    @Test
    void testUpdateWithoutCreator()// jobs were produced with an empty `creator` and `createdTime` fields from 7.7.0 to 7.7.2
    throws Exception
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "property", "value" );

        final CronCalendarImpl calendar = CronCalendarImpl.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getTimeZone( "GMT+5:30" ) ).
            build();

        final DescriptorKey descriptor = DescriptorKey.from( "appKey:descriptorName" );

        final String jobDescription = "Job description";

        final PrincipalKey userKey = PrincipalKey.from( "user:system:user" );

        final OneTimeCalendarImpl oneTimeCalendar =
            OneTimeCalendarImpl.create().value( Instant.parse( "2021-04-25T10:44:33.170079900Z" ) ).build();

        final ModifyScheduledJobParams params = ModifyScheduledJobParams.create().
            name( name ).
            editor( edit -> edit.calendar = oneTimeCalendar ).
            build();

        final ScheduledJob job = ScheduledJob.create()
            .name( name )
            .calendar( oneTimeCalendar )
            .descriptor( descriptor )
            .description( jobDescription )
            .config( config )
            .enabled( true )
            .user( userKey )
            .modifier( PrincipalKey.from( "user:system:creator" ) )
            .createdTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
            .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
            .build();

        context.runWith( () -> support.modify( params, job ) );

        executor.shutdown();
        executor.awaitTermination( 1, TimeUnit.MINUTES );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        assertNull( argumentCaptor.getValue().getData().getSet( "params" ).getString( "creator" ) );
    }

    @Test
    void delete()
        throws Exception
    {
        context.runWith( () -> support.delete( name, true ) );

        executor.shutdown();
        executor.awaitTermination( 1, TimeUnit.MINUTES );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        assertTrue( argumentCaptor.getValue().getData().getSet( "result" ).getBoolean( "value" ) );
        assertEquals( name.getValue(), argumentCaptor.getValue().getData().getSet( "result" ).getString( "name" ) );
    }

    @Test
    void auditLogDisabled()
        throws Exception
    {
        Mockito.when( config.auditlogEnabled() ).thenReturn( false );
        support = new ScheduleAuditLogSupportImpl( config, executor, auditLogService );

        context.runWith( () -> support.delete( name, true ) );

        executor.shutdown();
        executor.awaitTermination( 1, TimeUnit.MINUTES );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, never() ).log( argumentCaptor.capture() );
    }
}
