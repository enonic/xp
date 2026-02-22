package com.enonic.xp.portal.impl.exception;

import java.io.IOException;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static com.enonic.xp.portal.RenderMode.EDIT;
import static com.google.common.base.Strings.nullToEmpty;

@Component
public final class ExceptionRendererImpl
    implements ExceptionRenderer
{
    private static final Logger LOG = LoggerFactory.getLogger( ExceptionRendererImpl.class );

    private static final String DEFAULT_HANDLER = "handleError";

    private static final String CMS_ERROR_SCRIPT_PATH = "cms/error/error.js";

    private static final String GENERIC_ERROR_SCRIPT_PATH = "error/error.js";

    private static final List<String> SITE_ERROR_SCRIPT_PATHS = List.of( CMS_ERROR_SCRIPT_PATH, GENERIC_ERROR_SCRIPT_PATH );

    private final ResourceService resourceService;

    private final ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private final IdProviderControllerService idProviderControllerService;

    private final PostProcessor postProcessor;

    private final ExceptionMapper exceptionMapper;

    @Activate
    public ExceptionRendererImpl( @Reference final ResourceService resourceService,
                                  @Reference final ErrorHandlerScriptFactory errorHandlerScriptFactory,
                                  @Reference final IdProviderControllerService idProviderControllerService,
                                  @Reference final PostProcessor postProcessor, @Reference final ExceptionMapper exceptionMapper )
    {
        this.resourceService = resourceService;
        this.errorHandlerScriptFactory = errorHandlerScriptFactory;
        this.idProviderControllerService = idProviderControllerService;
        this.postProcessor = postProcessor;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public PortalResponse render( final WebRequest webRequest, final Exception cause )
    {
        PortalResponse response = doRender( webRequest, exceptionMapper.map( cause ) );
        webRequest.getRawRequest().setAttribute( "error.handled", Boolean.TRUE );
        return response;
    }

    @Override
    public WebResponse maybeThrow( WebRequest webRequest, WebResponse webResponse )
    {
        if ( !Boolean.TRUE.equals( webRequest.getRawRequest().getAttribute( "error.handled" ) ) )
        {
            this.exceptionMapper.throwIfNeeded( webResponse );
        }
        return webResponse;
    }

    public PortalResponse doRender( final WebRequest webRequest, final WebException cause )
    {
        final ExceptionInfo errorInfo = ExceptionInfo.create( cause.getStatus() ).withDebugInfo( RunMode.isDev() ).cause( cause );

        logIfNeeded( errorInfo );

        if ( webRequest instanceof PortalRequest portalRequest )
        {
            final PortalResponse statusCustomError = renderCustomError( portalRequest, cause, false );
            if ( statusCustomError != null )
            {
                return statusCustomError;
            }

            final PortalResponse idProviderError = renderIdProviderError( portalRequest, cause );
            if ( idProviderError != null )
            {
                return idProviderError;
            }

            final PortalResponse defaultCustomError = renderCustomError( portalRequest, cause, true );
            if ( defaultCustomError != null )
            {
                return defaultCustomError;
            }

            if ( PortalRequestHelper.isSiteBase( portalRequest ) && ContentConstants.BRANCH_MASTER.equals( portalRequest.getBranch() ) &&
                HttpStatus.NOT_FOUND.equals( cause.getStatus() ) )
            {
                errorInfo.tip( "Tip: Did you remember to publish the site?" );
            }
        }

        return toResponse( errorInfo, webRequest );
    }

    private PortalResponse renderCustomError( final PortalRequest req, final WebException cause, boolean defaultHandler )
    {
        if ( EDIT != req.getMode() )
        {
            try
            {
                PortalResponse portalResponse =
                    doRenderCustomError( req, cause, defaultHandler ? DEFAULT_HANDLER : "handle" + cause.getStatus().value() );
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
        final PortalError portalError =
            PortalError.create().status( cause.getStatus() ).message( cause.getMessage() ).exception( cause ).request( req ).build();

        if ( PortalRequestHelper.isSiteBase( req ) )
        {
            final SiteConfigs siteConfigs = PortalRequestHelper.getSiteConfigs( req );

            for ( SiteConfig siteConfig : siteConfigs )
            {
                final ApplicationKey applicationKey = siteConfig.getApplicationKey();
                for ( final String scriptPath : SITE_ERROR_SCRIPT_PATHS )
                {
                    final PortalResponse response = renderApplicationCustomError( applicationKey, scriptPath, portalError, handlerMethod );
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
        return errorHandlerScript.execute( portalError, handlerMethod );
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
            final IdProviderControllerExecutionParams executionParams =
                IdProviderControllerExecutionParams.create().functionName( "handle401" ).portalRequest( req ).build();
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

    private void logIfNeeded( final ExceptionInfo info )
    {
        if ( info.shouldLogAsError() )
        {
            LOG.error( info.getMessage(), info.getCause() );
        }
        else if ( LOG.isDebugEnabled() )
        {
            LOG.debug( info.getMessage(), info.getCause() );
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

    private PortalResponse toResponse( final ExceptionInfo info, final WebRequest req )
    {
        final boolean isHtml = nullToEmpty( req.getHeaders().get( HttpHeaders.ACCEPT ) ).contains( "text/html" );
        return isHtml ? toHtmlResponse( info, req ) : toJsonResponse( info );
    }

    private PortalResponse toJsonResponse( final ExceptionInfo info )
    {
        final ObjectNode node =
            JsonNodeFactory.instance.objectNode().put( "status", info.status.value() ).put( "message", info.getDescription() );

        return PortalResponse.create().status( info.getStatus() ).body( node ).contentType( MediaType.JSON_UTF_8 ).build();
    }

    private PortalResponse toHtmlResponse( ExceptionInfo info, final WebRequest req )
    {
        final ErrorPageBuilder builder;
        if ( info.withDebugInfo )
        {
            builder = new ErrorPageRichBuilder().cause( info.cause )
                .description( info.getDescription() )
                .resourceService( this.resourceService )
                .status( info.status.value() )
                .title( info.status.getReasonPhrase() );
        }
        else
        {
            ErrorPageSimpleBuilder errorBuilder =
                new ErrorPageSimpleBuilder().status( info.status.value() ).tip( info.tip ).title( info.status.getReasonPhrase() );
            if ( info.status == HttpStatus.FORBIDDEN && ContextAccessor.current().getAuthInfo().isAuthenticated() )
            {
                errorBuilder.logoutUrl( ServletRequestUrlHelper.createUri( req.getRawRequest(), "/_/idprovider/" +
                    ContextAccessor.current().getAuthInfo().getUser().getKey().getIdProviderKey() + "/logout" ) );
            }
            builder = errorBuilder;
        }

        final String html = builder.build();
        return PortalResponse.create().status( info.status ).body( html ).contentType( MediaType.HTML_UTF_8 ).build();
    }
}
