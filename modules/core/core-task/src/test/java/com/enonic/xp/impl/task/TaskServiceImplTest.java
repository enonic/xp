package com.enonic.xp.impl.task;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportException;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportRequestOptions;
import org.elasticsearch.transport.TransportResponse;
import org.elasticsearch.transport.TransportResponseHandler;
import org.elasticsearch.transport.TransportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.impl.task.cluster.TaskTransportRequest;
import com.enonic.xp.impl.task.cluster.TaskTransportRequestHandler;
import com.enonic.xp.impl.task.cluster.TaskTransportRequestSenderImpl;
import com.enonic.xp.impl.task.cluster.TaskTransportResponse;
import com.enonic.xp.impl.task.cluster.TaskTransportResponseHandler;
import com.enonic.xp.impl.task.script.NamedTaskScriptFactory;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskKey;
import com.enonic.xp.task.TaskNotFoundException;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.eq;

public class TaskServiceImplTest
{
    private final static Logger LOGGER = LoggerFactory.getLogger( TaskServiceImplTest.class );

    private TaskServiceImpl taskService;

    private volatile List<TaskInfo> allTasks;

    private volatile TransportException transportException;

    private volatile Object[] sendRequestArguments;

    private TaskTransportRequest transportRequest;

    private TaskTransportResponseHandler transportResponseHandler;

    private TaskTransportRequestHandler taskTransportRequestHandler1;

    private TaskTransportRequestHandler taskTransportRequestHandler2;

    private TaskDescriptorService taskDescriptorService;

    private NamedTaskScriptFactory namedTaskScriptFactory;

    private TaskManager taskManager;

    @Before
    public void setUp()
    {
        allTasks = null;
        transportException = null;

        TaskInfo taskInfo1 = TaskInfo.create().
            id( TaskId.from( "task1" ) ).
            description( "Task1 on node1" ).
            progress( TaskProgress.EMPTY ).
            state( TaskState.WAITING ).
            build();
        TaskInfo taskInfo2 = TaskInfo.create().
            id( TaskId.from( "task2" ) ).
            description( "Task2 on node1" ).
            progress( TaskProgress.EMPTY ).
            state( TaskState.FINISHED ).
            build();
        TaskInfo taskInfo3 = TaskInfo.create().
            id( TaskId.from( "task3" ) ).
            description( "Task3 on node2" ).
            progress( TaskProgress.EMPTY ).
            state( TaskState.RUNNING ).
            build();

        taskManager = Mockito.mock( TaskManager.class );
        Mockito.when( taskManager.getAllTasks() ).thenReturn( Arrays.asList( taskInfo1, taskInfo2 ) );
        Mockito.when( taskManager.getTaskInfo( Mockito.any( TaskId.class ) ) ).thenReturn( taskInfo2 );
        taskTransportRequestHandler1 = new TaskTransportRequestHandler();
        taskTransportRequestHandler1.setTaskManager( taskManager );

        final TaskManager taskManager2 = Mockito.mock( TaskManager.class );
        Mockito.when( taskManager2.getAllTasks() ).thenReturn( Arrays.asList( taskInfo3 ) );
        Mockito.when( taskManager2.getRunningTasks() ).thenReturn( Arrays.asList( taskInfo3 ) );
        taskTransportRequestHandler2 = new TaskTransportRequestHandler();
        taskTransportRequestHandler2.setTaskManager( taskManager2 );

        taskDescriptorService = Mockito.mock( TaskDescriptorService.class );
        namedTaskScriptFactory = Mockito.mock( NamedTaskScriptFactory.class );

        final TaskTransportRequestSenderImpl transportRequestSender = createTransportRequestSender();
        taskService = new TaskServiceImpl();
        taskService.setTaskManager( taskManager );
        taskService.setTaskTransportRequestSender( transportRequestSender );
        taskService.setTaskDescriptorService( taskDescriptorService );
        taskService.setNamedTaskScriptFactory( namedTaskScriptFactory );
    }


    private TaskTransportRequestSenderImpl createTransportRequestSender()
    {
        //Creates Cluster Service
        final ClusterState clusterState = Mockito.mock( ClusterState.class );
        final DiscoveryNode node1 = new DiscoveryNode( "node1", null, Version.CURRENT );
        final DiscoveryNode node2 = new DiscoveryNode( "node2", null, Version.CURRENT );
        final DiscoveryNodes discoveryNodes = DiscoveryNodes.builder().put( node1 ).put( node2 ).localNodeId( "node1" ).build();
        Mockito.when( clusterState.nodes() ).thenReturn( discoveryNodes );
        final ClusterService clusterService = Mockito.mock( ClusterService.class );
        Mockito.when( clusterService.state() ).thenReturn( clusterState );

        //Creates Transport Service
        final TransportService transportService = Mockito.mock( TransportService.class );
        Mockito.
            doAnswer( invocation ->
                      {
                          sendRequestArguments = invocation.getArguments();
                          transportRequest = (TaskTransportRequest) sendRequestArguments[2];
                          transportResponseHandler = (TaskTransportResponseHandler) sendRequestArguments[4];
                          LOGGER.info( "Transport service send a " + transportRequest.getType().toString() + " task request" );
                          return null;
                      } ).
            when( transportService ).
            sendRequest( Mockito.eq( node2 ), Mockito.eq( TaskTransportRequestSenderImpl.ACTION ), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportRequestOptions.class ), Mockito.any( TransportResponseHandler.class ) );

        //Creates Transport Request Sender
        final TaskTransportRequestSenderImpl transportRequestSender = new TaskTransportRequestSenderImpl();
        transportRequestSender.setClusterService( clusterService );
        transportRequestSender.setTransportService( transportService );
        return transportRequestSender;
    }

    @Test
    public void getAllTasks()
        throws InterruptedException, IOException
    {
        //Calls TaskService method
        final Thread senderThread = callServiceMethod( () -> taskService.getAllTasks() );

        //Checks request sent by TaskService
        Assert.assertEquals( TaskTransportRequest.Type.ALL, transportRequest.getType() );
        Assert.assertNull( transportRequest.getTaskId() );

        //Mocks the request reception by node1 and node2
        mockReception( taskTransportRequestHandler1, "node1" );
        mockReception( taskTransportRequestHandler2, "node2" );

        //Checks that the service received back the 3 tasks
        senderThread.join( 2000 );
        Assert.assertNull( transportException );
        Assert.assertEquals( 3, allTasks.size() );
        Assert.assertEquals( "task1", allTasks.get( 0 ).getId().toString() );
        Assert.assertEquals( "Task1 on node1", allTasks.get( 0 ).getDescription() );
        Assert.assertEquals( TaskState.WAITING, allTasks.get( 0 ).getState() );
    }

    @Test
    public void getAllTasks_with_exception()
        throws InterruptedException, IOException
    {
        //Calls TaskService method
        final Thread senderThread = callServiceMethod( () -> taskService.getAllTasks() );

        //Mocks a transport exception
        transportResponseHandler.handleException( new TransportException( "ATransportException" ) );

        //Checks that the service received back the 3 tasks
        senderThread.join( 2000 );
        Assert.assertNotNull( transportException );
    }

    @Test
    public void getRunningTasks()
        throws InterruptedException, IOException
    {
        //Calls TaskService method
        final Thread senderThread = callServiceMethod( () -> taskService.getRunningTasks() );

        //Checks request sent by TaskService
        Assert.assertEquals( TaskTransportRequest.Type.RUNNING, transportRequest.getType() );
        Assert.assertNull( transportRequest.getTaskId() );

        //Mocks the request reception by node1 and node2
        mockReception( taskTransportRequestHandler1, "node1" );
        mockReception( taskTransportRequestHandler2, "node2" );

        //Checks that the service received back the 3 tasks
        senderThread.join( 2000 );
        Assert.assertNull( transportException );
        Assert.assertEquals( 1, allTasks.size() );
        Assert.assertEquals( "task3", allTasks.get( 0 ).getId().toString() );
        Assert.assertEquals( "Task3 on node2", allTasks.get( 0 ).getDescription() );
        Assert.assertEquals( TaskState.RUNNING, allTasks.get( 0 ).getState() );
    }

    @Test
    public void getTaskInfo()
        throws InterruptedException, IOException
    {
        //Calls TaskService method
        final Thread senderThread = callServiceMethod( () -> Arrays.asList( taskService.getTaskInfo( TaskId.from( "task2" ) ) ) );

        //Checks request sent by TaskService
        Assert.assertEquals( TaskTransportRequest.Type.BY_ID, transportRequest.getType() );
        Assert.assertEquals( "task2", transportRequest.getTaskId().toString() );

        //Mocks the request reception by node1 and node2
        mockReception( taskTransportRequestHandler1, "node1" );
        mockReception( taskTransportRequestHandler2, "node2" );

        //Checks that the service received back the 3 tasks
        senderThread.join( 2000 );
        Assert.assertNull( transportException );
        Assert.assertEquals( 1, allTasks.size() );
        Assert.assertEquals( "task2", allTasks.get( 0 ).getId().toString() );
        Assert.assertEquals( "Task2 on node1", allTasks.get( 0 ).getDescription() );
        Assert.assertEquals( TaskState.FINISHED, allTasks.get( 0 ).getState() );
    }

    @Test
    public void submitTask()
    {
        // set up descriptor
        final ApplicationKey app = ApplicationKey.from( "myapplication" );
        final DescriptorKey descKey = DescriptorKey.from( app, "task1" );
        final TaskDescriptor descriptor = TaskDescriptor.create().description( "My task" ).key( descKey ).build();
        final Descriptors<TaskDescriptor> descriptors = Descriptors.from( descriptor );
        Mockito.when( taskDescriptorService.getTasks( app ) ).thenReturn( descriptors );

        // set up script
        final RunnableTask runnableTask = ( id, progressReporter ) ->
        {
        };
        Mockito.when( namedTaskScriptFactory.create( Mockito.eq( descriptor ) ) ).thenReturn( runnableTask );
        Mockito.when( taskManager.submitTask( eq( runnableTask ), eq( "My task" ) ) ).thenReturn( TaskId.from( "123" ) );

        // submit task by name
        final TaskId taskId = taskService.submitTask( TaskKey.from( "myapplication:task1" ) );

        // verify
        assertEquals( "123", taskId.toString() );
    }

    @Test(expected = TaskNotFoundException.class)
    public void submitTaskMissing()
    {
        // set up descriptor
        final ApplicationKey app = ApplicationKey.from( "myapplication" );
        Mockito.when( taskDescriptorService.getTasks( app ) ).thenReturn( Descriptors.empty() );

        // submit task by name
        taskService.submitTask( TaskKey.from( "myapplication:task1" ) );
    }

    @Test(expected = TaskNotFoundException.class)
    public void submitTaskNoRunExported()
    {
        // set up descriptor
        final ApplicationKey app = ApplicationKey.from( "myapplication" );
        final DescriptorKey descKey = DescriptorKey.from( app, "task1" );
        final TaskDescriptor descriptor = TaskDescriptor.create().description( "My task" ).key( descKey ).build();
        final Descriptors<TaskDescriptor> descriptors = Descriptors.from( descriptor );
        Mockito.when( taskDescriptorService.getTasks( app ) ).thenReturn( descriptors );

        // set up script
        Mockito.when( namedTaskScriptFactory.create( Mockito.eq( descriptor ) ) ).thenReturn( null );

        // submit task by name
        taskService.submitTask( TaskKey.from( "myapplication:task1" ) );
    }

    private Thread callServiceMethod( Supplier<List<TaskInfo>> serviceMethod )
        throws InterruptedException
    {
        final Thread senderThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    allTasks = serviceMethod.get();
                    LOGGER.info( "Task service receives back " + allTasks.size() + " task infos" );
                }
                catch ( TransportException e )
                {
                    transportException = e;
                    LOGGER.info( "Task service throws a transport exception: " + e.toString() );
                }
            }
        };
        senderThread.start();
        Thread.sleep( 1000 );
        return senderThread;
    }

    private void mockReception( final TaskTransportRequestHandler transportRequestHandler, final String nodeId )
        throws IOException
    {
        final TransportChannel transportChannel = Mockito.mock( TransportChannel.class );
        Mockito.
            doAnswer( invocation ->
                      {
                          final TaskTransportResponse taskTransportResponse = (TaskTransportResponse) invocation.getArguments()[0];
                          LOGGER.info( "Transport request handler of " + nodeId + " receives a request and sends back a response with " +
                                           taskTransportResponse.getTaskInfos().size() + " task infos" );
                          transportResponseHandler.handleResponse( taskTransportResponse );
                          return null;
                      } ).
            when( transportChannel ).
            sendResponse( Mockito.any( TransportResponse.class ) );

        transportRequestHandler.messageReceived( transportRequest, transportChannel );
    }
}
