package com.enonic.xp.impl.scheduler.distributed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulableTaskTest
{
    @Mock(stubOnly = true)
    ServiceReference<TaskService> serviceReference;

    @Captor
    ArgumentCaptor<SubmitTaskParams> taskCaptor;

    @Mock
    private TaskService taskService;

    @Mock(stubOnly = true)
    private BundleContext bundleContext;

    private static byte[] serialize( Serializable serializable )
        throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream( baos ))
        {
            oos.writeObject( serializable );
            return baos.toByteArray();
        }
    }

    private static SchedulableTask deserialize( byte[] bytes )
        throws IOException, ClassNotFoundException
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream( bytes ); ObjectInputStream ois = new ObjectInputStream( bais ))
        {
            return (SchedulableTask) ois.readObject();
        }
    }

    @BeforeEach
    public void setUp()
        throws Exception
    {
        final Bundle bundle = OsgiSupportMock.mockBundle();
        when( bundle.getBundleContext() ).thenReturn( bundleContext );
        when( bundleContext.getServiceReferences( TaskService.class, null ) ).thenReturn( List.of( serviceReference ) );
        when( bundleContext.getService( serviceReference ) ).thenReturn( taskService );
    }

    @AfterEach
    void tearDown()
    {
        OsgiSupportMock.reset();
    }

    @Test
    public void taskCalled()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "string", "value" );

        final SchedulableTask task = createAndRunTask( SchedulerName.from( "task" ), DescriptorKey.from( "app:key" ), data );
        assertEquals( "task", task.getName() );

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );

        assertEquals( DescriptorKey.from( "app:key" ), taskCaptor.getValue().getDescriptorKey() );
        assertEquals( data, taskCaptor.getValue().getData() );
    }

    @Test
    public void taskFailed()
    {
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( RuntimeException.class );
        createAndRunTask( SchedulerName.from( "task" ), DescriptorKey.from( "app:key" ), new PropertyTree() );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( OutOfMemoryError.class );
        assertThrows( OutOfMemoryError.class,
                      () -> createAndRunTask( SchedulerName.from( "task" ), DescriptorKey.from( "app:key" ), new PropertyTree() ) );
    }

    @Test
    public void taskSerialized()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "string", "value" );

        final SchedulableTask task = createAndRunTask( SchedulerName.from( "task" ), DescriptorKey.from( "app:key" ), data );

        byte[] serialized = serialize( task );

        final SchedulableTask deserializedTask = deserialize( serialized );

        assertEquals( task.getJob().getName(), deserializedTask.getJob().getName() );
        assertEquals( task.getJob().getPayload(), deserializedTask.getJob().getPayload() );
        assertEquals( task.getJob().getUser(), deserializedTask.getJob().getUser() );
        assertEquals( task.getJob().getAuthor(), deserializedTask.getJob().getAuthor() );
        assertEquals( task.getJob().getDescriptor(), deserializedTask.getJob().getDescriptor() );
        assertEquals( task.getJob().getDescription(), deserializedTask.getJob().getDescription() );
        assertEquals( task.getJob().isEnabled(), deserializedTask.getJob().isEnabled() );
    }

    private SchedulableTask createAndRunTask( final SchedulerName name, final DescriptorKey descriptor, final PropertyTree data )
    {
        final ScheduledJob job = ScheduledJob.create().
            name( name ).
            descriptor( descriptor ).
            description( "description" ).
            calendar( CronCalendar.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getTimeZone( ZoneId.systemDefault() ) ).
                build() ).
            payload( data ).
            user( PrincipalKey.from( "user:system:user" ) ).
            author( PrincipalKey.from( "user:system:author" ) ).
            enabled( true ).
            build();

        final SchedulableTask task = SchedulableTask.create().
            job( job ).
            build();

        task.run();

        return task;
    }
}
