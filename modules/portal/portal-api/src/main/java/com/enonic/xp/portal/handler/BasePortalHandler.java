package com.enonic.xp.portal.handler;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.csp.ContentSecurityPolicy;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

public abstract class BasePortalHandler
    extends BaseWebHandler
{
    private static final String CSP_HEADER = "Content-Security-Policy";

    private static final String CSP_REPORT_ONLY_HEADER = "Content-Security-Policy-Report-Only";

    protected ExceptionRenderer exceptionRenderer;

    public BasePortalHandler()
    {
        super( -50 );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
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

        PortalRequestAccessor.set( portalRequest.getRawRequest(), portalRequest );

        final RepositoryId repositoryId = portalRequest.getRepositoryId();
        if ( repositoryId != null )
        {
            ContextAccessor.current().getLocalScope().setAttribute( repositoryId );
        }
        final Branch branch = portalRequest.getBranch();
        if ( branch != null )
        {
            ContextAccessor.current().getLocalScope().setAttribute( branch );
        }

        final WebResponse response;
        try
        {
            response = exceptionRenderer.maybeThrow( portalRequest, webHandlerChain.handle( portalRequest, webResponse ) );
        }
        catch ( Exception e )
        {
            return applyContentSecurityPolicy( portalRequest, exceptionRenderer.render( portalRequest, e ) );
        }
        return applyContentSecurityPolicy( portalRequest, response );
    }

    protected abstract PortalRequest createPortalRequest( WebRequest webRequest, WebResponse webResponse );

    private static WebResponse applyContentSecurityPolicy( final PortalRequest portalRequest, final WebResponse response )
    {
        final ContentSecurityPolicy policy = portalRequest.getContentSecurityPolicy();
        final String headerValue = policy.build();
        final String header = policy.isReportOnly() ? CSP_REPORT_ONLY_HEADER : CSP_HEADER;
        if ( headerValue.isEmpty() || response.getHeaders().containsKey( header ) )
        {
            return response;
        }
        return PortalResponse.create( response ).header( header, headerValue ).build();
    }
}
