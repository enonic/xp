package com.enonic.xp.impl.server.rest;

import java.util.UUID;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationUninstalledJson;
import com.enonic.xp.impl.server.rest.model.ListApplicationJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

@Path("app")
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public class SseEntryPoint
    implements JaxRsComponent, EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SseEntryPoint.class );

    private static final String EVENT_TYPE = "application.cluster";

    private static final String EVENT_TYPE_KEY = "eventType";

    private static final String INSTALLED = "installed";

    private static final String STATE = "state";

    private static final String UNINSTALLED = "uninstalled";

    private static final String APPLICATION_KEY_PARAM = "key";

    private final ApplicationService applicationService;

    private volatile SseContextHolder contextHolder;

    @Activate
    public SseEntryPoint( final @Reference ApplicationService applicationService )
    {
        this.applicationService = applicationService;
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

        final OutboundSseEvent sseEvent = ctx.sse.newEventBuilder().
            name( "list" ).
            id( UUID.randomUUID().toString() ).
            mediaType( MediaType.APPLICATION_JSON_TYPE ).
            data( ListApplicationJson.class, new ListApplicationJson( applicationService.getInstalledApplications().stream().
                map( application -> new ApplicationInfoJson( application, applicationService.isLocalApplication( application.getKey() ) ) ).
                collect( Collectors.toList() ) ) ).build();

        sseEventSink.send( sseEvent );

        ctx.broadcaster.register( sseEventSink );
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( EVENT_TYPE.equals( event.getType() ) && event.isLocalOrigin() )
        {
            event.getValueAs( String.class, EVENT_TYPE_KEY ).
                ifPresent( eventSubType -> {
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
            LOG.debug( "Skipping {} {}. Please to subscribe to send SSE events.", EVENT_TYPE, eventSubType );
            return;
        }

        if ( event.getValue( APPLICATION_KEY_PARAM ).isPresent() )
        {
            final OutboundSseEvent.Builder eventBuilder = ctx.sse.newEventBuilder().
                name( eventSubType ).
                id( UUID.randomUUID().toString() ).
                mediaType( MediaType.APPLICATION_JSON_TYPE );

            if ( UNINSTALLED.equals( eventSubType ) )
            {
                eventBuilder.data( ApplicationUninstalledJson.class,
                                   new ApplicationUninstalledJson( event.getValue( APPLICATION_KEY_PARAM ).get().toString() ) );
            }
            else
            {
                final ApplicationKey applicationKey = ApplicationKey.from( event.getValue( APPLICATION_KEY_PARAM ).get().toString() );

                final Application application = applicationService.getInstalledApplication( applicationKey );

                if ( application == null )
                {
                    LOG.warn( "Application \"{}\" not found", applicationKey );
                    return;
                }

                final boolean localApplication = applicationService.isLocalApplication( application.getKey() );

                eventBuilder.data( ApplicationInfoJson.class, new ApplicationInfoJson( application, localApplication ) );
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
