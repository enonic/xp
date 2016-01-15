package com.enonic.xp.core.impl.app;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public class ApplicationEventListener
    implements EventListener
{
    private ApplicationService applicationService;

    private static final PrincipalKey APPLICATION_INSTALL_USER = PrincipalKey.ofUser( UserStoreKey.system(), "su" );

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null )
        {
            if ( event.isLocalOrigin() )
            {
                return;
            }

            doHandleEvent( event );
        }
    }

    private void doHandleEvent( final Event event )
    {
        System.out.println( "Received event" + event );

        final String type = event.getType();

        switch ( type )
        {
            case ApplicationEvents.APPLICATION_INSTALLED_EVENT:
                runAsAdmin( () -> handleInstalledEvent( event ), APPLICATION_INSTALL_USER );
                break;
        }
    }

    private void handleInstalledEvent( final Event event )
    {
        final Optional<String> valueAs = event.getValueAs( String.class, ApplicationEvents.NODE_ID_PARAM );

        if ( valueAs.isPresent() )
        {
            this.applicationService.installApplication( NodeId.from( valueAs.get() ) );
        }
    }

    private void runAsAdmin( Runnable runnable, final PrincipalKey user )
    {
        final User admin = User.create().key( user ).login( "su" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).authInfo( authInfo ).build().runWith( runnable );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
