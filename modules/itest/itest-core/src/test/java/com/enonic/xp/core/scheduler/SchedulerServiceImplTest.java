package com.enonic.xp.core.scheduler;

import java.time.Instant;
import java.util.Set;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.scheduler.CalendarServiceImpl;
import com.enonic.xp.impl.scheduler.ScheduleAuditLogExecutorImpl;
import com.enonic.xp.impl.scheduler.ScheduleAuditLogSupportImpl;
import com.enonic.xp.impl.scheduler.SchedulerConfig;
import com.enonic.xp.impl.scheduler.SchedulerExecutorService;
import com.enonic.xp.impl.scheduler.SchedulerServiceImpl;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerConstants;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchedulerServiceImplTest
    extends AbstractNodeTest
{
    private static final User REPO_TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-test-user" ) ).login( "repo-test-user" ).build();

    private static final AuthenticationInfo REPO_TEST_ADMIN_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.ADMIN ).
        user( REPO_TEST_DEFAULT_USER ).
        build();

    @Mock
    private SchedulerExecutorService schedulerExecutorService;

    private SchedulerServiceImpl schedulerService;

    private CalendarServiceImpl calendarService;

    private static Context adminContext()
    {
        return ContextBuilder.create().
            branch( "master" ).
            repositoryId( SchedulerConstants.SCHEDULER_REPO_ID ).
            authInfo( REPO_TEST_ADMIN_USER_AUTHINFO ).
            build();
    }

    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        super.setUpNode();

        final AuditLogService auditLogService = Mockito.mock( AuditLogService.class );

        final SchedulerConfig schedulerConfig = Mockito.mock( SchedulerConfig.class );
        Mockito.when( schedulerConfig.auditlogEnabled() ).thenReturn( Boolean.TRUE );

        final ScheduleAuditLogSupportImpl auditLogSupport =
            new ScheduleAuditLogSupportImpl( schedulerConfig, new ScheduleAuditLogExecutorImpl(), auditLogService );

        schedulerService =
            new SchedulerServiceImpl( indexService, repositoryService, nodeService, schedulerExecutorService, auditLogSupport );

        adminContext().runWith( () -> schedulerService.initialize() );

        calendarService = new CalendarServiceImpl();
    }

    @Test
    void create()
        throws Exception
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );
        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" );
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getDefault() );
        final PropertyTree config = new PropertyTree();
        config.addString( "string", "value" );
        final PrincipalKey user = PrincipalKey.from( "user:system:user" );

        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( name ).
            descriptor( descriptor ).
            calendar( calendar ).
            config( config ).
            description( "description" ).
            enabled( true ).
            user( user ).
            build();

        final Instant now = Instant.now();

        Thread.sleep( 100 );

        final ScheduledJob scheduledJob = adminContext().callWith( () -> schedulerService.create( params ) );

        assertEquals( name, scheduledJob.getName() );
        assertEquals( descriptor, scheduledJob.getDescriptor() );
        assertEquals( calendar.getCronValue(), ( (CronCalendar) scheduledJob.getCalendar() ).getCronValue() );
        assertEquals( calendar.getTimeZone(), ( (CronCalendar) scheduledJob.getCalendar() ).getTimeZone() );
        assertEquals( config, scheduledJob.getConfig() );
        assertEquals( "description", scheduledJob.getDescription() );
        assertEquals( user, scheduledJob.getUser() );
        assertEquals( "user:system:repo-test-user", scheduledJob.getModifier().toString() );
        assertEquals( "user:system:repo-test-user", scheduledJob.getCreator().toString() );
        assertTrue( now.isBefore( scheduledJob.getCreatedTime() ) );
        assertTrue( now.isBefore( scheduledJob.getModifiedTime() ) );
        assertTrue( scheduledJob.isEnabled() );
    }

    @Test
    void createOneTimeJob()
        throws Exception
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );
        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" );
        final ScheduleCalendar calendar = calendarService.oneTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );
        final PropertyTree config = new PropertyTree();
        config.addString( "string", "value" );

        final PrincipalKey user = PrincipalKey.from( "user:system:user" );

        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( name ).
            descriptor( descriptor ).
            calendar( calendar ).
            config( config ).
            description( "description" ).
            enabled( true ).
            user( user ).
            build();

        final Instant now = Instant.now();

        Thread.sleep( 100 );

        final ScheduledJob scheduledJob = adminContext().callWith( () -> schedulerService.create( params ) );

        assertEquals( "2021-02-25T10:44:33.170079900Z", ( (OneTimeCalendar) scheduledJob.getCalendar() ).getValue().toString() );
        assertEquals( ScheduleCalendarType.ONE_TIME, scheduledJob.getCalendar().getType() );
        assertEquals( "user:system:repo-test-user", scheduledJob.getCreator().toString() );
        assertEquals( "user:system:repo-test-user", scheduledJob.getModifier().toString() );
        assertTrue( now.isBefore( scheduledJob.getModifiedTime() ) );
        assertTrue( now.isBefore( scheduledJob.getCreatedTime() ) );
    }

    @Test
    void createWithoutAccess()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getDefault() );

        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( name ).
            calendar( calendar ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" ) ).
            config( new PropertyTree() ).
            build();

        assertThrows( NodeAccessException.class, () -> schedulerService.create( params ) );
    }

    @Test
    void createExisted()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getDefault() );

        final CreateScheduledJobParams params = CreateScheduledJobParams.create().
            name( name ).
            calendar( calendar ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" ) ).
            config( new PropertyTree() ).
            build();

        adminContext().runWith( () -> schedulerService.create( params ) );

        assertThrows( NodeAlreadyExistAtPathException.class, () -> adminContext().runWith( () -> schedulerService.create( params ) ) );
    }

    @Test
    void modifyNotCreated()
    {
        assertThrows( NodeNotFoundException.class,
                      () -> adminContext().runWith( () -> schedulerService.modify( ModifyScheduledJobParams.create().
                          name( ScheduledJobName.from( "nonExist" ) ).
                          editor( edit -> edit.enabled = false ).
                          build() ) ) );

    }

    @Test
    void modifyWithoutAccess()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) ) ).
            config( new PropertyTree() ).
            enabled( true ).
            build() ) );

        adminContext().callWith( () -> schedulerService.modify( ModifyScheduledJobParams.create().
            name( name ).
            editor( edit -> edit.enabled = false ).
            build() ) );
    }

    @Test
    void modifyWithDispose()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) ) ).
            config( new PropertyTree() ).
            enabled( true ).
            build() ) );

        adminContext().callWith( () -> schedulerService.modify( ModifyScheduledJobParams.create().
            name( name ).
            editor( edit -> edit.enabled = false ).
            build() ) );

        verify( schedulerExecutorService, times( 1 ) ).dispose( eq( "test" ) );
    }

    @Test
    void modify()
        throws Exception
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) ) ).
            config( new PropertyTree() ).
            build() ) );

        final Instant now = Instant.now();

        Thread.sleep( 100 );

        final User user = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final Context adminContext = adminContext();
        final Context userAdminContext = ContextBuilder.from( adminContext ).authInfo(
            AuthenticationInfo.copyOf( adminContext.getAuthInfo() ).user( user ).build() ).build();

        final ScheduledJob modifiedJob = userAdminContext.callWith( () -> schedulerService.modify( ModifyScheduledJobParams.create().
            name( name ).
            editor( edit -> {
                edit.enabled = true;
                edit.description = "new description";
                edit.descriptor = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task2" );
                edit.config.addString( "string", "value" );
                edit.user = PrincipalKey.from( "user:provider:user" );
                edit.calendar = calendarService.oneTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );
            } ).
            build() ) );

        assertEquals( name, modifiedJob.getName() );
        assertEquals( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task2" ), modifiedJob.getDescriptor() );
        assertEquals( "new description", modifiedJob.getDescription() );
        assertEquals( "2021-02-25T10:44:33.170079900Z", ( (OneTimeCalendar) modifiedJob.getCalendar() ).getValue().toString() );
        assertEquals( ScheduleCalendarType.ONE_TIME, modifiedJob.getCalendar().getType() );
        assertEquals( "value", modifiedJob.getConfig().getString( "string" ) );
        assertEquals( PrincipalKey.from( "user:provider:user" ), modifiedJob.getUser() );
        assertEquals( user.getKey(), modifiedJob.getModifier() );
        assertTrue( now.isBefore( modifiedJob.getModifiedTime() ) );
    }

    @Test
    void deleteNotCreated()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );
        final boolean deleted = schedulerService.delete( name );

        assertFalse( deleted );
    }

    @Test
    void deleteWithoutAccess()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) ) ).
            config( new PropertyTree() ).
            build() ) );

        final boolean deleted = schedulerService.delete( name );
        assertFalse( deleted );
    }

    @Test
    void deleteWithDispose()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) ) ).
            config( new PropertyTree() ).
            build() ) );

        mockFuture( name );

        adminContext().callWith( () -> schedulerService.delete( name ) );

        verify( schedulerExecutorService, times( 1 ) ).dispose( eq( "test" ) );
    }

    @Test
    void delete()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) ) ).
            config( new PropertyTree() ).
            build() ) );

        final boolean deleted = adminContext().callWith( () -> schedulerService.delete( name ) );

        assertTrue( deleted );
    }

    @Test
    void getNotCreated()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        assertNull( schedulerService.get( name ) );
    }

    @Test
    void getWithoutAccess()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) ) ).
            config( new PropertyTree() ).
            build() ) );

        assertNull( schedulerService.get( name ) );
    }

    @Test
    void get()
    {
        final ScheduledJobName name = ScheduledJobName.from( "test" );
        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" );
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getDefault() );
        final PropertyTree config = new PropertyTree();
        config.addString( "string", "value" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( descriptor ).
            calendar( calendar ).
            config( config ).
            description( "description" ).
            build() ) );

        final ScheduledJob scheduledJob = adminContext().callWith( () -> schedulerService.get( name ) );

        assertEquals( name, scheduledJob.getName() );
        assertEquals( descriptor, scheduledJob.getDescriptor() );
        assertEquals( calendar.getCronValue(), ( (CronCalendar) scheduledJob.getCalendar() ).getCronValue() );
        assertEquals( calendar.getTimeZone(), ( (CronCalendar) scheduledJob.getCalendar() ).getTimeZone() );
        assertEquals( config, scheduledJob.getConfig() );
        assertEquals( "description", scheduledJob.getDescription() );
    }

    @Test
    void listWithoutAccess()
    {
        assertEquals( 0, schedulerService.list().size() );

        final ScheduledJobName name = ScheduledJobName.from( "test" );
        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" );
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getDefault() );
        final PropertyTree config = new PropertyTree();
        config.addString( "string", "value" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( name ).
            descriptor( descriptor ).
            calendar( calendar ).
            config( config ).
            description( "description" ).
            build() ) );

        assertEquals( 0, schedulerService.list().size() );
    }

    @Test
    void list()
    {
        assertEquals( 0, adminContext().callWith( () -> schedulerService.list() ).size() );

        final DescriptorKey descriptor = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.features" ), "landing" );
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getDefault() );
        final PropertyTree config = new PropertyTree();
        config.addString( "string", "value" );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "test1" ) ).
            descriptor( descriptor ).
            calendar( calendar ).
            config( config ).
            description( "description" ).
            build() ) );

        assertEquals( 1, adminContext().callWith( () -> schedulerService.list() ).size() );

        adminContext().callWith( () -> schedulerService.create( CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "test2" ) ).
            descriptor( descriptor ).
            calendar( calendar ).
            config( config ).
            description( "description" ).
            build() ) );

        assertEquals( 2, adminContext().callWith( () -> schedulerService.list() ).size() );
    }

    private IScheduledFuture<?> mockFuture( final ScheduledJobName name )
    {
        final ScheduledTaskHandler handler = mock( ScheduledTaskHandler.class );
        final IScheduledFuture<?> future = mock( IScheduledFuture.class );

        when( future.getHandler() ).thenReturn( handler );
        when( handler.getTaskName() ).thenReturn( name.getValue() );

        when( schedulerExecutorService.getAllFutures() ).
            thenReturn( Set.of( name.getValue() ) );

        return future;
    }
}
