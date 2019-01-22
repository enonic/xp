package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = WebHandler.class)
public class AdminPortalHandler
    extends BasePortalHandler
{
    private final static String BASE_URI_START = "/admin/site";

    private final static Pattern BASE_URI_PATTERN = Pattern.compile( "^" + BASE_URI_START + "/(edit|preview|admin|inline)" );

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( BASE_URI_START );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final Matcher matcher = BASE_URI_PATTERN.matcher( webRequest.getRawPath() );
        if ( !matcher.find() )
        {
            throw WebException.notFound( "Mode needs to be specified" );
        }
        final String baseUri = matcher.group( 0 );
        final RenderMode mode = RenderMode.from( matcher.group( 1 ) );
        final String baseSubPath = webRequest.getRawPath().substring( baseUri.length() + 1 );
        final Branch branch = findBranch( baseSubPath );
        final ContentPath contentPath = findContentPath( baseSubPath );

        final PortalRequest portalRequest = new PortalRequest( webRequest );
        portalRequest.setBaseUri( baseUri );
        portalRequest.setBranch( branch );
        portalRequest.setContentPath( contentPath );
        portalRequest.setMode( mode );

        return portalRequest;
    }

    @Reference
    public void setExceptionMapper( final ExceptionMapper exceptionMapper )
    {
        this.exceptionMapper = exceptionMapper;
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }
}
