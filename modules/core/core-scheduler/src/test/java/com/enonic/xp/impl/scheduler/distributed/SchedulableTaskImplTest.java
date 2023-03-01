package com.enonic.xp.impl.scheduler.distributed;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.scheduler.ScheduledJobPropertyNames;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.support.SerializableUtils;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SchedulableTaskImplTest
{
    @Mock(stubOnly = true)
    ServiceReference<TaskService> taskServiceReference;

    @Mock(stubOnly = true)
    ServiceReference<NodeService> nodeServiceReference;

    @Mock(stubOnly = true)
    ServiceReference<SecurityService> securityServiceReference;

    @Captor
    ArgumentCaptor<SubmitTaskParams> taskCaptor;

    @Mock
    private TaskService taskService;

    @Mock
    private NodeService nodeService;

    @Mock
    private SecurityService securityService;

    @Mock(stubOnly = true)
    private BundleContext bundleContext;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        final Bundle bundle = OsgiSupportMock.mockBundle();
        when( bundle.getBundleContext() ).thenReturn( bundleContext );
        when( bundleContext.getServiceReferences( TaskService.class, null ) ).thenReturn( List.of( taskServiceReference ) );
        when( bundleContext.getServiceReferences( NodeService.class, null ) ).thenReturn( List.of( nodeServiceReference ) );
        when( bundleContext.getServiceReferences( SecurityService.class, null ) ).thenReturn( List.of( securityServiceReference ) );
        when( bundleContext.getService( taskServiceReference ) ).thenReturn( taskService );
        when( bundleContext.getService( nodeServiceReference ) ).thenReturn( nodeService );
        when( bundleContext.getService( securityServiceReference ) ).thenReturn( securityService );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "task-id" ) );
    }

    @AfterEach
    void tearDown()
    {
        OsgiSupportMock.reset();
    }

    @Test
    public void taskCalledWithNonExistUser()
    {
        mockNode();

        final PropertyTree taskData = new PropertyTree();
        taskData.addString( "string", "value" );

        when( securityService.authenticate( isA( VerifiedUsernameAuthToken.class ) ) ).thenReturn( AuthenticationInfo.unAuthenticated() );

        final SchedulableTaskImpl task = createAndRunTask( ScheduledJobName.from( "task" ), DescriptorKey.from( "app:key" ), taskData,
                                                           PrincipalKey.from( "user:system:user" ) );
        assertEquals( "task", task.getName() );

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    public void taskCalledWithSetUser()
    {
        mockNode();

        final PropertyTree taskData = new PropertyTree();
        taskData.addString( "string", "value" );

        final User customUser = User.create().
            displayName( "Custom" ).
            key( PrincipalKey.from( "user:system:user" ) ).
            login( "custom" ).
            build();

        final AuthenticationInfo authenticationInfo = AuthenticationInfo.create().
            user( customUser ).
            principals( RoleKeys.AUTHENTICATED, RoleKeys.ADMIN ).
            build();

        when( securityService.authenticate( isA( VerifiedUsernameAuthToken.class ) ) ).thenReturn( authenticationInfo );

        final SchedulableTaskImpl task =
            createAndRunTask( ScheduledJobName.from( "task" ), DescriptorKey.from( "app:key" ), taskData, customUser.getKey() );
        assertEquals( "task", task.getName() );

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );

        assertEquals( DescriptorKey.from( "app:key" ), taskCaptor.getValue().getDescriptorKey() );
        assertEquals( taskData, taskCaptor.getValue().getData() );
    }

    @Test
    public void taskCalledWithoutSetUser()
    {
        mockNode();

        final PropertyTree taskData = new PropertyTree();
        taskData.addString( "string", "value" );

        final SchedulableTaskImpl task =
            createAndRunTask( ScheduledJobName.from( "task" ), DescriptorKey.from( "app:key" ), taskData, null );
        assertEquals( "task", task.getName() );

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );

        assertEquals( DescriptorKey.from( "app:key" ), taskCaptor.getValue().getDescriptorKey() );
        assertEquals( taskData, taskCaptor.getValue().getData() );
    }

    @Test
    public void taskFailed()
    {
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( RuntimeException.class );
        createAndRunTask( ScheduledJobName.from( "task" ), DescriptorKey.from( "app:key" ), new PropertyTree() );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( OutOfMemoryError.class );
        assertThrows( OutOfMemoryError.class,
                      () -> createAndRunTask( ScheduledJobName.from( "task" ), DescriptorKey.from( "app:key" ), new PropertyTree() ) );
    }

    @Test
    public void taskSerialized()
        throws Exception
    {
        mockNode();

        final PropertyTree data = new PropertyTree();
        data.addString( "string", "value" );

        final SchedulableTaskImpl task = createAndRunTask( ScheduledJobName.from( "task" ), DescriptorKey.from( "app:key" ), data );

        byte[] serialized = SerializableUtils.serialize( task );

        final SchedulableTaskImpl deserializedTask = (SchedulableTaskImpl) SerializableUtils.deserialize( serialized );

        assertEquals( task.getJob().getName(), deserializedTask.getJob().getName() );
        assertEquals( task.getJob().getConfig(), deserializedTask.getJob().getConfig() );
        assertEquals( task.getJob().getUser(), deserializedTask.getJob().getUser() );
        assertEquals( task.getJob().getDescriptor(), deserializedTask.getJob().getDescriptor() );
        assertEquals( task.getJob().getDescription(), deserializedTask.getJob().getDescription() );
        assertEquals( task.getJob().isEnabled(), deserializedTask.getJob().isEnabled() );
    }

    private SchedulableTaskImpl createAndRunTask( final ScheduledJobName name, final DescriptorKey descriptor, final PropertyTree data )
    {
        return createAndRunTask( name, descriptor, data, null );
    }

    private SchedulableTaskImpl createAndRunTask( final ScheduledJobName name, final DescriptorKey descriptor, final PropertyTree data,
                                                  final PrincipalKey user )
    {
        final ScheduledJob job = ScheduledJob.create().
            name( name ).
            descriptor( descriptor ).
            description( "description" ).
            calendar( CronCalendarImpl.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getTimeZone( ZoneId.systemDefault() ) ).
                build() ).
            config( data ).
            user( user ).
            enabled( true ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2021-02-26T10:44:33.170079900Z" ) ).
            modifiedTime( Instant.parse( "2021-03-26T10:44:33.170079900Z" ) ).
            build();

        final SchedulableTaskImpl task = SchedulableTaskImpl.create().
            job( job ).
            build();

        task.run();

        return task;
    }

    private void mockNode()
    {
        final PropertyTree jobData = new PropertyTree();

        final PropertySet calendar = new PropertySet();
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_TYPE, "ONE_TIME" );
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_VALUE, "2021-02-25T10:44:33.170079900Z" );

        jobData.addString( ScheduledJobPropertyNames.DESCRIPTOR, "app:key" );
        jobData.addBoolean( ScheduledJobPropertyNames.ENABLED, true );
        jobData.addSet( ScheduledJobPropertyNames.CALENDAR, calendar );
        jobData.addSet( ScheduledJobPropertyNames.CONFIG, new PropertySet() );
        jobData.setString( ScheduledJobPropertyNames.CREATOR, "user:system:creator" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIER, "user:system:modifier" );
        jobData.setString( ScheduledJobPropertyNames.CREATED_TIME, "2021-02-26T10:44:33.170079900Z" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIED_TIME, "2021-03-26T10:44:33.170079900Z" );

        final Node job = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "test" ).
            parentPath( NodePath.ROOT ).
            data( jobData ).
            build();

        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( job );
    }
}
