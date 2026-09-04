package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.impl.server.rest.task.VacuumCommand;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:system} - installation-wide maintenance. {@code prune} removes unused blobs and old versions;
 * {@code /vacuum} is its alias, kept for the name the operation has always had.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:system", "title=System API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class SystemApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:system";

    private final TaskService taskService;

    @Activate
    public SystemApiHandler( @Reference final TaskService taskService )
    {
        super( KEY );
        this.taskService = taskService;

        route( HttpMethod.POST, "/prune", "prune", this::prune );
        route( HttpMethod.POST, "/vacuum", "prune", this::prune );
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
}
