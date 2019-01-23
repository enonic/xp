package com.enonic.xp.portal.impl.handler.portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.BaseSiteHandler;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = WebHandler.class)
public class SiteHandler
    extends BaseSiteHandler
{
    private final static String BASE_URI = "/site";

    private final static String BRANCH_PREFIX = BASE_URI + "/";

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( BRANCH_PREFIX );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final String baseSubPath = webRequest.getRawPath().substring( BRANCH_PREFIX.length() );
        return doCreatePortalRequest( webRequest, BASE_URI, baseSubPath );
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