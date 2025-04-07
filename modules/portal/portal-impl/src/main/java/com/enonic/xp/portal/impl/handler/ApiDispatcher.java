package com.enonic.xp.portal.impl.handler;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public class ApiDispatcher
    extends BaseWebHandler
{
    private static final Pattern HANDLER_PATTERN = Pattern.compile( "^/(_|api)/(?<handler>[^/]+)" );

    private final SlashApiHandler apiHandler;

    private final ComponentHandler componentHandler;

    private final AssetHandler assetHandler;

    private final ServiceHandler serviceHandler;

    private final IdentityHandler identityHandler;

    private final ImageHandler imageHandler;

    private final AttachmentHandler attachmentHandler;

    private final ErrorHandler errorHandler;

    private volatile boolean legacyImageServiceEnabled;

    private volatile boolean legacyAttachmentServiceEnabled;

    private volatile boolean legacyHttpServiceEnabled;

    @Activate
    public ApiDispatcher( @Reference final SlashApiHandler apiHandler, @Reference final ComponentHandler componentHandler,
                          @Reference final AssetHandler assetHandler, @Reference final ServiceHandler serviceHandler,
                          @Reference final IdentityHandler identityHandler, @Reference final ImageHandler imageHandler,
                          @Reference final AttachmentHandler attachmentHandler, @Reference final ErrorHandler errorHandler )
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
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.legacyImageServiceEnabled = config.legacy_imageService_enabled();
        this.legacyAttachmentServiceEnabled = config.legacy_attachmentService_enabled();
        this.legacyHttpServiceEnabled = config.legacy_httpService_enabled();
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );
        return path.startsWith( "/_/" ) || path.startsWith( "/api/" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        final String handler = resolveHandler( webRequest );
        return switch ( handler )
        {
            case "attachment" ->
                doHandleLegacyHandler( webResponse, legacyAttachmentServiceEnabled, () -> attachmentHandler.handle( webRequest ) );
            case "image" -> doHandleLegacyHandler( webResponse, legacyImageServiceEnabled, () -> imageHandler.handle( webRequest ) );
            case "service" -> doHandleLegacyHandler( webResponse, legacyHttpServiceEnabled, () -> serviceHandler.handle( webRequest ) );
            case "error" -> errorHandler.handle( webRequest );
            case "idprovider" -> identityHandler.handle( webRequest, webResponse );
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

    private WebResponse doHandleLegacyHandler( final WebResponse webResponse, final boolean handlerEnabled,
                                               final Callable<WebResponse> handler )
        throws Exception
    {
        return handlerEnabled ? handler.call() : WebResponse.create( webResponse ).status( HttpStatus.NOT_FOUND ).build();
    }
}
