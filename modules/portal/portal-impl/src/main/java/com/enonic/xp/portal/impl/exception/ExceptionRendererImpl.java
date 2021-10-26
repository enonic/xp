package com.enonic.xp.portal.impl.exception;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentConstants;
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

    private static final String SITE_ERROR_SCRIPT_PATH = "site/error/error.js";

    private static final String GENERIC_ERROR_SCRIPT_PATH = "error/error.js";

    private static final List<String> SITE_ERROR_SCRIPT_PATHS = List.of( SITE_ERROR_SCRIPT_PATH, GENERIC_ERROR_SCRIPT_PATH );

    private final ResourceService resourceService;

    private final ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private final ContentService contentService;

    private final IdProviderControllerService idProviderControllerService;

    private final PostProcessor postProcessor;

    private final RunMode runMode;


    ExceptionRendererImpl( final ResourceService resourceService, final ErrorHandlerScriptFactory errorHandlerScriptFactory,
                           final ContentService contentService, final IdProviderControllerService idProviderControllerService,
                           final PostProcessor postProcessor, final RunMode runMode )
    {
        this.resourceService = resourceService;
        this.errorHandlerScriptFactory = errorHandlerScriptFactory;
        this.contentService = contentService;
        this.idProviderControllerService = idProviderControllerService;
        this.postProcessor = postProcessor;
        this.runMode = runMode;
    }

    @Activate
    public ExceptionRendererImpl( @Reference final ResourceService resourceService,
                                  @Reference final ErrorHandlerScriptFactory errorHandlerScriptFactory,
                                  @Reference final ContentService contentService,
                                  @Reference final IdProviderControllerService idProviderControllerService,
                                  @Reference final PostProcessor postProcessor )
    {
        this( resourceService, errorHandlerScriptFactory, contentService, idProviderControllerService, postProcessor, RunMode.get() );
    }

    @Override
    public PortalResponse render( final WebRequest webRequest, final WebException cause )
    {
        String tip = null;
        final ExceptionInfo info = toErrorInfo( cause );
        logIfNeeded( info );

        if ( webRequest instanceof PortalRequest )
        {
            PortalRequest portalRequest = (PortalRequest) webRequest;
            final HttpStatus httpStatus = cause.getStatus();
            if ( httpStatus != null )
            {
                final String handlerMethod = "handle" + httpStatus.value();
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

            if ( "/site".equals( portalRequest.getBaseUri() ) && ContentConstants.BRANCH_MASTER.equals( portalRequest.getBranch() ) &&
                HttpStatus.NOT_FOUND.equals( cause.getStatus() ) )
            {
                tip = "Tip: Did you remember to publish the site?";
            }
        }

        return renderInternalErrorPage( webRequest, tip, cause );
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

        final Site siteInRequest = req.getSite();
        final Site site = siteInRequest != null
            ? siteInRequest
            : callAsContentAdmin( () -> this.contentService.findNearestSiteByPath( req.getContentPath() ) );
        if ( site != null )
        {
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
                req.setSite( siteInRequest );
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

    private PortalResponse renderInternalErrorPage( final WebRequest req, String tip, final WebException cause )
    {
        final ExceptionInfo info = ExceptionInfo.create( cause.getStatus() ).
            runMode( runMode ).
            cause( cause ).
            tip( tip );

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

    private <T> T callAsContentAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .build()
            .callWith( callable );
    }
}
