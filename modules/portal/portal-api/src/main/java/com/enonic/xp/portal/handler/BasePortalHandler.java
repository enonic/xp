package com.enonic.xp.portal.handler;

import com.google.common.base.Strings;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

public abstract class BasePortalHandler
    extends BaseWebHandler
{
    protected ExceptionMapper exceptionMapper;

    protected ExceptionRenderer exceptionRenderer;

    public BasePortalHandler()
    {
        super( -50 );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final PortalRequest portalRequest;
        if ( webRequest instanceof PortalRequest )
        {
            portalRequest = (PortalRequest) webRequest;
        }
        else
        {
            portalRequest = createPortalRequest( webRequest, webResponse );
        }

        try
        {
            PortalRequestAccessor.set( portalRequest.getRawRequest(), portalRequest );
            ContextAccessor.current().getLocalScope().setAttribute( portalRequest.getBranchId() );

            final WebResponse returnedWebResponse = webHandlerChain.handle( portalRequest, webResponse );
            exceptionMapper.throwIfNeeded( returnedWebResponse );
            return returnedWebResponse;
        }
        catch ( Exception e )
        {
            return handleError( portalRequest, e );
        }
    }

    protected abstract PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse );

    private WebResponse handleError( final WebRequest webRequest, final Exception e )
    {
        final WebException webException = exceptionMapper.map( e );
        final WebResponse webResponse = exceptionRenderer.render( webRequest, webException );
        webRequest.getRawRequest().setAttribute( "error.handled", Boolean.TRUE );

        return webResponse;
    }

    protected static BranchId findBranch( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        final String result = baseSubPath.substring( 0, index > 0 ? index : baseSubPath.length() );
        if ( Strings.isNullOrEmpty( result ) )
        {
            throw WebException.notFound( "Branch needs to be specified" );
        }
        return BranchId.from( result );
    }

    protected static ContentPath findContentPath( final String baseSubPath )
    {
        final String branchSubPath = findPathAfterBranch( baseSubPath );
        final int underscore = branchSubPath.indexOf( "/_/" );
        final String result = branchSubPath.substring( 0, underscore > -1 ? underscore : branchSubPath.length() );
        return ContentPath.from( result.startsWith( "/" ) ? result : ( "/" + result ) );
    }

    protected static String findPathAfterBranch( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        return baseSubPath.substring( index > 0 ? index : baseSubPath.length() );
    }
}
