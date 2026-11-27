package com.enonic.xp.portal.impl.handler;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.impl.idprovider.OAuthMetadataService;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.vhost.WellKnownPaths;

/**
 * Serves the OAuth well-known documents at the root of a host: the protected resource metadata
 * of any path XP serves there, and the authorization server metadata of an id provider's
 * endpoints. The vhost filter has already matched the mapping serving the described path.
 */
@Component(immediate = true, service = WebHandler.class)
@NullMarked
public class WellKnownHandler
    extends BaseWebHandler
{
    private final OAuthMetadataService metadata;

    @Activate
    public WellKnownHandler( @Reference final OAuthMetadataService metadata )
    {
        super( -100, WellKnownDocuments.METHODS );
        this.metadata = metadata;
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        final Object connector = webRequest.getRawRequest() == null ? null : webRequest.getRawRequest().getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE );
        return DispatchConstants.WEB_CONNECTOR.equals( connector ) && WellKnownPaths.documentOf( webRequest.getRawPath() ) != null;
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        return WellKnownDocuments.serve( metadata, webRequest );
    }
}
