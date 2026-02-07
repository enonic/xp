package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component(service = IdentityHandler.class, configurationPid = "com.enonic.xp.portal")
public class IdentityHandler
{
    private static final Pattern PATTERN = Pattern.compile( "^(?<idp>[^/?]+)(?:/(?<fun>login|logout))?" );

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
        final String restPath = HandlerHelper.findRestPath( webRequest, "idprovider" );
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

        final PortalRequest portalRequest = createPortalRequest( webRequest, idProviderKey, idProviderFunction );

        final Trace trace = Tracer.newTrace( "portalRequest" );

        if ( trace == null )
        {
            return doHandle( idProviderKey, idProviderFunction, portalRequest );
        }

        trace.put( "path", webRequest.getPath() );
        trace.put( "method", webRequest.getMethod().toString() );
        trace.put( "host", webRequest.getHost() );
        trace.put( "httpRequest", webRequest );
        trace.put( "context", ContextAccessor.current() );

        return Tracer.traceIO( trace, () -> {
            final PortalResponse portalResponse = doHandle( idProviderKey, idProviderFunction, portalRequest );
            HandlerHelper.addTraceInfo( trace, portalResponse );
            return portalResponse;
        } );
    }

    private PortalResponse doHandle( final IdProviderKey idProviderKey, final String idProviderFunction, final PortalRequest portalRequest )
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
            .idProviderKey( idProviderKey )
            .functionName( Objects.requireNonNullElse( idProviderFunction, portalRequest.getMethod().toString().toLowerCase() ) )
            .portalRequest( portalRequest )
            .build();

        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );

        if ( portalResponse == null )
        {
            throw WebException.notFound(
                String.format( "ID Provider function [%s] not found for id provider [%s]", idProviderFunction, idProviderKey ) );
        }

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
