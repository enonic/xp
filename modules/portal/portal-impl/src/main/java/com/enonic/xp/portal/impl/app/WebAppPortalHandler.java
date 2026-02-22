package com.enonic.xp.portal.impl.app;

import java.util.regex.MatchResult;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.portal.impl.handler.PathMatchers;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = WebHandler.class)
public class WebAppPortalHandler
    extends BasePortalHandler
{
    @Activate
    public WebAppPortalHandler( @Reference final ExceptionMapper exceptionMapper, @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getBasePath().startsWith( PathMatchers.WEBAPP_PREFIX );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final MatchResult matcher = PathMatchers.webapp( webRequest );
        if ( !matcher.hasMatch() )
        {
            throw WebException.notFound( "Invalid webapp URL" );
        }

        final PortalRequest portalRequest = new PortalRequest( webRequest );
        portalRequest.setBaseUri( matcher.group( "base" ) );
        portalRequest.setApplicationKey( ApplicationKey.from( matcher.group( "app" ) ) );
        return portalRequest;
    }
}
