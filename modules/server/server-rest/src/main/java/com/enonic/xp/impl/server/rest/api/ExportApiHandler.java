package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.RepoPath;
import com.enonic.xp.impl.server.rest.task.SystemTasks;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.SubmitTaskParams;
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

        final PropertyTree data = new PropertyTree();
        data.addString( "repository", export.getSourceRepoPath().getRepositoryId().toString() );
        data.addString( "branch", export.getSourceRepoPath().getBranch().getValue() );
        data.addString( "nodePath", export.getSourceRepoPath().getNodePath().toString() );
        data.addString( "exportName", export.getExportName() );
        if ( export.getBatchSize() != null )
        {
            data.addLong( "batchSize", export.getBatchSize().longValue() );
        }
        return accepted( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.EXPORT ).data( data ).build() ) );
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

        final PropertyTree data = new PropertyTree();
        data.addString( "exportName", name );
        data.addString( "repository", target.getRepositoryId().toString() );
        data.addString( "branch", target.getBranch().getValue() );
        data.addString( "nodePath", target.getNodePath().toString() );
        data.addBoolean( "importWithIds", load.importWithIds );
        data.addBoolean( "importWithPermissions", load.importWithPermissions );
        if ( load.xslSource != null )
        {
            data.addString( "xslSource", load.xslSource );
        }
        if ( load.xslParams != null && !load.xslParams.isEmpty() )
        {
            final PropertySet xslParams = data.addSet( "xslParams" );
            load.xslParams.forEach( ( key, value ) -> xslParams.addString( key, String.valueOf( value ) ) );
        }
        return accepted( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.IMPORT ).data( data ).build() ) );
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
