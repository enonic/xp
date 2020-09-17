package com.enonic.xp.portal.impl.exception;

import java.io.IOException;
import java.util.List;
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
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.exception.ExceptionRenderer;

import static com.enonic.xp.portal.RenderMode.EDIT;

@Component
public final class ExceptionRendererImpl
    implements ExceptionRenderer
{
    private static final Logger LOG = LoggerFactory.getLogger( ExceptionRendererImpl.class );

    private static final String DEFAULT_HANDLER = "handleError";

    private static final String STATUS_HANDLER = "handle%d";

    private static final String SITE_ERROR_SCRIPT_PATH = "site/error/error.js";

    private static final String GENERIC_ERROR_SCRIPT_PATH = "error/error.js";

    private static final List<String> SITE_ERROR_SCRIPT_PATHS = List.of( SITE_ERROR_SCRIPT_PATH, GENERIC_ERROR_SCRIPT_PATH );

    private ResourceService resourceService;

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private ContentService contentService;

    private IdProviderControllerService idProviderControllerService;

    private PostProcessor postProcessor;

    private final RunMode runMode;

    public ExceptionRendererImpl( final RunMode runMode )
    {
        this.runMode = runMode;
    }

    public ExceptionRendererImpl()
    {
        this( RunMode.get() );
    }

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
        if ( EDIT != req.getMode() )
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
        final PortalError portalError = PortalError.create().
            status( cause.getStatus() ).
            message( cause.getMessage() ).
            exception( cause ).
            request( req ).build();

        final Site site = resolveSite( req );
        if ( site != null )
        {
            final Site prevSite = req.getSite();
            req.setSite( site );
            try
            {
                for ( SiteConfig siteConfig : site.getSiteConfigs() )
                {
                    final ApplicationKey applicationKey = siteConfig.getApplicationKey();
                    for ( final String scriptPath : SITE_ERROR_SCRIPT_PATHS )
                    {
                        final PortalResponse response =
                            renderApplicationCustomError( applicationKey, scriptPath, portalError, handlerMethod );
                        if ( response != null )
                        {
                            if ( response.isPostProcess() )
                            {
                                req.setApplicationKey( applicationKey );
                            }
                            return response;
                        }
                    }
                }
            }
            finally
            {
                req.setSite( prevSite );
            }
        }
        else if ( req.getApplicationKey() != null )
        {
            final ApplicationKey applicationKey = req.getApplicationKey();
            final PortalResponse response =
                renderApplicationCustomError( applicationKey, GENERIC_ERROR_SCRIPT_PATH, portalError, handlerMethod );
            if ( response != null )
            {
                if ( response.isPostProcess() )
                {
                    req.setApplicationKey( applicationKey );
                }
                return response;
            }
        }

        return null;
    }

    private Site resolveSite( final PortalRequest req )
    {
        Site site = req.getSite();
        if ( site == null )
        {
            site = resolveSiteFromPath( req );
        }
        return site;
    }

    private Site resolveSiteFromPath( final PortalRequest req )
    {
        ContentPath contentPath = req.getContentPath();
        final Content content = getByPathAsContentAdmin( contentPath );
        if ( content != null && content.isSite() )
        {
            return (Site) content;
        }

        //Resolves closest site, starting from the top.
        Site site = null;
        ContentPath currentContentPath = ContentPath.ROOT;
        for ( int contentPathIndex = 0; contentPathIndex < contentPath.elementCount(); contentPathIndex++ )
        {
            currentContentPath = ContentPath.from( currentContentPath, contentPath.getElement( contentPathIndex ) );
            final Content childContent = getByPathAsContentAdmin( currentContentPath );
            if ( childContent == null )
            {
                break;
            }
            else if ( childContent.isSite() )
            {
                site = (Site) childContent;
            }
        }

        return site;
    }

    private Content getByPathAsContentAdmin( final ContentPath contentPath )
    {
        try
        {
            return runAsContentAdmin( () -> this.contentService.getByPath( contentPath ) );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
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

    private PortalResponse renderApplicationCustomError( final ApplicationKey appKey, final String errorScriptPath,
                                                         final PortalError portalError, final String handlerMethod )
    {
        final ResourceKey script = getScript( appKey, errorScriptPath );
        if ( script == null )
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

    private ResourceKey getScript( final ApplicationKey applicationKey, final String scriptPath )
    {
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, scriptPath );
        if ( this.resourceService.getResource( resourceKey ).exists() )
        {
            return resourceKey;
        }
        return null;
    }

    private PortalResponse renderIdProviderError( final PortalRequest req, final WebException cause )
    {
        if ( isUnauthorizedError( cause.getStatus() ) )
        {
            final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
                functionName( "handle401" ).
                portalRequest( req ).
                build();
            try
            {
                return idProviderControllerService.execute( executionParams );
            }
            catch ( IOException e )
            {
                LOG.error( "Exception while executing application login function", e );
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
            runMode( runMode ).
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
        return HttpStatus.UNAUTHORIZED == httpStatus || ( HttpStatus.FORBIDDEN == httpStatus && !isAuthenticated() );
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
    public void setIdProviderControllerService( final IdProviderControllerService idProviderControllerService )
    {
        this.idProviderControllerService = idProviderControllerService;
    }

    @Reference
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }

}
