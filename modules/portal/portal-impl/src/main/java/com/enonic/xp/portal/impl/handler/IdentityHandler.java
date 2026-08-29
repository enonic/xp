package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.trace.Traced;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component(service = IdentityHandler.class, configurationPid = "com.enonic.xp.portal")
public class IdentityHandler
{
    private static final Pattern PATTERN = Pattern.compile( "^(?<idp>[^/]+)(?:/(?<fun>login|logout))?" );

    private final IdProviderControllerService idProviderControllerService;

    private final RedirectChecksumService redirectChecksumService;

    @Activate
    public IdentityHandler( @Reference final IdProviderControllerService idProviderControllerService,
                            @Reference final RedirectChecksumService redirectChecksumService )
    {
        this.idProviderControllerService = idProviderControllerService;
        this.redirectChecksumService = redirectChecksumService;
    }

    public PortalResponse handle( final WebRequest webRequest )
        throws IOException
    {
        final String restPath = HandlerHelper.findEndpointPath( webRequest, "idprovider" );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw WebException.notFound( "Not a valid idprovider url pattern" );
        }

        if ( !HttpMethod.isStandard( webRequest.getMethod() ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( HttpMethod.standard() );
        }

        final IdProviderKey idProviderKey = IdProviderKey.from( matcher.group( "idp" ) );

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( webRequest.getRawRequest() );

        if ( !virtualHost.getIdProviderKeys().contains( idProviderKey ) )
        {
            throw WebException.forbidden( String.format( "'%s' id provider is forbidden", idProviderKey ) );
        }

        final String target = virtualHost.getTarget();
        if ( !webRequest.getRawPath().startsWith( target + ( target.endsWith( "/" ) ? "_/idprovider/" : "/_/idprovider/" ) ) )
        {
            throw WebException.notFound( "Not a valid idprovider url pattern" );
        }

        String idProviderFunction = matcher.group( "fun" );

        // The XP-managed functions require their flow on this vhost. The custom endpoints (pages,
        // callbacks, token endpoints, static assets) are always dispatched: the id provider app
        // controls them itself, guided by the vhost's flow list on the request.
        final String requiredFlow = idProviderFunction == null ? null
            : "logout".equals( idProviderFunction ) ? IdProviderFlow.LOGOUT : IdProviderFlow.LOGIN;
        if ( requiredFlow != null && !virtualHost.getIdProviderFlows( idProviderKey ).contains( requiredFlow ) )
        {
            throw WebException.forbidden( String.format( "'%s' flow is disabled for '%s' id provider", requiredFlow, idProviderKey ) );
        }

        final PortalRequest portalRequest = createPortalRequest( webRequest, idProviderKey, idProviderFunction );

        return doHandle( idProviderKey, idProviderFunction, portalRequest );
    }

    @Traced("portalRequest")
    private PortalResponse doHandle( final IdProviderKey idProviderKey, final String idProviderFunction, final PortalRequest portalRequest )
        throws IOException
    {
        Tracer.withCurrent( trace -> {
            trace.attribute( "path", portalRequest.getPath() );
            trace.attribute( "method", portalRequest.getMethod().toString() );
            trace.attribute( "host", portalRequest.getHost() );
        } );

        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
            .idProviderKey( idProviderKey )
            .functionName( idProviderFunction )
            .portalRequest( portalRequest )
            .build();

        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );

        if ( portalResponse == null )
        {
            throw WebException.notFound(
                String.format( "ID Provider function [%s] not found for id provider [%s]", idProviderFunction, idProviderKey ) );
        }

        Tracer.withCurrent( trace -> HandlerHelper.addTraceInfo( trace, portalResponse ) );
        return portalResponse;
    }

    private PortalRequest createPortalRequest( final WebRequest webRequest, final IdProviderKey idProviderName,
                                               final String idProviderFunction )
    {
        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        portalRequest.setContextPath( portalRequest.getBasePath() + "/_/idprovider/" + idProviderName );

        if ( idProviderFunction != null )
        {
            checkTicket( portalRequest );
        }

        return portalRequest;
    }

    private void checkTicket( final PortalRequest req )
    {
        final String redirect = HandlerHelper.getParameter( req, "redirect" );
        if ( redirect != null )
        {
            final String ticket = HandlerHelper.removeParameter( req, "_ticket" );
            if ( ticket == null )
            {
                throw WebException.badRequest( "Missing ticket parameter" );
            }

            req.setValidTicket( redirectChecksumService.verifyChecksum( redirect, ticket ) );
        }
    }
}
