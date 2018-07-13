package com.enonic.xp.portal.impl.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = WebHandler.class)
public class AppPortalHandler
    extends BasePortalHandler
{
    public final static Pattern PATTERN = Pattern.compile( "(/app/([^/]+))(?:/.*)?" );

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return PATTERN.matcher( webRequest.getRawPath() ).
            matches();
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final Matcher matcher = PATTERN.matcher( webRequest.getRawPath() );
        matcher.matches();

        final PortalRequest portalRequest = new PortalRequest( webRequest );
        portalRequest.setBaseUri( matcher.group( 1 ) );
        portalRequest.setApplicationKey( ApplicationKey.from( matcher.group( 2 ) ) );
        return portalRequest;
    }

    @Reference
    public void setWebExceptionMapper( final ExceptionMapper exceptionMapper )
    {
        this.exceptionMapper = exceptionMapper;
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }
}