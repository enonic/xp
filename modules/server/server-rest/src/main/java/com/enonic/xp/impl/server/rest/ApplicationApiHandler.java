package com.enonic.xp.impl.server.rest;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.UuidHelper;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.impl.server.rest.model.ApplicationActionResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.impl.server.rest.model.ApplicationUninstalledJson;
import com.enonic.xp.impl.server.rest.model.ListApplicationJson;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.multipart.MultipartService;
import com.enonic.xp.web.sse.SseConfig;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

@Component(property = {"key=server:app", "title=Applications API", "mount=management", "allowedPrincipals=role:system.admin"})
public class ApplicationApiHandler
    implements UniversalApiHandler, EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationApiHandler.class );

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.create();

    private static final String APP_API = "server:app";

    private static final String SSE_GROUP = "server:app:events";

    private static final String EVENT_TYPE = "application.cluster";

    private static final String EVENT_TYPE_KEY = "eventType";

    private static final String INSTALLED = "installed";

    private static final String STATE = "state";

    private static final String UNINSTALLED = "uninstalled";

    private static final String APPLICATION_KEY_PARAM = "key";

    private final ApplicationResourceService applicationResourceService;

    private final MultipartService multipartService;

    private final SseManager sseManager;

    @Activate
    public ApplicationApiHandler( @Reference final ApplicationResourceService applicationResourceService,
                                  @Reference final MultipartService multipartService, @Reference final SseManager sseManager )
    {
        this.applicationResourceService = applicationResourceService;
        this.multipartService = multipartService;
        this.sseManager = sseManager;
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final String apiPath = WebHandlerHelper.findApiPath( request, APP_API );

        if ( "/events".equals( apiPath ) && request.getMethod() == HttpMethod.GET )
        {
            return WebResponse.create().status( HttpStatus.OK ).sse( SseConfig.empty() ).build();
        }

        if ( request.getMethod() != HttpMethod.POST )
        {
            return WebResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        try
        {
            return switch ( apiPath )
            {
                case "/install" -> handleInstall( request );
                case "/installUrl" -> handleInstallUrl( request );
                case "/start" -> handleStart( request );
                case "/stop" -> handleStop( request );
                case "/uninstall" -> handleUninstall( request );
                default -> WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
            };
        }
        catch ( JsonProcessingException e )
        {
            return WebResponse.create().status( HttpStatus.BAD_REQUEST ).build();
        }
    }

    @Override
    public void onSseEvent( final SseEvent event )
    {
        if ( event.getType() == SseEventType.OPEN )
        {
            final UUID id = event.getClientId();
            sseManager.addToGroup( SSE_GROUP, id );

            try
            {
                final ListApplicationJson list = new ListApplicationJson(
                    applicationResourceService.getInstalledApplications().stream().map( ApplicationJson::new ).toList() );
                sseManager.send( id, SseMessage.create()
                    .id( UuidHelper.newUUIDv7().toString() )
                    .event( "list" )
                    .data( OBJECT_MAPPER.writeValueAsString( list ) )
                    .build() );
            }
            catch ( JsonProcessingException e )
            {
                LOG.warn( "Failed to serialize application list", e );
            }
        }
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( !EVENT_TYPE.equals( event.getType() ) || !event.isLocalOrigin() )
        {
            return;
        }

        event.getValueAs( String.class, EVENT_TYPE_KEY ).ifPresent( eventSubType -> {
            switch ( eventSubType )
            {
                case INSTALLED, STATE, UNINSTALLED -> handleApplicationEvent( event, eventSubType );
                default -> LOG.debug( "Ignoring {} {}", EVENT_TYPE, eventSubType );
            }
        } );
    }

    private void handleApplicationEvent( final Event event, final String eventSubType )
    {
        if ( sseManager.getGroupSize( SSE_GROUP ) == 0 )
        {
            return;
        }

        event.getValue( APPLICATION_KEY_PARAM ).ifPresent( keyValue -> {
            try
            {
                final String data;
                if ( UNINSTALLED.equals( eventSubType ) )
                {
                    data = OBJECT_MAPPER.writeValueAsString( new ApplicationUninstalledJson( keyValue.toString() ) );
                }
                else
                {
                    final ApplicationKey applicationKey = ApplicationKey.from( keyValue.toString() );
                    final ApplicationInfoJson applicationInfo = applicationResourceService.getInstalledApplication( applicationKey );
                    if ( applicationInfo == null )
                    {
                        LOG.warn( "Application {} not found", applicationKey );
                        return;
                    }
                    data = OBJECT_MAPPER.writeValueAsString( new ApplicationJson( applicationInfo ) );
                }
                sseManager.sendToGroup( SSE_GROUP, SseMessage.create()
                    .id( UuidHelper.newUUIDv7().toString() )
                    .event( eventSubType )
                    .data( data )
                    .build() );
            }
            catch ( JsonProcessingException e )
            {
                LOG.warn( "Failed to serialize application event", e );
            }
        } );
    }

    private WebResponse handleInstall( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationInfoJson result = applicationResourceService.install( multipartService.parse( request.getRawRequest() ) );

        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .body( OBJECT_MAPPER.writeValueAsString( result ) )
            .build();
    }

    private WebResponse handleInstallUrl( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationInstallParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationInstallParams.class );
        final ApplicationInfoJson result = applicationResourceService.installUrl( params );

        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .body( OBJECT_MAPPER.writeValueAsString( result ) )
            .build();
    }

    private WebResponse handleStart( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationParams.class );
        final ApplicationActionResultJson result = applicationResourceService.start( params );

        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .body( OBJECT_MAPPER.writeValueAsString( result ) )
            .build();
    }

    private WebResponse handleStop( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationParams.class );
        final ApplicationActionResultJson result = applicationResourceService.stop( params );

        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .body( OBJECT_MAPPER.writeValueAsString( result ) )
            .build();
    }

    private WebResponse handleUninstall( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationParams.class );
        final ApplicationActionResultJson result = applicationResourceService.uninstall( params );

        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .body( OBJECT_MAPPER.writeValueAsString( result ) )
            .build();
    }
}
