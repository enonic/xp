package com.enonic.xp.portal.impl.exception;

import java.io.IOException;
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
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.exception.ExceptionRenderer;

@Component
public final class ExceptionRendererImpl
    implements ExceptionRenderer
{
    private final static Logger LOG = LoggerFactory.getLogger( ExceptionRendererImpl.class );

    private static final String DEFAULT_HANDLER = "handleError";

    private static final String STATUS_HANDLER = "handle%d";

    private ResourceService resourceService;

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private ContentService contentService;

    private AuthControllerService authControllerService;

    private PostProcessor postProcessor;

    @Override
    public PortalResponse render( final WebRequest webRequest, final WebException cause )
    {

        final ExceptionInfo info = toErrorInfo( cause );
        logIfNeeded( info );

        if ( webRequest instanceof PortalRequest )
        {
            PortalRequest portalRequest = (PortalRequest) webRequest;
            final HttpStatus httpStatus = cause.getStatus();
            if ( httpStatus != null )
            {
                final String handlerMethod = String.format( STATUS_HANDLER, httpStatus.value() );
                final PortalResponse statusCustomError = renderCustomError( portalRequest, cause, handlerMethod );
                if ( statusCustomError != null )
                {
                    logIfNeeded( toErrorInfo( cause ) );
                    return statusCustomError;
                }
            }

            final PortalResponse idProviderError = renderIdProviderError( portalRequest, cause );
            if ( idProviderError != null )
            {
                logIfNeeded( toErrorInfo( cause ) );
                return idProviderError;
            }

            final PortalResponse defaultCustomError = renderCustomError( portalRequest, cause, DEFAULT_HANDLER );
            if ( defaultCustomError != null )
            {
                logIfNeeded( toErrorInfo( cause ) );
                return defaultCustomError;
            }
        }

        return renderInternalErrorPage( webRequest, cause );
    }

    private PortalResponse renderCustomError( final PortalRequest req, final WebException cause, final String handlerMethod )
    {
        if ( RenderMode.LIVE == req.getMode() || RenderMode.PREVIEW == req.getMode() )
        {
            try
            {
                PortalResponse portalResponse = doRenderCustomError( req, cause, handlerMethod );
                if ( portalResponse != null && portalResponse.isPostProcess() )
                {
                    portalResponse = this.postProcessor.processResponse( req, portalResponse );
                }
                return portalResponse;
            }
            catch ( Exception e )
            {
                LOG.error( "Exception while executing custom error handler", e );
            }
        }
        return null;
    }


    private PortalResponse doRenderCustomError( final PortalRequest req, final WebException cause, final String handlerMethod )
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
                    final PortalResponse response =
                        renderApplicationCustomError( siteConfig.getApplicationKey(), portalError, handlerMethod );
                    if ( response != null )
                    {
                        if ( response.isPostProcess() )
                        {
                            req.setApplicationKey( siteConfig.getApplicationKey() );
                        }
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

    private PortalResponse renderApplicationCustomError( final ApplicationKey appKey, final PortalError portalError,
                                                         final String handlerMethod )
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
            return errorHandlerScript.execute( portalError, handlerMethod );
        }
        finally
        {
            request.setApplicationKey( previousApp );
        }
    }

    private PortalResponse renderIdProviderError( final PortalRequest req, final WebException cause )
    {
        if ( isUnauthorizedError( cause.getStatus() ) )
        {
            final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
                functionName( "handle401" ).
                portalRequest( req ).
                build();
            try
            {
                return authControllerService.execute( executionParams );
            }
            catch ( IOException e )
            {
                LOG.error( "Exception while executing ID provider login function", e );
            }
        }
        return null;
    }

    private PortalResponse renderInternalErrorPage( final WebRequest req, final WebException cause )
    {
        final ExceptionInfo info = toErrorInfo( cause );
        logIfNeeded( info );
        return info.toResponse( req );
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

    private boolean isUnauthorizedError( final HttpStatus httpStatus )
    {
        return ( HttpStatus.UNAUTHORIZED == httpStatus || ( HttpStatus.FORBIDDEN == httpStatus ) && !isAuthenticated() );
    }

    private boolean isAuthenticated()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.isAuthenticated();
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

    @Reference
    public void setAuthControllerService( final AuthControllerService authControllerService )
    {
        this.authControllerService = authControllerService;
    }

    @Reference
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }

}
