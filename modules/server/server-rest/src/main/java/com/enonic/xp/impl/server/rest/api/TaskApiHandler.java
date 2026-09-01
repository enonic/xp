package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.impl.server.rest.model.TaskInfoJson;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:task} - the tasks the other management APIs hand out ids for.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:task", "title=Task API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class TaskApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:task";

    private final TaskService taskService;

    @Activate
    public TaskApiHandler( @Reference final TaskService taskService )
    {
        super( KEY );
        this.taskService = taskService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.GET, "/{id}", "get", this::get );
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
}
