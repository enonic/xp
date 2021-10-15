package com.enonic.xp.portal.impl.handler.unknown;

import java.util.EnumSet;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public class UnknownEndpointHandler
    extends BaseWebHandler
{
    public UnknownEndpointHandler()
    {
        super( 1, EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ) );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getEndpointPath() != null;
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        throw WebException.notFound( String.format( "No handler for the [%s] endpointPath", webRequest.getEndpointPath() ) );
    }
}
