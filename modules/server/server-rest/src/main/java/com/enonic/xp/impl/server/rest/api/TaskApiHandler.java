package com.enonic.xp.impl.server.rest.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.core.internal.UuidHelper;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.impl.server.rest.model.TaskInfoJson;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.sse.SseConfig;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

/**
 * {@code server:task} - the tasks the other management APIs hand out ids for. {@code watch} streams task events over SSE:
 * for every task on the cluster, or for one task.
 */
@Component(service = {UniversalApiHandler.class, EventListener.class}, property = {"key=server:task", "title=Task API",
    "mount=management", "allowedPrincipals=role:system.admin"})
public class TaskApiHandler
    extends ManagementApiHandler
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( TaskApiHandler.class );

    static final String KEY = "server:task";

    static final String SSE_GROUP = "server:task:events";

    static final String TASK_ID_ATTRIBUTE = "taskId";

    private static final String EVENT_PREFIX = "task.";

    private static final Set<String> EVENT_TYPES = Set.of( "submitted", "updated", "finished", "failed", "removed" );

    private final TaskService taskService;

    private final SseManager sseManager;

    @Activate
    public TaskApiHandler( @Reference final TaskService taskService, @Reference final SseManager sseManager )
    {
        super( KEY );
        this.taskService = taskService;
        this.sseManager = sseManager;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.GET, "/watch", "watch", ( request, params ) -> watch( null ) );
        route( HttpMethod.GET, "/events", "watch", ( request, params ) -> watch( null ) );
        route( HttpMethod.GET, "/{id}", "get", this::get );
        route( HttpMethod.GET, "/{id}/watch", "watch", ( request, params ) -> watch( params.get( "id" ) ) );
        route( HttpMethod.GET, "/{id}/events", "watch", ( request, params ) -> watch( params.get( "id" ) ) );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final boolean running = "true".equalsIgnoreCase( param( request, "running" ) );
        final List<TaskInfo> tasks = running ? taskService.getRunningTasks() : taskService.getAllTasks();
        return json( Map.of( "tasks", tasks.stream().map( TaskInfoJson::new ).toList() ) );
    }

    private WebResponse get( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final TaskInfo taskInfo = taskService.getTaskInfo( TaskId.from( params.get( "id" ) ) );
        if ( taskInfo == null )
        {
            return error( HttpStatus.NOT_FOUND, String.format( "Task [%s] not found", params.get( "id" ) ) );
        }
        return json( new TaskInfoJson( taskInfo ) );
    }

    private WebResponse watch( final String taskId )
    {
        if ( taskId != null && taskService.getTaskInfo( TaskId.from( taskId ) ) == null )
        {
            return error( HttpStatus.NOT_FOUND, String.format( "Task [%s] not found", taskId ) );
        }
        final GenericValue.ObjectBuilder attributes = GenericValue.newObject();
        if ( taskId != null )
        {
            attributes.put( TASK_ID_ATTRIBUTE, taskId );
        }
        return WebResponse.create().status( HttpStatus.OK ).sse( new SseConfig( attributes.build(), -1, 0 ) ).build();
    }

    @Override
    public void onSseEvent( final SseEvent event )
    {
        if ( event.getType() != SseEventType.OPEN )
        {
            return;
        }
        final UUID clientId = event.getClientId();
        final String taskId = event.getAttributes().optional( TASK_ID_ATTRIBUTE ).map( GenericValue::asString ).orElse( null );
        try
        {
            if ( taskId == null )
            {
                sseManager.addToGroup( SSE_GROUP, clientId );
                sseManager.send( clientId, message( "list", Map.of( "tasks", taskService.getAllTasks().stream().map( TaskInfoJson::new ).toList() ) ) );
            }
            else
            {
                sseManager.addToGroup( group( taskId ), clientId );
                final TaskInfo taskInfo = taskService.getTaskInfo( TaskId.from( taskId ) );
                if ( taskInfo != null )
                {
                    sseManager.send( clientId, message( "task", new TaskInfoJson( taskInfo ) ) );
                }
            }
        }
        catch ( JsonProcessingException e )
        {
            LOG.warn( "Failed to serialize task list", e );
        }
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( !event.getType().startsWith( EVENT_PREFIX ) )
        {
            return;
        }
        final String type = event.getType().substring( EVENT_PREFIX.length() );
        if ( !EVENT_TYPES.contains( type ) )
        {
            return;
        }
        final String taskId = event.getValueAs( String.class, "id" ).orElse( null );
        if ( taskId == null )
        {
            return;
        }
        final boolean all = sseManager.getGroupSize( SSE_GROUP ) > 0;
        final boolean single = sseManager.getGroupSize( group( taskId ) ) > 0;
        if ( !all && !single )
        {
            return;
        }
        try
        {
            final SseMessage message = message( type, task( event ) );
            if ( all )
            {
                sseManager.sendToGroup( SSE_GROUP, message );
            }
            if ( single )
            {
                sseManager.sendToGroup( group( taskId ), message );
            }
        }
        catch ( JsonProcessingException e )
        {
            LOG.warn( "Failed to serialize task event", e );
        }
    }

    /**
     * The task as the event describes it, in the shape of {@link TaskInfoJson}. Events are distributed across the cluster,
     * so the task need not be known to this node's {@link TaskService}.
     */
    static Map<String, Object> task( final Event event )
    {
        final Map<String, Object> json = new LinkedHashMap<>();
        for ( final String key : List.of( "id", "name", "description", "state", "application", "user", "startTime" ) )
        {
            event.getValue( key ).ifPresent( value -> json.put( key, value ) );
        }
        event.getValue( "progress" ).ifPresent( value -> json.put( "progress", value ) );
        return json;
    }

    private static String group( final String taskId )
    {
        return SSE_GROUP + ":" + taskId;
    }

    private static SseMessage message( final String event, final Object data )
        throws JsonProcessingException
    {
        return SseMessage.create().id( UuidHelper.newUUIDv7().toString() ).event( event ).data( MAPPER.writeValueAsString( data ) ).build();
    }
}
