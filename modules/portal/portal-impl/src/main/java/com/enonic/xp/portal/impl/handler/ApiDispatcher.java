package com.enonic.xp.portal.impl.handler;

import java.io.IOException;

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

    private volatile boolean legacyAssetServiceEnabled;

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
        this.legacyAssetServiceEnabled = config.legacy_assetService_enabled();
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getEndpointPath() != null || webRequest.getBasePath().startsWith( PathMatchers.API_PREFIX );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        return switch ( HandlerHelper.findEndpoint( webRequest ) )
        {
            case "attachment" ->
                doHandleLegacyHandler( webRequest, webResponse, legacyAttachmentServiceEnabled, attachmentHandler::handle );
            case "image" -> doHandleLegacyHandler( webRequest, webResponse, legacyImageServiceEnabled, imageHandler::handle );
            case "service" -> doHandleLegacyHandler( webRequest, webResponse, legacyHttpServiceEnabled, serviceHandler::handle );
            case "asset" -> doHandleLegacyHandler( webRequest, webResponse, legacyAssetServiceEnabled, assetHandler::handle );
            case "error" -> doHandleLegacyHandler( webRequest, webResponse, true, errorHandler::handle );
            case "idprovider" -> doHandleLegacyHandler( webRequest, webResponse, true, identityHandler::handle );
            case "component" -> doHandleLegacyHandler( webRequest, webResponse, true, componentHandler::handle );
            case null, default -> apiHandler.handle( webRequest );
        };
    }

    private WebResponse doHandleLegacyHandler( final WebRequest webRequest, WebResponse webResponse, final boolean handlerEnabled,
                                               final HandlerFunction<WebRequest, WebResponse> handler )
        throws IOException
    {
        return handlerEnabled ? handler.apply( webRequest ) : WebResponse.create( webResponse ).status( HttpStatus.NOT_FOUND ).build();
    }

    @FunctionalInterface
    private interface HandlerFunction<T extends WebRequest, R extends WebResponse>
    {
        R apply( T t )
            throws IOException;
    }
}
