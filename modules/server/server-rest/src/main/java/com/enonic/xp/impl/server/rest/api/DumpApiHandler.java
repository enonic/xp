package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.impl.server.rest.model.SystemDumpListJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.impl.server.rest.task.DumpRunnableTask;
import com.enonic.xp.impl.server.rest.task.LoadRunnableTask;
import com.enonic.xp.impl.server.rest.task.UpgradeRunnableTask;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:dump} - system dumps under {@code $XP_HOME/data/dump}: list, create, load and upgrade.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:dump", "title=Dump API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class DumpApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:dump";

    private final DumpService dumpService;

    private final TaskService taskService;

    @Activate
    public DumpApiHandler( @Reference final DumpService dumpService, @Reference final TaskService taskService )
    {
        super( KEY );
        this.dumpService = dumpService;
        this.taskService = taskService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.POST, "/", "create", this::create );
        route( HttpMethod.POST, "/{name}/load", "load", this::load );
        route( HttpMethod.POST, "/{name}/upgrade", "upgrade", this::upgrade );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( SystemDumpListJson.from( dumpService.list() ) );
    }

    private WebResponse create( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final SystemDumpRequestJson dump = body( request, SystemDumpRequestJson.class );
        if ( dump.getName() == null || dump.getName().isBlank() )
        {
            throw new IllegalArgumentException( "[name] is required" );
        }

        final TaskId taskId = DumpRunnableTask.create()
            .name( dump.getName() )
            .includeVersions( dump.isIncludeVersions() )
            .maxAge( dump.getMaxAge() )
            .maxVersions( dump.getMaxVersions() )
            .repositories( repositories( dump.getRepositories() ) )
            .taskService( taskService )
            .dumpService( dumpService )
            .build()
            .execute();
        return accepted( taskId );
    }

    private WebResponse load( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String name = params.get( "name" );
        final String body = request.getBodyAsString();
        final SystemLoadRequestJson load =
            body == null || body.isBlank() ? new SystemLoadRequestJson( name, false, null ) : MAPPER.readValue( body, SystemLoadRequestJson.class );

        final LoadRunnableTask task = LoadRunnableTask.create()
            .name( name )
            .upgrade( load.isUpgrade() )
            .repositories( repositories( load.getRepositories() ) )
            .taskService( taskService )
            .dumpService( dumpService )
            .build();
        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( task ).name( "load" ).description( "Load " + name ).build() );
        return accepted( taskId );
    }

    private WebResponse upgrade( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String name = params.get( "name" );
        final UpgradeRunnableTask task = UpgradeRunnableTask.create().dumpService( dumpService ).name( name ).build();
        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( task ).description( "Upgrade dump " + name ).build() );
        return accepted( taskId );
    }

    private static RepositoryIds repositories( final List<String> repositories )
    {
        return repositories == null
            ? RepositoryIds.empty()
            : repositories.stream().map( RepositoryId::from ).collect( RepositoryIds.collector() );
    }
}
