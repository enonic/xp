package com.enonic.xp.impl.task;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportResponse;
import org.elasticsearch.transport.TransportResponseHandler;
import org.elasticsearch.transport.TransportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.impl.task.cluster.TaskTransportRequest;
import com.enonic.xp.impl.task.cluster.TaskTransportRequestHandler;
import com.enonic.xp.impl.task.cluster.TaskTransportRequestSenderImpl;
import com.enonic.xp.impl.task.cluster.TaskTransportResponse;
import com.enonic.xp.impl.task.cluster.TaskTransportResponseHandler;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

public class TaskServiceImplTest
{
    private final static Logger LOGGER = LoggerFactory.getLogger( TaskServiceImplTest.class );

    private TaskServiceImpl taskService;

    private volatile List<TaskInfo> allTasks;

    private volatile Object[] sendRequestArguments;

    private TaskTransportRequest transportRequest;

    private TaskTransportResponseHandler transportResponseHandler;

    private TaskTransportRequestHandler taskTransportRequestHandler1;

    private TaskTransportRequestHandler taskTransportRequestHandler2;

    @Before
    public void setUp()
    {
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

        final TaskManager taskManager1 = Mockito.mock( TaskManager.class );
        Mockito.when( taskManager1.getAllTasks() ).thenReturn( Arrays.asList( taskInfo1, taskInfo2 ) );
        taskTransportRequestHandler1 = new TaskTransportRequestHandler();
        taskTransportRequestHandler1.setTaskManager( taskManager1 );

        final TaskManager taskManager2 = Mockito.mock( TaskManager.class );
        Mockito.when( taskManager2.getAllTasks() ).thenReturn( Arrays.asList( taskInfo3 ) );
        taskTransportRequestHandler2 = new TaskTransportRequestHandler();
        taskTransportRequestHandler2.setTaskManager( taskManager2 );

        final TaskTransportRequestSenderImpl transportRequestSender = createTransportRequestSender();
        taskService = new TaskServiceImpl();
        taskService.setTaskManager( taskManager1 );
        taskService.setTaskTransportRequestSender( transportRequestSender );


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
            doAnswer( invocation -> {
                sendRequestArguments = invocation.getArguments();
                transportRequest = (TaskTransportRequest) sendRequestArguments[2];
                transportResponseHandler = (TaskTransportResponseHandler) sendRequestArguments[3];
                LOGGER.info( "Transport service send a " + transportRequest.getType().toString() + " task request" );
                return null;
            } ).
            when( transportService ).
            sendRequest( Mockito.eq( node2 ), Mockito.eq( TaskTransportRequestSenderImpl.ACTION ), Mockito.any( TransportRequest.class ),
                         Mockito.any( TransportResponseHandler.class ) );

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
        final Thread senderThread = new Thread()
        {
            @Override
            public void run()
            {
                allTasks = taskService.getAllTasks();
                LOGGER.info( "Task service receives back " + allTasks.size() + " task infos" );
            }
        };
        senderThread.start();
        Thread.sleep( 1000 );

        //Checks request sent by TaskService
        Assert.assertEquals( TaskTransportRequest.Type.ALL, transportRequest.getType() );
        Assert.assertNull( transportRequest.getTaskId() );

        //Mocks the request reception by node1 and node2
        mockReception( taskTransportRequestHandler1, "node1" );
        mockReception( taskTransportRequestHandler2, "node2" );

        senderThread.join( 2000 );
        Assert.assertEquals( 3, allTasks.size() );
        Assert.assertEquals( "task1", allTasks.get( 0 ).getId().toString() );
        Assert.assertEquals( "Task1 on node1", allTasks.get( 0 ).getDescription() );
        Assert.assertEquals( TaskState.WAITING, allTasks.get( 0 ).getState() );
    }

    private void mockReception( final TaskTransportRequestHandler transportRequestHandler, final String nodeId )
        throws IOException
    {
        final TransportChannel transportChannel = Mockito.mock( TransportChannel.class );
        Mockito.
            doAnswer( invocation -> {
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
