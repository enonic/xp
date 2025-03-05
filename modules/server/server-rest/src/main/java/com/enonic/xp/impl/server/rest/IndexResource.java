package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.impl.server.rest.model.ReindexRequestJson;
import com.enonic.xp.impl.server.rest.model.ReindexResultJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.model.UpdateIndexSettingsRequestJson;
import com.enonic.xp.impl.server.rest.model.UpdateIndexSettingsResultJson;
import com.enonic.xp.impl.server.rest.task.ReindexRunnableTask;
import com.enonic.xp.impl.server.rest.task.listener.ReindexListenerImpl;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.index.UpdateIndexSettingsResult;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static com.google.common.base.Strings.isNullOrEmpty;

@Path("/repo/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class IndexResource
    implements JaxRsComponent
{
    private IndexService indexService;

    private RepositoryService repositoryService;

    private TaskService taskService;

    @POST
    @Path("reindex")
    public ReindexResultJson reindex( final ReindexRequestJson params )
    {
        final ReindexResult result = this.indexService.reindex( ReindexParams.create()
                                                                    .repositoryId( params.getRepository() )
                                                                    .setBranches( params.getBranches() )
                                                                    .initialize( params.isInitialize() )
                                                                    .listener( new ReindexListenerImpl() )
                                                                    .build() );

        return ReindexResultJson.create( result );
    }

    @POST
    @Path("reindexTask")
    public TaskResultJson reindexTask( final ReindexRequestJson params )
    {
        ReindexRunnableTask reindexRunnableTask = ReindexRunnableTask.create()
            .indexService( indexService )
            .taskService( taskService )
            .repository( params.getRepository() )
            .branches( params.getBranches() )
            .initialize( params.isInitialize() )
            .build();
        final TaskId taskId = taskService.submitLocalTask( SubmitLocalTaskParams.create()
                                                               .runnableTask( reindexRunnableTask )
                                                               .name( "reindex-" + params.getRepository() )
                                                               .description(
                                                                   "Reindex " + params.getRepository() + " " + params.getBranches() )
                                                               .build() );

        return new TaskResultJson( taskId );
    }

    @POST
    @Path("updateSettings")
    public UpdateIndexSettingsResultJson updateSettings( final UpdateIndexSettingsRequestJson request )
    {
        final RepositoryIds.Builder repositoryIds = RepositoryIds.create();

        if ( !isNullOrEmpty( request.repositoryId ) )
        {
            repositoryIds.add( RepositoryId.from( request.repositoryId ) );
        }
        else
        {
            repositoryIds.addAll( this.repositoryService.list().getIds() );
        }

        final UpdateIndexSettingsResult result = this.indexService.updateIndexSettings( UpdateIndexSettingsParams.create().
            repositories( repositoryIds.build() ).
            settings( request.settings.toString() ).
            requireClosedIndex( request.requireClosedIndex ).
            build() );

        return UpdateIndexSettingsResultJson.create( result );
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }

}
