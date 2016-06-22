package com.enonic.xp.portal.impl.handler.identity;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.security.UserStoreKey;

@Component(immediate = true, service = PortalHandler.class)
public class IdentityHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "^([^/^?]+)(?:/(login|logout))?" );

    private ContentService contentService;

    protected AuthControllerService authControllerService;

    public IdentityHandler()
    {
        super( "idprovider" );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid idprovider url pattern" );
        }

        final UserStoreKey userStoreKey = UserStoreKey.from( matcher.group( 1 ) );
        String idProviderFunction = matcher.group( 2 );

        if ( idProviderFunction != null )
        {
            checkTicket( req );
        }

        if ( idProviderFunction == null )
        {
            idProviderFunction = req.getMethod().
                toString().
                toLowerCase();
        }

        final IdentityHandlerWorker worker = new IdentityHandlerWorker();
        worker.userStoreKey = userStoreKey;
        worker.idProviderFunction = idProviderFunction;
        worker.setContentService( this.contentService );
        worker.authControllerService = this.authControllerService;
        return worker;
    }

    private void checkTicket( final PortalRequest req )
    {
        if ( getParameter( req, "redirect" ) != null )
        {
            final String ticket = removeParameter( req, "_ticket" );
            if ( ticket == null )
            {
                throw badRequest( "Missing ticket parameter" );
            }

            final String jSessionId = getJSessionId();
            final String expectedTicket = generateTicket( jSessionId );
            if ( expectedTicket.equals( ticket ) )
            {
                req.setValidTicket( Boolean.TRUE );
            }
            else
            {
                req.setValidTicket( Boolean.FALSE );
            }
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

    private String getJSessionId()
    {
        return ContextAccessor.current().
            getLocalScope().
            getSession().
            getKey().
            toString();
    }

    private String generateTicket( final String jSessionId )
    {
        return Hashing.sha1().
            newHasher().
            putString( jSessionId, Charsets.UTF_8 ).
            hash().
            toString();
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
}