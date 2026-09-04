package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.impl.server.rest.task.ProjectsSyncTask;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:content} - content operations across projects. {@code sync} runs as a local task on the node that
 * received the request: it needs the content applications installed there.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:content", "title=Content API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class ContentApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:content";

    private final ProjectService projectService;

    private final SyncContentService syncContentService;

    private final TaskService taskService;

    @Activate
    public ContentApiHandler( @Reference final ProjectService projectService, @Reference final SyncContentService syncContentService,
                              @Reference final TaskService taskService )
    {
        super( KEY );
        this.projectService = projectService;
        this.syncContentService = syncContentService;
        this.taskService = taskService;

        route( HttpMethod.POST, "/sync", "sync", this::sync );
    }

    private WebResponse sync( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String body = request.getBodyAsString();
        final SyncJson sync = body == null || body.isBlank() ? new SyncJson( null ) : MAPPER.readValue( body, SyncJson.class );
        final List<ProjectName> projects = sync.projects() == null ? List.of() : sync.projects().stream().map( ProjectName::from ).toList();

        final ProjectsSyncTask task =
            ProjectsSyncTask.create().projectService( projectService ).syncContentService( syncContentService ).projects( projects ).build();

        final TaskId taskId = taskService.submitLocalTask( SubmitLocalTaskParams.create()
                                                               .runnableTask( task )
                                                               .name( "sync-all-projects" )
                                                               .description( projects.isEmpty() ? "Sync all projects" : "Sync projects " + projects )
                                                               .build() );
        return accepted( taskId );
    }

    public record SyncJson(List<String> projects)
    {
    }
}
