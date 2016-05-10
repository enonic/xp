package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebExceptionMapper;
import com.enonic.xp.web.handler.WebExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = WebHandler.class)
public class AdminPortalHandler
    extends BaseWebHandler
{
    private final static String BASE_URI_START = "/admin/portal";

    private final static Pattern PATTERN = Pattern.compile( "^" + BASE_URI_START + "/(edit|preview|admin)/" );

    private WebExceptionMapper webExceptionMapper;

    private WebExceptionRenderer webExceptionRenderer;

    public AdminPortalHandler()
    {
        super( -50 );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return PATTERN.matcher( webRequest.getPath() ).find();
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final PortalWebRequest portalWebRequest;
        if ( webRequest instanceof PortalWebRequest )
        {
            portalWebRequest = (PortalWebRequest) webRequest;
        }
        else
        {
            final Matcher matcher = PATTERN.matcher( webRequest.getPath() );
            matcher.find();
            final String baseUri = matcher.group( 0 );
            final RenderMode mode = RenderMode.from( matcher.group( 1 ) );
            final String baseSubPath = webRequest.getPath().substring( baseUri.length() );
            final Branch branch = findBranch( baseSubPath );
            final ContentPath contentPath = findContentPath( baseSubPath );

            portalWebRequest = PortalWebRequest.create( webRequest ).
                baseUri( baseUri ).
                branch( branch ).
                contentPath( contentPath ).
                mode( mode ).
                build();
        }

        try
        {
            final WebResponse returnedWebResponse = webHandlerChain.handle( portalWebRequest, new PortalWebResponse() );
            webExceptionMapper.throwIfNeeded( returnedWebResponse );
            return returnedWebResponse;
        }
        catch ( Exception e )
        {
            return handleError( portalWebRequest, e );
        }
    }

    private WebResponse handleError( final WebRequest webRequest, final Exception e )
    {
        final WebException webException = webExceptionMapper.map( e );
        return webExceptionRenderer.render( webRequest, webException );
    }


    private static Branch findBranch( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        final String result = baseSubPath.substring( 0, index > 0 ? index : baseSubPath.length() );
        return Strings.isNullOrEmpty( result ) ? null : Branch.from( result );
    }

    private static ContentPath findContentPath( final String baseSubPath )
    {
        final String branchSubPath = findPathAfterBranch( baseSubPath );
        final int underscore = branchSubPath.indexOf( "/_/" );
        final String result = branchSubPath.substring( 0, underscore > -1 ? underscore : branchSubPath.length() );
        return ContentPath.from( result.startsWith( "/" ) ? result : ( "/" + result ) );
    }

    private static String findPathAfterBranch( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        return baseSubPath.substring( index > 0 ? index : baseSubPath.length() );
    }

    @Reference
    public void setWebExceptionMapper( final WebExceptionMapper webExceptionMapper )
    {
        this.webExceptionMapper = webExceptionMapper;
    }

    @Reference
    public void setWebExceptionRenderer( final WebExceptionRenderer webExceptionRenderer )
    {
        this.webExceptionRenderer = webExceptionRenderer;
    }
}
