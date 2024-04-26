package com.enonic.xp.portal.impl.handler;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(service = WebHandler.class)
public class ApiDispatcher
    extends BaseWebHandler
{
    private static final Pattern HANDLER_PATTERN = Pattern.compile( "^/(_|api)/(?<handler>[^/]+)" );

    private static final Predicate<WebRequest> IS_STANDARD_METHOD = req -> HttpMethod.standard().contains( req.getMethod() );

    private static final Predicate<WebRequest> IS_GET_HEAD_OPTIONS_METHOD =
        req -> EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ).contains( req.getMethod() );

    private static final Predicate<WebRequest> IS_SITE_BASE = req -> req instanceof PortalRequest && ( (PortalRequest) req ).isSiteBase();

    private final SlashApiHandler apiHandler;

    private final ComponentHandler componentHandler;

    private final AssetHandler assetHandler;

    private final ServiceHandler serviceHandler;

    private final IdentityHandler identityHandler;

    private final ImageHandler imageHandler;

    private final AttachmentHandler attachmentHandler;

    private final ErrorHandler errorHandler;

    private final MediaHandler mediaHandler;


    @Activate
    public ApiDispatcher( @Reference final SlashApiHandler apiHandler, @Reference final ComponentHandler componentHandler,
                          @Reference final AssetHandler assetHandler, @Reference final ServiceHandler serviceHandler,
                          @Reference final IdentityHandler identityHandler, @Reference final ImageHandler imageHandler,
                          @Reference final AttachmentHandler attachmentHandler, @Reference final ErrorHandler errorHandler,
                          @Reference final MediaHandler mediaHandler )
    {
        super( 1 );

        this.apiHandler = apiHandler;
        this.componentHandler = componentHandler;
        this.assetHandler = assetHandler;
        this.serviceHandler = serviceHandler;
        this.identityHandler = identityHandler;
        this.imageHandler = imageHandler;
        this.attachmentHandler = attachmentHandler;
        this.errorHandler = errorHandler;
        this.mediaHandler = mediaHandler;
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );
        if ( path.startsWith( "/_/" ) || path.startsWith( "/api/" ) )
        {
            final String handler = resolveHandler( webRequest );
            return switch ( handler )
            {
                case "attachment", "image" -> IS_GET_HEAD_OPTIONS_METHOD.test( webRequest ) && IS_SITE_BASE.test( webRequest );
                case "media", "asset" -> IS_GET_HEAD_OPTIONS_METHOD.test( webRequest );
                case "component" -> IS_STANDARD_METHOD.test( webRequest ) && IS_SITE_BASE.test( webRequest );
                case "idprovider", "service", "error" -> IS_STANDARD_METHOD.test( webRequest );
                default -> true;
            };
        }
        else
        {
            return false;
        }
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        final String handler = resolveHandler( webRequest );
        return switch ( handler )
        {
            case "attachment" -> attachmentHandler.handle( webRequest );
            case "image" -> imageHandler.handle( webRequest );
            case "media" -> mediaHandler.handle( webRequest, webResponse );
            case "error" -> errorHandler.handle( webRequest );
            case "idprovider" -> identityHandler.handle( webRequest, webResponse );
            case "service" -> serviceHandler.handle( webRequest );
            case "asset" -> assetHandler.handle( webRequest );
            case "component" -> componentHandler.handle( webRequest );
            default -> apiHandler.handle( webRequest );
        };
    }

    private String resolveHandler( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );
        final Matcher matcher = HANDLER_PATTERN.matcher( path );
        if ( !matcher.find() )
        {
            throw new IllegalStateException( "Invalid API path: " + path );
        }
        return matcher.group( "handler" );
    }
}
