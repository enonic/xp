package com.enonic.xp.portal.handler;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.repository.RepositoryId;
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

            final RepositoryId repositoryId = portalRequest.getRepositoryId();
            if ( repositoryId != null )
            {
                ContextAccessor.current().getLocalScope().setAttribute( repositoryId );
            }
            ContextAccessor.current().getLocalScope().setAttribute( portalRequest.getBranch() );

            final WebResponse returnedWebResponse = webHandlerChain.handle( portalRequest, webResponse );
            exceptionMapper.throwIfNeeded( returnedWebResponse );
            return returnedWebResponse;
        }
        catch ( Exception e )
        {
            return handleError( portalRequest, e );
        }
    }

    protected abstract PortalRequest createPortalRequest( WebRequest webRequest, WebResponse webResponse );

    private WebResponse handleError( final WebRequest webRequest, final Exception e )
    {
        final WebException webException = exceptionMapper.map( e );
        final WebResponse webResponse = exceptionRenderer.render( webRequest, webException );
        webRequest.getRawRequest().setAttribute( "error.handled", Boolean.TRUE );

        return webResponse;
    }
}
