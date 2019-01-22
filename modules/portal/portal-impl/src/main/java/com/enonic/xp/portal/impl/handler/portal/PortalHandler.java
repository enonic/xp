package com.enonic.xp.portal.impl.handler.portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = WebHandler.class)
public class PortalHandler
    extends BasePortalHandler
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
        final Branch branch = findBranch( baseSubPath );
        final ContentPath contentPath = findContentPath( baseSubPath );

        final PortalRequest portalRequest = new PortalRequest( webRequest );
        portalRequest.setBaseUri( BASE_URI );
        portalRequest.setBranch( branch );
        portalRequest.setContentPath( contentPath );

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