package com.enonic.xp.portal.impl.exception;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebExceptionRenderer;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component
public final class WebExceptionRendererImpl
    implements WebExceptionRenderer
{
    private final static Logger LOG = LoggerFactory.getLogger( WebExceptionRendererImpl.class );

    private ResourceService resourceService;

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private ContentService contentService;


    @Override
    public WebResponse render( final WebRequest webRequest, final WebException webException )
    {
        if ( webRequest instanceof PortalWebRequest )
        {
            PortalWebRequest portalWebRequest = (PortalWebRequest) webRequest;
            if ( RenderMode.LIVE == portalWebRequest.getMode() || RenderMode.PREVIEW == portalWebRequest.getMode() )
            {
                try
                {
                    final WebResponse portalError = renderCustomError( portalWebRequest, webException );
                    if ( portalError != null )
                    {
                        return portalError;
                    }
                }
                catch ( Exception e )
                {
                    LOG.error( "Exception while executing custom error handler", e );
                }
            }
        }
        return renderInternalErrorPage( webRequest, webException );
    }

    private WebResponse renderCustomError( final PortalWebRequest req, final WebException cause )
    {
        Site site = req.getSite();
        if ( site == null )
        {
            site = resolveSiteFromPath( req );
        }

        if ( site != null )
        {
            PortalWebRequest errorPortalWebRequest = PortalWebRequest.create( req ).
                site( site ).
                build();
            final PortalRequest portalRequest = PortalWebRequest.convertToPortalRequest( errorPortalWebRequest );

            final PortalError portalError = PortalError.create().
                status( cause.getStatus() ).
                message( cause.getMessage() ).
                exception( cause ).
                request( portalRequest ).
                build();

            for ( SiteConfig siteConfig : site.getSiteConfigs() )
            {
                final PortalResponse response = renderApplicationCustomError( siteConfig.getApplicationKey(), portalError );
                if ( response != null )
                {
                    return PortalWebResponse.convertToPortalWebResponse( response );
                }
            }
        }

        return null;
    }

    private Site resolveSiteFromPath( final PortalWebRequest req )
    {
        ContentPath path = req.getContentPath();
        while ( path != null && !path.isRoot() )
        {
            try
            {
                final ContentPath cp = path;
                final Content content = runAsContentAdmin( () -> this.contentService.getByPath( cp ) );
                if ( content.isSite() )
                {
                    return (Site) content;
                }
            }
            catch ( ContentNotFoundException e )
            {
                // not actual content found in the path, but there might still be a site in the parent
            }
            path = path.getParentPath();
        }
        return null;
    }

    private <T> T runAsContentAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).
                principals( RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN ).build() ).
            build().
            callWith( callable );
    }

    private PortalResponse renderApplicationCustomError( final ApplicationKey appKey, final PortalError portalError )
    {
        final ResourceKey script = ResourceKey.from( appKey, "site/error/error.js" );
        final Resource scriptResource = this.resourceService.getResource( script );
        if ( !scriptResource.exists() )
        {
            return null;
        }

        final ErrorHandlerScript errorHandlerScript = this.errorHandlerScriptFactory.errorScript( script );

        final PortalRequest request = portalError.getRequest();
        final ApplicationKey previousApp = request.getApplicationKey();
        // set application of the error handler in the current context PortalRequest
        try
        {
            request.setApplicationKey( appKey );
            return errorHandlerScript.execute( portalError );
        }
        finally
        {
            request.setApplicationKey( previousApp );
        }
    }

    private PortalWebResponse renderInternalErrorPage( final WebRequest req, final WebException cause )
    {
        final ExceptionInfo info = toErrorInfo( cause );
        logIfNeeded( info );
        final PortalResponse portalResponse = info.toResponse( req );
        return PortalWebResponse.convertToPortalWebResponse( portalResponse );
    }

    private ExceptionInfo toErrorInfo( final WebException cause )
    {
        return ExceptionInfo.create( cause.getStatus() ).
            cause( cause ).
            resourceService( this.resourceService );
    }

    private void logIfNeeded( final ExceptionInfo info )
    {
        if ( info.shouldLogAsError() )
        {
            LOG.error( info.getMessage(), info.getCause() );
        }
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setErrorHandlerScriptFactory( final ErrorHandlerScriptFactory value )
    {
        this.errorHandlerScriptFactory = value;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
