package com.enonic.xp.impl.task.cluster;

import java.util.List;

import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.BaseTransportRequestHandler;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequestHandler;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskManager;

@Component(immediate = true, service = TransportRequestHandler.class)
public final class GetAllTasksRequestHandler
    extends BaseTransportRequestHandler<GetAllTasksRequest>
{
    private final static Logger LOG = LoggerFactory.getLogger( GetAllTasksRequestHandler.class );

    private TransportService transportService;

    private TaskManager taskManager;

    @Activate
    public void activate()
    {
        this.transportService.registerHandler( GetAllTasksRequestSender.ACTION, this );
    }

    @Deactivate
    public void deactivate()
    {
        this.transportService.removeHandler( GetAllTasksRequestSender.ACTION );
    }

    @Override
    public GetAllTasksRequest newInstance()
    {
        return new GetAllTasksRequest();
    }

    @Override
    public void messageReceived( final GetAllTasksRequest request, final TransportChannel channel )
    {
        try
        {
            final List<TaskInfo> allTasks = taskManager.getAllTasks();
            final GetAllTasksResponse response = new GetAllTasksResponse( allTasks );
            channel.sendResponse( response );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to get and return local tasks", e );
        }
    }

    @Override
    public String executor()
    {
        return ThreadPool.Names.MANAGEMENT;
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
