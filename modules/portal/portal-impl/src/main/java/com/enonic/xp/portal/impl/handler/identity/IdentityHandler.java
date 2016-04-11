package com.enonic.xp.portal.impl.handler.identity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;

@Component(immediate = true, service = PortalHandler.class)
public class IdentityHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)" );

    protected SecurityService securityService;

    protected AuthDescriptorService authDescriptorService;

    protected AuthControllerScriptFactory authControllerScriptFactory;

    public IdentityHandler()
    {
        super( "identity" );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid identity url pattern" );
        }

        final UserStoreKey userStoreKey = UserStoreKey.from( matcher.group( 1 ) );
        final String idProviderFunction = matcher.group( 2 );

        final IdentityHandlerWorker worker = new IdentityHandlerWorker();
        worker.userStoreKey = userStoreKey;
        worker.idProviderFunction = idProviderFunction;
        worker.securityService = this.securityService;
        worker.authDescriptorService = this.authDescriptorService;
        worker.authControllerScriptFactory = this.authControllerScriptFactory;
        return worker;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setAuthDescriptorService( final AuthDescriptorService authDescriptorService )
    {
        this.authDescriptorService = authDescriptorService;
    }

    @Reference
    public void setAuthControllerScriptFactory( final AuthControllerScriptFactory authControllerScriptFactory )
    {
        this.authControllerScriptFactory = authControllerScriptFactory;
    }
}
