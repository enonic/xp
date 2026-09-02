package com.enonic.xp.impl.server.rest.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.server.rest.task.SystemTasks;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:index} - the search and storage indices of a repository. Exposes replica counts, the one index setting
 * an operator tunes, instead of raw Elasticsearch settings.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:index", "title=Index API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class IndexApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:index";

    static final String SEARCH = "search";

    static final String STORAGE = "storage";

    private static final String NUMBER_OF_REPLICAS = "index.number_of_replicas";

    private static final String AUTO_EXPAND_REPLICAS = "index.auto_expand_replicas";

    private static final Pattern FIXED = Pattern.compile( "^\\d+$" );

    private static final Pattern RANGE = Pattern.compile( "^\\d+-(\\d+|all)$" );

    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final TaskService taskService;

    @Activate
    public IndexApiHandler( @Reference final IndexService indexService, @Reference final RepositoryService repositoryService,
                            @Reference final TaskService taskService )
    {
        super( KEY );
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.taskService = taskService;

        route( HttpMethod.GET, "/{repo}", "get", this::get );
        route( HttpMethod.PUT, "/{repo}", "update", this::update );
        route( HttpMethod.POST, "/{repo}/reindex", "reindex", this::reindex );
    }

    private WebResponse get( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final Repository repository = repository( params );
        if ( repository == null )
        {
            return notFound( params );
        }
        return json( replicas( repository.getId() ) );
    }

    private WebResponse update( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final Repository repository = repository( params );
        if ( repository == null )
        {
            return notFound( params );
        }

        final ReplicasJson replicas = body( request, ReplicasJson.class );
        if ( replicas.replicas == null )
        {
            throw new IllegalArgumentException( "[replicas] is required" );
        }
        final String search = validate( SEARCH, replicas.replicas.get( SEARCH ) );
        final String storage = validate( STORAGE, replicas.replicas.get( STORAGE ) );

        indexService.updateIndexSettings( UpdateIndexSettingsParams.create()
                                              .repository( repository.getId() )
                                              .indexType( IndexType.SEARCH )
                                              .settings( settings( search ) )
                                              .build() );
        indexService.updateIndexSettings( UpdateIndexSettingsParams.create()
                                              .repository( repository.getId() )
                                              .indexType( IndexType.VERSION )
                                              .settings( settings( storage ) )
                                              .build() );

        return json( replicas( repository.getId() ) );
    }

    private WebResponse reindex( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final Repository repository = repository( params );
        if ( repository == null )
        {
            return notFound( params );
        }

        final String body = request.getBodyAsString();
        final ReindexJson reindex = body == null || body.isBlank() ? new ReindexJson() : MAPPER.readValue( body, ReindexJson.class );

        final List<String> branches = reindex.branches == null || reindex.branches.isEmpty()
            ? repository.getBranches().stream().map( Branch::getValue ).toList()
            : reindex.branches;

        final PropertyTree data = new PropertyTree();
        data.addString( "repository", repository.getId().toString() );
        data.addStrings( "branches", branches );
        data.addBoolean( "initialize", reindex.initialize );

        final TaskId taskId = taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.REINDEX ).data( data ).build() );
        return accepted( taskId );
    }

    private Repository repository( final Map<String, String> params )
    {
        return repositoryService.get( RepositoryId.from( params.get( "repo" ) ) );
    }

    private WebResponse notFound( final Map<String, String> params )
    {
        return error( HttpStatus.NOT_FOUND, String.format( "Repository [%s] not found", params.get( "repo" ) ) );
    }

    private ReplicasJson replicas( final RepositoryId repositoryId )
    {
        final ReplicasJson json = new ReplicasJson();
        json.replicas = new LinkedHashMap<>();
        json.replicas.put( SEARCH, replicas( indexService.getIndexSettings( repositoryId, IndexType.SEARCH ) ) );
        json.replicas.put( STORAGE, replicas( indexService.getIndexSettings( repositoryId, IndexType.VERSION ) ) );
        return json;
    }

    private static String replicas( final Map<String, String> settings )
    {
        if ( settings == null )
        {
            return null;
        }
        final String auto = settings.get( AUTO_EXPAND_REPLICAS );
        return auto != null && !"false".equals( auto ) ? auto : settings.get( NUMBER_OF_REPLICAS );
    }

    private static String validate( final String index, final String value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( String.format( "[replicas.%s] is required", index ) );
        }
        if ( !FIXED.matcher( value ).matches() && !RANGE.matcher( value ).matches() )
        {
            throw new IllegalArgumentException(
                String.format( "[replicas.%s] must be a number (\"2\") or a range (\"0-all\", \"0-1\"), got [%s]", index, value ) );
        }
        return value;
    }

    private static String settings( final String replicas )
        throws JsonProcessingException
    {
        final Map<String, String> index = new LinkedHashMap<>();
        if ( FIXED.matcher( replicas ).matches() )
        {
            index.put( "number_of_replicas", replicas );
            index.put( "auto_expand_replicas", "false" );
        }
        else
        {
            index.put( "auto_expand_replicas", replicas );
        }
        return MAPPER.writeValueAsString( Map.of( "index", index ) );
    }

    public static final class ReplicasJson
    {
        public Map<String, String> replicas;
    }

    public static final class ReindexJson
    {
        public List<String> branches;

        public boolean initialize;
    }
}
