package com.enonic.xp.impl.server.rest.api;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.UuidHelper;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.impl.server.rest.ApplicationResourceService;
import com.enonic.xp.impl.server.rest.IconHash;
import com.enonic.xp.impl.server.rest.model.ApplicationActionResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.impl.server.rest.model.ApplicationUninstalledJson;
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

/**
 * {@code server:app} - installed applications. {@code install} takes the bundle as a multipart upload; {@code pull}
 * fetches it from a URL the server can reach (with its own allow-list and checksum policy, see
 * {@code com.enonic.xp.server.management.app.cfg}), so a vhost can grant one without the other. {@code /installUrl} and
 * {@code /events} are aliases of {@code /pull} and {@code /watch}.
 * <p>
 * An application that has an icon reports its {@code iconHash}; {@code /icon?key=<key>&v=<iconHash>} serves the icon under the
 * {@code list} verb. With the current hash in {@code v} the response never needs revalidation; any other request is revalidated
 * against the {@code ETag}.
 * <p>
 * {@code list} reports every application on this node. {@code watch} covers only the applications installed cluster-wide: its
 * initial {@code list} event and the changes that follow leave out local and system applications, and report a change
 * whichever node it was made through.
 */
@Component(service = {UniversalApiHandler.class, EventListener.class}, property = {"key=server:app", "title=Applications API",
    "mount=management", "allowedPrincipals=role:system.admin"})
public class ApplicationApiHandler
    extends ManagementApiHandler
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationApiHandler.class );

    private static final ObjectMapper OBJECT_MAPPER = MAPPER;

    static final String KEY = "server:app";

    private static final String SSE_GROUP = "server:app:events";

    private static final String EVENT_TYPE = "application.cluster";

    private static final String EVENT_TYPE_KEY = "eventType";

    private static final String INSTALLED = "installed";

    private static final String STATE = "state";

    private static final String UNINSTALLED = "uninstalled";

    private static final String APPLICATION_KEY_PARAM = "key";

    private static final String ICON_VERSION_PARAM = "v";

    private static final MediaType SVG = MediaType.SVG_UTF_8.withoutParameters();

    private static final String SVG_CONTENT_SECURITY_POLICY =
        "sandbox; default-src 'none'; base-uri 'none'; form-action 'none'; style-src 'unsafe-inline'";

    private static final String ICON_CACHE_IMMUTABLE = "private, max-age=31536000, immutable";

    private static final String ICON_CACHE_REVALIDATE = "private, no-cache";

    private final ApplicationResourceService applicationResourceService;

    private final MultipartService multipartService;

    private final SseManager sseManager;

    @Activate
    public ApplicationApiHandler( @Reference final ApplicationResourceService applicationResourceService,
                                  @Reference final MultipartService multipartService, @Reference final SseManager sseManager )
    {
        super( KEY );
        this.applicationResourceService = applicationResourceService;
        this.multipartService = multipartService;
        this.sseManager = sseManager;

        route( HttpMethod.GET, "/", "list", ( request, params ) -> handleList() );
        route( HttpMethod.GET, "/icon", "list", ( request, params ) -> handleIcon( request ) );
        route( HttpMethod.POST, "/install", "install", ( request, params ) -> handleInstall( request ) );
        route( HttpMethod.POST, "/pull", "pull", ( request, params ) -> handleInstallUrl( request ) );
        route( HttpMethod.POST, "/installUrl", "pull", ( request, params ) -> handleInstallUrl( request ) );
        route( HttpMethod.POST, "/uninstall", "uninstall", ( request, params ) -> handleUninstall( request ) );
        route( HttpMethod.POST, "/start", "start", ( request, params ) -> handleStart( request ) );
        route( HttpMethod.POST, "/stop", "stop", ( request, params ) -> handleStop( request ) );
        route( HttpMethod.GET, "/watch", "watch", ( request, params ) -> handleWatch() );
        route( HttpMethod.GET, "/events", "watch", ( request, params ) -> handleWatch() );
    }

    private WebResponse handleWatch()
    {
        return WebResponse.create().status( HttpStatus.OK ).sse( SseConfig.empty() ).build();
    }

    private WebResponse handleList()
        throws JsonProcessingException
    {
        return json( new ApplicationsJson( applicationResourceService.getInstalledApplications() ) );
    }

    private WebResponse handleIcon( final WebRequest request )
    {
        final String key = param( request, APPLICATION_KEY_PARAM );
        if ( key == null || key.isBlank() )
        {
            throw new IllegalArgumentException( "Missing parameter: " + APPLICATION_KEY_PARAM );
        }

        final Optional<Icon> found = applicationResourceService.getApplicationIcon( ApplicationKey.from( key ) );
        if ( found.isEmpty() )
        {
            return error( HttpStatus.NOT_FOUND, String.format( "Icon of application [%s] not found", key ) );
        }

        final Icon icon = found.get();
        final String hash = IconHash.of( icon );
        final String etag = "\"" + hash + "\"";

        final WebResponse.Builder<?> response = WebResponse.create()
            .header( HttpHeaders.ETAG, etag )
            .header( HttpHeaders.CACHE_CONTROL,
                     hash.equals( param( request, ICON_VERSION_PARAM ) ) ? ICON_CACHE_IMMUTABLE : ICON_CACHE_REVALIDATE );

        if ( matches( request.getHeaders().get( HttpHeaders.IF_NONE_MATCH ), etag ) )
        {
            return response.status( HttpStatus.NOT_MODIFIED ).build();
        }

        final MediaType contentType = MediaType.parse( icon.getMimeType() );
        response.status( HttpStatus.OK )
            .contentType( contentType )
            .body( icon.toByteArray() )
            .header( HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff" );

        if ( contentType.is( SVG ) )
        {
            response.header( HttpHeaders.CONTENT_SECURITY_POLICY, SVG_CONTENT_SECURITY_POLICY );
        }

        return response.build();
    }

    private static boolean matches( final String ifNoneMatch, final String etag )
    {
        if ( ifNoneMatch == null )
        {
            return false;
        }
        return ifNoneMatch.strip().equals( "*" ) || Arrays.stream( ifNoneMatch.split( "," ) )
            .map( String::strip )
            .map( candidate -> candidate.startsWith( "W/" ) ? candidate.substring( 2 ) : candidate )
            .anyMatch( etag::equals );
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
                final ApplicationsJson list = new ApplicationsJson( applicationResourceService.getGlobalApplications() );
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
        if ( !EVENT_TYPE.equals( event.getType() ) )
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
            final ApplicationKey applicationKey = ApplicationKey.from( keyValue.toString() );
            if ( applicationResourceService.isLocalApplication( applicationKey ) )
            {
                LOG.debug( "Ignoring {} {} for local application {}", EVENT_TYPE, eventSubType, applicationKey );
                return;
            }
            try
            {
                final String data;
                if ( UNINSTALLED.equals( eventSubType ) )
                {
                    data = OBJECT_MAPPER.writeValueAsString( new ApplicationUninstalledJson( keyValue.toString() ) );
                }
                else
                {
                    final ApplicationInfoJson applicationInfo = applicationResourceService.getInstalledApplication( applicationKey );
                    if ( applicationInfo == null )
                    {
                        LOG.debug( "Application {} is not installed on this node", applicationKey );
                        return;
                    }
                    data = OBJECT_MAPPER.writeValueAsString( applicationInfo );
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

    public record ApplicationsJson(List<ApplicationInfoJson> applications)
    {
    }
}
