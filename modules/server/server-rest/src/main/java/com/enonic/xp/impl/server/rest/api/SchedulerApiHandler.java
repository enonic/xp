package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.impl.server.rest.model.ScheduledJobJson;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:scheduler} - scheduled jobs: list and get. Jobs are created by applications and the
 * scheduler configuration, not through the management API.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:scheduler", "title=Scheduler API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class SchedulerApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:scheduler";

    private final SchedulerService schedulerService;

    @Activate
    public SchedulerApiHandler( @Reference final SchedulerService schedulerService )
    {
        super( KEY );
        this.schedulerService = schedulerService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.GET, "/{name}", "get", this::get );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( Map.of( "jobs", schedulerService.list().stream().map( ScheduledJobJson::new ).toList() ) );
    }

    private WebResponse get( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final ScheduledJob job = schedulerService.get( ScheduledJobName.from( params.get( "name" ) ) );
        if ( job == null )
        {
            return error( HttpStatus.NOT_FOUND, String.format( "Scheduled job [%s] not found", params.get( "name" ) ) );
        }
        return json( new ScheduledJobJson( job ) );
    }
}
