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
import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
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

@Component
public final class ExceptionRendererImpl
    implements ExceptionRenderer
{
    private final static Logger LOG = LoggerFactory.getLogger( ExceptionRendererImpl.class );

    private ResourceService resourceService;

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private ContentService contentService;

    @Override
    public PortalResponse render( final PortalRequest req, final PortalException cause )
    {
        if ( RenderMode.LIVE == req.getMode() || RenderMode.PREVIEW == req.getMode() )
        {
            try
            {
                final PortalResponse portalError = renderCustomError( req, cause );
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
        return renderInternalErrorPage( req, cause );
    }

    private PortalResponse renderCustomError( final PortalRequest req, final PortalException cause )
    {
        Site site = req.getSite();
        if ( site == null )
        {
            site = resolveSiteFromPath( req );
        }

        if ( site != null )
        {
            final Site prevSite = req.getSite();
            req.setSite( site );
            try
            {
                final PortalError portalError = PortalError.create().
                    status( cause.getStatus() ).
                    message( cause.getMessage() ).
                    exception( cause ).
                    request( req ).build();

                for ( SiteConfig siteConfig : site.getSiteConfigs() )
                {
                    final PortalResponse response = renderApplicationCustomError( siteConfig.getApplicationKey(), portalError );
                    if ( response != null )
                    {
                        return response;
                    }
                }
            }
            finally
            {
                req.setSite( prevSite );
            }
        }

        return null;
    }

    private Site resolveSiteFromPath( final PortalRequest req )
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

    private PortalResponse renderInternalErrorPage( final PortalRequest req, final PortalException cause )
    {
        final ExceptionInfo info = toErrorInfo( cause );
        logIfNeeded( info );
        return info.toResponse( req );
    }

    private ExceptionInfo toErrorInfo( final PortalException cause )
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
