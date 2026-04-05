package com.enonic.xp.impl.server.rest;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.impl.server.rest.model.ApplicationActionResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.impl.server.rest.model.ApplicationUninstalledJson;
import com.enonic.xp.impl.server.rest.model.ListApplicationJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.multipart.MultipartForm;

@Path("/app")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public class ApplicationResource
    implements JaxRsComponent, EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationResource.class );

    private static final String EVENT_TYPE = "application.cluster";

    private static final String EVENT_TYPE_KEY = "eventType";

    private static final String INSTALLED = "installed";

    private static final String STATE = "state";

    private static final String UNINSTALLED = "uninstalled";

    private static final String APPLICATION_KEY_PARAM = "key";

    private final ApplicationResourceService applicationResourceService;

    private volatile SseContextHolder contextHolder;

    @Activate
    public ApplicationResource( final @Reference ApplicationResourceService applicationResourceService )
    {
        this.applicationResourceService = applicationResourceService;
    }

    @Deactivate
    public void deactivate()
    {
        final SseContextHolder ctx = contextHolder;

        if ( ctx != null )
        {
            ctx.broadcaster.close();
        }
    }

    @Context
    public void setSse( final Sse sse )
    {
        final SseContextHolder ctx = contextHolder;
        if ( ctx == null )
        {
            this.contextHolder = new SseContextHolder( sse );
        }
    }

    @GET
    @Path("events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe( final @Context SseEventSink sseEventSink )
    {
        final SseContextHolder ctx = contextHolder;

        if ( ctx == null )
        {
            throw new IllegalStateException( "No SSE context" );
        }

        final OutboundSseEvent sseEvent = ctx.sse.newEventBuilder()
            .name( "list" )
            .id( UUID.randomUUID().toString() )
            .mediaType( MediaType.APPLICATION_JSON_TYPE )
            .data( ListApplicationJson.class, new ListApplicationJson(
                applicationResourceService.getInstalledApplications().stream().map( ApplicationJson::new ).toList() ) )
            .build();

        sseEventSink.send( sseEvent );

        ctx.broadcaster.register( sseEventSink );
    }

    @POST
    @Path("install")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ApplicationInstallResultJson install( final MultipartForm form )
    {
        try
        {
            return ApplicationInstallResultJson.success( applicationResourceService.install( form ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to install application";
            LOG.error( failure, e );
            return ApplicationInstallResultJson.failure( failure );
        }
    }

    @POST
    @Path("installUrl")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationInstallResultJson installUrl( final ApplicationInstallParams params )
    {
        try
        {
            return ApplicationInstallResultJson.success( applicationResourceService.installUrl( params ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to upload application";
            LOG.error( failure, e );
            return ApplicationInstallResultJson.failure( failure );
        }
    }

    @POST
    @Path("uninstall")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationActionResultJson uninstall( final ApplicationParams params )
    {
        return applicationResourceService.uninstall( params );
    }

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationActionResultJson start( final ApplicationParams params )
    {
        return applicationResourceService.start( params );
    }

    @POST
    @Path("stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationActionResultJson stop( final ApplicationParams params )
    {
        return applicationResourceService.stop( params );
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( EVENT_TYPE.equals( event.getType() ) && event.isLocalOrigin() )
        {
            event.getValueAs( String.class, EVENT_TYPE_KEY ).ifPresent( eventSubType -> {
                switch ( eventSubType )
                {
                    case INSTALLED:
                    case STATE:
                    case UNINSTALLED:
                        handleEvent( event, eventSubType );
                        break;
                    default:
                        LOG.debug( "Ignoring {} {}", EVENT_TYPE, eventSubType );
                        break;
                }
            } );
        }
    }

    private void handleEvent( final Event event, final String eventSubType )
    {
        final SseContextHolder ctx = contextHolder;

        if ( ctx == null )
        {
            LOG.debug( "Skipping until subscribed to SSE events. {} {}.", EVENT_TYPE, eventSubType );
            return;
        }

        if ( event.getValue( APPLICATION_KEY_PARAM ).isPresent() )
        {
            final OutboundSseEvent.Builder eventBuilder = ctx.sse.newEventBuilder()
                .name( eventSubType )
                .id( UUID.randomUUID().toString() )
                .mediaType( MediaType.APPLICATION_JSON_TYPE );

            if ( UNINSTALLED.equals( eventSubType ) )
            {
                eventBuilder.data( ApplicationUninstalledJson.class,
                                   new ApplicationUninstalledJson( event.getValue( APPLICATION_KEY_PARAM ).get().toString() ) );
            }
            else
            {
                final ApplicationKey applicationKey = ApplicationKey.from( event.getValue( APPLICATION_KEY_PARAM ).get().toString() );

                final ApplicationInfoJson applicationInfo = applicationResourceService.getInstalledApplication( applicationKey );

                if ( applicationInfo == null )
                {
                    LOG.warn( "Application {} not found", applicationKey );
                    return;
                }

                eventBuilder.data( ApplicationJson.class, new ApplicationJson( applicationInfo ) );
            }

            ctx.broadcaster.broadcast( eventBuilder.build() );
        }
    }

    private static class SseContextHolder
    {
        final Sse sse;

        final SseBroadcaster broadcaster;

        SseContextHolder( final Sse sse )
        {
            this.sse = sse;
            this.broadcaster = sse.newBroadcaster();
        }
    }
}
