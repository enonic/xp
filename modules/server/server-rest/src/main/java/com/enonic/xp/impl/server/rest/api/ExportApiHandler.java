package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.RepoPath;
import com.enonic.xp.impl.server.rest.task.ExportRunnableTask;
import com.enonic.xp.impl.server.rest.task.ImportRunnableTask;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:export} - node exports under {@code $XP_HOME/data/export}: list them, create one from a repository
 * path, load one back into a repository path.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:export", "title=Export API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class ExportApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:export";

    private final ExportService exportService;

    private final TaskService taskService;

    @Activate
    public ExportApiHandler( @Reference final ExportService exportService, @Reference final TaskService taskService )
    {
        super( KEY );
        this.exportService = exportService;
        this.taskService = taskService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.POST, "/", "create", this::create );
        route( HttpMethod.POST, "/{name}/load", "load", this::load );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final List<Map<String, String>> exports = exportService.list().stream().map( ExportInfo::name ).map( name -> Map.of( "name", name ) ).toList();
        return json( Map.of( "exports", exports ) );
    }

    private WebResponse create( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final ExportNodesRequestJson export = body( request, ExportNodesRequestJson.class );
        if ( export.getExportName() == null || export.getSourceRepoPath() == null )
        {
            throw new IllegalArgumentException( "[exportName] and [sourceRepoPath] are required" );
        }

        final ExportRunnableTask task = ExportRunnableTask.create()
            .repositoryId( export.getSourceRepoPath().getRepositoryId() )
            .branch( export.getSourceRepoPath().getBranch() )
            .nodePath( export.getSourceRepoPath().getNodePath() )
            .exportName( export.getExportName() )
            .batchSize( export.getBatchSize() )
            .exportService( exportService )
            .build();

        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( task ).description( "Export " + export.getExportName() ).build() );
        return accepted( taskId );
    }

    private WebResponse load( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String name = params.get( "name" );
        final LoadJson load = body( request, LoadJson.class );
        if ( load.targetRepoPath == null )
        {
            throw new IllegalArgumentException( "[targetRepoPath] is required" );
        }
        final RepoPath target = RepoPath.from( load.targetRepoPath );

        final ImportRunnableTask task = ImportRunnableTask.create()
            .repositoryId( target.getRepositoryId() )
            .branch( target.getBranch() )
            .nodePath( target.getNodePath() )
            .exportName( name )
            .importWithIds( load.importWithIds )
            .importWithPermissions( load.importWithPermissions )
            .xslSource( load.xslSource )
            .xslParams( load.xslParams )
            .exportService( exportService )
            .build();

        final TaskId taskId =
            taskService.submitLocalTask( SubmitLocalTaskParams.create().runnableTask( task ).description( "Import " + name ).build() );
        return accepted( taskId );
    }

    /**
     * Body of {@code POST /{name}/load}; the export is named by the path.
     */
    public static final class LoadJson
    {
        public String targetRepoPath;

        public boolean importWithIds = true;

        public boolean importWithPermissions = true;

        public String xslSource;

        public Map<String, Object> xslParams;
    }
}
