package com.enonic.xp.impl.task.cluster;

import java.util.Collections;
import java.util.List;

import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequestHandler;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.impl.task.TaskManager;
import com.enonic.xp.task.TaskInfo;

@Component(immediate = true, service = TransportRequestHandler.class)
public final class TaskTransportRequestHandler
    extends TransportRequestHandler<TaskTransportRequest>
{
    private final static Logger LOG = LoggerFactory.getLogger( TaskTransportRequestHandler.class );

    private TransportService transportService;

    private TaskManager taskManager;

    @Activate
    public void activate()
    {
        this.transportService.registerRequestHandler( TaskTransportRequestSenderImpl.ACTION, TaskTransportRequest.class,
                                                      ThreadPool.Names.MANAGEMENT, this );
    }

    @Deactivate
    public void deactivate()
    {
        this.transportService.removeHandler( TaskTransportRequestSenderImpl.ACTION );
    }

    @Override
    public void messageReceived( final TaskTransportRequest request, final TransportChannel channel )
    {
        try
        {
            final List<TaskInfo> taskInfos;
            if ( TaskTransportRequest.Type.BY_ID == request.getType() )
            {
                final TaskInfo taskInfo = taskManager.getTaskInfo( request.getTaskId() );
                taskInfos = taskInfo == null ? Collections.emptyList() : Collections.singletonList( taskInfo );
            }
            else if ( TaskTransportRequest.Type.RUNNING == request.getType() )
            {
                taskInfos = taskManager.getRunningTasks();
            }
            else
            {
                taskInfos = taskManager.getAllTasks();
            }

            final TaskTransportResponse response = new TaskTransportResponse( taskInfos );
            channel.sendResponse( response );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to get and return local tasks", e );
        }
    }

    @Reference
    public void setTransportService( final TransportService transportService )
    {
        this.transportService = transportService;
    }

    @Reference
    public void setTaskManager( final TaskManager taskManager )
    {
        this.taskManager = taskManager;
    }
}
