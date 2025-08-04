package com.enonic.xp.impl.task.distributed;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.impl.task.script.NamedTask;
import com.enonic.xp.impl.task.script.NamedTaskFactory;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DistributableTaskTest
{
    private static final TaskContext TEST_TASK_CONTEXT = TaskContext.create()
        .setBranch( Branch.from( "master" ) )
        .setRepo( RepositoryId.from( "test" ) )
        .setAuthInfo( AuthenticationInfo.unAuthenticated() )
        .build();

    @Mock(stubOnly = true)
    BundleContext bundleContext;

    @Mock(stubOnly = true)
    ServiceReference<NamedTaskFactory> serviceReference;

    @Mock(stubOnly = true)
    NamedTaskFactory namedTaskFactory;

    @Mock(stubOnly = true)
    ServiceReference<TaskDescriptorService> taskDescriptorServiceReference;

    @Mock(stubOnly = true)
    ServiceReference<PropertyTreeMarshallerService> propertyTreeMarshallerServiceReference;

    @Mock(stubOnly = true)
    TaskDescriptorService taskDescriptorService;

    @Mock(stubOnly = true)
    PropertyTreeMarshallerService propertyTreeMarshallerService;


    Bundle bundle;

    @BeforeEach
    void setUp()
        throws Exception
    {
        bundle = OsgiSupportMock.mockBundle();
        when( bundle.getBundleContext() ).thenReturn( bundleContext );

        when( bundleContext.getServiceReferences( NamedTaskFactory.class, null ) ).thenReturn( List.of( serviceReference ) );
        when( bundleContext.getServiceReferences( TaskDescriptorService.class, null ) ).thenReturn(
            List.of( taskDescriptorServiceReference ) );
        when( bundleContext.getServiceReferences( PropertyTreeMarshallerService.class, null ) ).thenReturn(
            List.of( propertyTreeMarshallerServiceReference ) );

        when( bundleContext.getService( serviceReference ) ).thenReturn( namedTaskFactory );
        when( bundleContext.getService( taskDescriptorServiceReference ) ).thenReturn( taskDescriptorService );
        when( bundleContext.getService( propertyTreeMarshallerServiceReference ) ).thenReturn( propertyTreeMarshallerService );
    }

    @AfterEach
    void tearDown()
    {
        OsgiSupportMock.reset();
    }

    @Test
    void fields()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "app:a" );
        final TaskDescriptor descriptor =
            TaskDescriptor.create().key( descriptorKey ).description( "task description" ).config( Form.empty() ).build();

        final PropertyTree config = new PropertyTree();

        when( taskDescriptorService.getTask( descriptorKey ) ).thenReturn( descriptor );
        when(
            propertyTreeMarshallerService.marshal( eq( config.toMap() ), eq( descriptor.getConfig() ), Mockito.anyBoolean() ) ).thenReturn(
            config );

        final DistributableTask describedTask = new DistributableTask( descriptorKey, null, config, TEST_TASK_CONTEXT );

        assertAll( () -> assertEquals( "app:a", describedTask.getName() ), () -> assertNotNull( describedTask.getTaskId() ),
                   () -> assertNotNull( describedTask.getTaskContext() ),
                   () -> assertEquals( "task description", describedTask.getDescription() ),
                   () -> assertEquals( descriptorKey.getApplicationKey(), describedTask.getApplicationKey() ) );
    }

    @Test
    void fields_with_name()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "app:a" );
        final TaskDescriptor descriptor =
            TaskDescriptor.create().key( descriptorKey ).description( "task description" ).config( Form.empty() ).build();

        final PropertyTree config = new PropertyTree();

        when( taskDescriptorService.getTask( descriptorKey ) ).thenReturn( descriptor );
        when(
            propertyTreeMarshallerService.marshal( eq( config.toMap() ), eq( descriptor.getConfig() ), Mockito.anyBoolean() ) ).thenReturn(
            config );

        final DistributableTask describedTask = new DistributableTask( descriptorKey, "name", config, TEST_TASK_CONTEXT );

        assertAll( () -> assertEquals( "name", describedTask.getName() ), () -> assertNotNull( describedTask.getTaskId() ),
                   () -> assertNotNull( describedTask.getTaskContext() ),
                   () -> assertEquals( "task description", describedTask.getDescription() ),
                   () -> assertEquals( descriptorKey.getApplicationKey(), describedTask.getApplicationKey() ) );
    }

    @Test
    void run()
    {
        final NamedTask namedTask = mock( NamedTask.class );

        final DescriptorKey descriptorKey = DescriptorKey.from( "app:a" );
        final TaskDescriptor descriptor = TaskDescriptor.create().key( descriptorKey ).description( "task description" ).build();

        final PropertyTree config = new PropertyTree();

        when( namedTaskFactory.create( descriptor, config ) ).thenReturn( namedTask );

        when( taskDescriptorService.getTask( descriptorKey ) ).thenReturn( descriptor );
        when(
            propertyTreeMarshallerService.marshal( eq( config.toMap() ), eq( descriptor.getConfig() ), Mockito.anyBoolean() ) ).thenReturn(
            config );

        final DistributableTask describedTask = new DistributableTask( descriptorKey, null, config, TEST_TASK_CONTEXT );

        describedTask.run( mock( ProgressReporter.class ) );
        verify( namedTask ).run( any( TaskId.class ), any( ProgressReporter.class ) );
    }

    @Test
    void run_with_name()
    {
        final NamedTask namedTask = mock( NamedTask.class );

        final DescriptorKey descriptorKey = DescriptorKey.from( "app:a" );
        final TaskDescriptor descriptor = TaskDescriptor.create().key( descriptorKey ).description( "task description" ).build();

        final PropertyTree config = new PropertyTree();

        when( namedTaskFactory.create( descriptor, config ) ).thenReturn( namedTask );

        when( taskDescriptorService.getTask( descriptorKey ) ).thenReturn( descriptor );
        when(
            propertyTreeMarshallerService.marshal( eq( config.toMap() ), eq( descriptor.getConfig() ), Mockito.anyBoolean() ) ).thenReturn(
            config );

        final DistributableTask describedTask = new DistributableTask( descriptorKey, "name", config, TEST_TASK_CONTEXT );

        describedTask.run( mock( ProgressReporter.class ) );
        verify( namedTask ).run( any( TaskId.class ), any( ProgressReporter.class ) );
    }
}
