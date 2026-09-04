package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.impl.server.rest.model.CleanUpAuditLogRequestJson;
import com.enonic.xp.impl.server.rest.task.CleanUpAuditLogCommand;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:auditlog} - the audit log. {@code prune} removes records older than a threshold; {@code /cleanup} is
 * its alias.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:auditlog", "title=Audit Log API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class AuditLogApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:auditlog";

    private final TaskService taskService;

    @Activate
    public AuditLogApiHandler( @Reference final TaskService taskService )
    {
        super( KEY );
        this.taskService = taskService;

        route( HttpMethod.POST, "/prune", "prune", this::prune );
        route( HttpMethod.POST, "/cleanup", "prune", this::prune );
    }

    private WebResponse prune( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final CleanUpAuditLogRequestJson cleanup = body( request, CleanUpAuditLogRequestJson.class );
        if ( cleanup.getAgeThreshold() == null || cleanup.getAgeThreshold().isBlank() )
        {
            throw new IllegalArgumentException( "[ageThreshold] is required" );
        }
        final TaskId taskId =
            CleanUpAuditLogCommand.create().taskService( taskService ).ageThreshold( cleanup.getAgeThreshold() ).build().execute();
        return accepted( taskId );
    }
}
