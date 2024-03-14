package com.enonic.xp.portal.impl.handler.api;

import java.io.IOException;
import java.util.Collection;
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
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component(immediate = true, service = WebHandler.class)
public class ApiIdentityHandler
    extends BaseWebHandler
{
    private static final Pattern PATTERN =
        Pattern.compile( "^(/admin)?/api/idprovider/(?<idProviderName>[^/]+)(/(?<functionName>(login|logout)))?(?<restPath>.+)?$" );

    private final IdProviderControllerService idProviderControllerService;

    private final RedirectChecksumService redirectChecksumService;

    @Activate
    public ApiIdentityHandler( @Reference final IdProviderControllerService idProviderControllerService,
                               @Reference final RedirectChecksumService redirectChecksumService )
    {
        this.idProviderControllerService = idProviderControllerService;
        this.redirectChecksumService = redirectChecksumService;
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( "/api/idprovider" ) || webRequest.getRawPath().startsWith( "/admin/api/idprovider" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        final Matcher matcher = PATTERN.matcher( webRequest.getRawPath() );
        if ( !matcher.matches() )
        {
            return PortalResponse.create( webResponse ).status( HttpStatus.NOT_FOUND ).build();
        }

        final IdProviderKey idProviderKey = IdProviderKey.from( matcher.group( "idProviderName" ) );

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( webRequest.getRawRequest() );

        if ( !virtualHost.getIdProviderKeys().contains( idProviderKey ) )
        {
            throw WebException.forbidden( String.format( "'%s' id provider is forbidden", idProviderKey ) );
        }

        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        final String idProviderFunction = resolveIdProviderFunction( matcher, portalRequest );

        final Trace trace = Tracer.newTrace( "ApiIdentityHandler" );
        if ( trace == null )
        {
            return doExecute( idProviderKey, idProviderFunction, portalRequest );
        }
        else
        {
            return executeWithTrace( trace, idProviderKey, idProviderFunction, portalRequest );
        }
    }

    private PortalResponse executeWithTrace( final Trace trace, final IdProviderKey idProviderKey, final String idProviderFunction,
                                             final PortalRequest portalRequest )
        throws Exception
    {
        trace.put( "method", portalRequest.getMethod().toString() );
        trace.put( "path", portalRequest.getPath() );
        trace.put( "host", portalRequest.getHost() );
        trace.put( "httpRequest", portalRequest );
        trace.put( "context", ContextAccessor.current() );

        return Tracer.traceEx( trace, () -> {
            final PortalResponse response = doExecute( idProviderKey, idProviderFunction, portalRequest );
            addTraceInfo( trace, response );
            return response;
        } );
    }

    private String resolveIdProviderFunction( final Matcher matcher, final PortalRequest portalRequest )
    {
        String idProviderFunction = matcher.group( "functionName" );

        if ( idProviderFunction != null )
        {
            checkTicket( portalRequest );
        }

        if ( idProviderFunction == null )
        {
            idProviderFunction = portalRequest.getMethod().toString().toLowerCase();
        }

        return idProviderFunction;
    }

    private PortalResponse doExecute( final IdProviderKey idProviderKey, final String idProviderFunction,
                                      final PortalRequest portalRequest )
        throws IOException
    {
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
        else
        {
            return portalResponse;
        }
    }

    private void checkTicket( final PortalRequest req )
    {
        final String redirect = getParameter( req, "redirect" );
        if ( redirect != null )
        {
            final String ticket = removeParameter( req, "_ticket" );
            if ( ticket == null )
            {
                throw WebException.badRequest( "Missing ticket parameter" );
            }

            req.setValidTicket( redirectChecksumService.verifyChecksum( redirect, ticket ) );
        }
    }

    private String getParameter( final PortalRequest req, final String name )
    {
        final Collection<String> values = req.getParams().get( name );
        return values.isEmpty() ? null : values.iterator().next();
    }

    private String removeParameter( final PortalRequest req, final String name )
    {
        final Collection<String> values = req.getParams().removeAll( name );
        return values.isEmpty() ? null : values.iterator().next();
    }
}
