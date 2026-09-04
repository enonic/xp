package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.impl.server.rest.model.SystemDumpListJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.impl.server.rest.task.SystemTasks;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.SubmitTaskParams;
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

        final PropertyTree data = new PropertyTree();
        data.addString( "name", dump.getName() );
        data.addBoolean( "includeVersions", dump.isIncludeVersions() );
        if ( dump.getMaxAge() != null )
        {
            data.addLong( "maxAge", dump.getMaxAge().longValue() );
        }
        if ( dump.getMaxVersions() != null )
        {
            data.addLong( "maxVersions", dump.getMaxVersions().longValue() );
        }
        if ( dump.getRepositories() != null )
        {
            data.addStrings( "repositories", dump.getRepositories() );
        }
        return accepted( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.DUMP ).data( data ).build() ) );
    }

    private WebResponse load( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String name = params.get( "name" );
        final String body = request.getBodyAsString();
        final SystemLoadRequestJson load =
            body == null || body.isBlank() ? new SystemLoadRequestJson( name, false, null ) : MAPPER.readValue( body, SystemLoadRequestJson.class );

        final PropertyTree data = new PropertyTree();
        data.addString( "name", name );
        data.addBoolean( "upgrade", load.isUpgrade() );
        if ( load.getRepositories() != null )
        {
            data.addStrings( "repositories", load.getRepositories() );
        }
        return accepted( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.LOAD ).data( data ).build() ) );
    }

    private WebResponse upgrade( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "name", params.get( "name" ) );
        return accepted( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.UPGRADE ).data( data ).build() ) );
    }
}
