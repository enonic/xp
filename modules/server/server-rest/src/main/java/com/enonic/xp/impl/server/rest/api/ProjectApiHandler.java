package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.server.rest.ProjectJsonFactory;
import com.enonic.xp.impl.server.rest.task.SystemTasks;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:project} - content projects: list them with their sites, and sync inherited content from parent
 * projects into all or a chosen set of projects.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:project", "title=Project API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class ProjectApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:project";

    private final ProjectJsonFactory projectJsonFactory;

    private final TaskService taskService;

    @Activate
    public ProjectApiHandler( @Reference final ProjectService projectService, @Reference final ContentService contentService,
                              @Reference final TaskService taskService )
    {
        super( KEY );
        this.projectJsonFactory = new ProjectJsonFactory( projectService, contentService );
        this.taskService = taskService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.POST, "/sync", "sync", this::sync );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( Map.of( "projects", projectJsonFactory.list() ) );
    }

    private WebResponse sync( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String body = request.getBodyAsString();
        final SyncJson sync = body == null || body.isBlank() ? new SyncJson() : MAPPER.readValue( body, SyncJson.class );

        final PropertyTree data = new PropertyTree();
        if ( sync.projects != null && !sync.projects.isEmpty() )
        {
            sync.projects.forEach( ProjectName::from ); // validate
            data.addStrings( "projects", sync.projects );
        }

        final TaskId taskId = taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.PROJECT_SYNC ).data( data ).build() );
        return accepted( taskId );
    }

    public static final class SyncJson
    {
        public List<String> projects;
    }
}
