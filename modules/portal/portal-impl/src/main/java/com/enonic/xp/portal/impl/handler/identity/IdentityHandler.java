package com.enonic.xp.portal.impl.handler.identity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
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
    private final static Pattern PATTERN = Pattern.compile( "^([^/^?]+)(?:/(logout))?" );

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