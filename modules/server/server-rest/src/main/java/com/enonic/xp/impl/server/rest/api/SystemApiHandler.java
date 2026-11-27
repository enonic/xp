package com.enonic.xp.impl.server.rest.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.impl.server.rest.task.VacuumCommand;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:system} - installation-wide maintenance. {@code prune} removes unused blobs and old versions;
 * {@code /vacuum} is its alias, kept for the name the operation has always had. {@code /reports} lists the diagnostic
 * reports, and {@code /reports/<name>} returns one: a thread dump, a deadlock analysis or a dump of the HTTP server.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:system", "scopes=read:system", "scopes=system", "title=System API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class SystemApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:system";

    private final TaskService taskService;

    private final Map<String, StatusReporter> reporters = new ConcurrentHashMap<>();

    @Activate
    public SystemApiHandler( @Reference final TaskService taskService )
    {
        super( KEY );
        this.taskService = taskService;

        route( HttpMethod.POST, "/prune", "prune", this::prune );
        route( HttpMethod.POST, "/vacuum", "prune", this::prune );
        route( HttpMethod.GET, "/reports", "report", ( request, params ) -> json( new TreeSet<>( reporters.keySet() ) ) );
        route( HttpMethod.GET, "/reports/{name}", "report", this::report );
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, target = "(mount=management)")
    public void addReporter( final StatusReporter reporter )
    {
        reporters.put( reporter.getName(), reporter );
    }

    public void removeReporter( final StatusReporter reporter )
    {
        reporters.remove( reporter.getName(), reporter );
    }

    private WebResponse prune( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String body = request.getBodyAsString();
        final VacuumRequestJson vacuum =
            body == null || body.isBlank() ? new VacuumRequestJson( null, null ) : MAPPER.readValue( body, VacuumRequestJson.class );

        final TaskId taskId = VacuumCommand.create()
            .ageThreshold( vacuum.getAgeThreshold() )
            .tasks( vacuum.getTasks() )
            .taskService( taskService )
            .build()
            .execute();
        return accepted( taskId );
    }

    private WebResponse report( final WebRequest request, final Map<String, String> params )
    {
        final String name = params.get( "name" );
        final StatusReporter reporter = reporters.get( name );
        if ( reporter == null )
        {
            return error( HttpStatus.NOT_FOUND, String.format( "Report [%s] not found", name ) );
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            reporter.report( out );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return WebResponse.create().status( HttpStatus.OK ).contentType( reporter.getMediaType() ).body( out.toByteArray() ).build();
    }
}
