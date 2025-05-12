package com.enonic.xp.core.impl.app.event;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.node.NodeId;

@Component(immediate = true)
public class ApplicationClusterEventListener
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationClusterEventListener.class );

    private final ApplicationService applicationService;

    @Activate
    public ApplicationClusterEventListener( @Reference final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( !event.isLocalOrigin() )
        {
            doHandleEvent( event );
        }
    }

    private void doHandleEvent( final Event event )
    {
        if ( ApplicationClusterEvents.EVENT_TYPE.equals( event.getType() ) )
        {
            event.getValueAs( String.class, ApplicationClusterEvents.EVENT_TYPE_KEY ).
                ifPresent( eventSubType -> {
                    switch ( eventSubType )
                    {
                        case ApplicationClusterEvents.INSTALLED:
                            handleInstalledEvent( event );
                            break;
                        case ApplicationClusterEvents.UNINSTALL:
                            handleUninstallEvent( event );
                            break;
                        case ApplicationClusterEvents.START:
                            handleStartEvent( event );
                            break;
                        case ApplicationClusterEvents.STOP:
                            handleStopEvent( event );
                            break;
                        default:
                            LOG.debug( "Ignoring {} {}", ApplicationClusterEvents.EVENT_TYPE, eventSubType );
                            break;
                    }
                } );
        }
    }

    private void handleInstalledEvent( final Event event )
    {
        event.getValueAs( String.class, ApplicationClusterEvents.NODE_ID_PARAM ).
            map( NodeId::from ).
            ifPresent( nodeId -> {
                final ApplicationInstallationParams params = ApplicationInstallationParams.create().
                    start( false ).
                    build();
                ApplicationHelper.runAsAdmin( () -> this.applicationService.installStoredApplication( nodeId, params ) );
            } );
    }

    private void handleUninstallEvent( final Event event )
    {
        handleEvent( event, applicationKey -> this.applicationService.uninstallApplication( applicationKey, false ) );
    }

    private void handleStartEvent( final Event event )
    {
        handleEvent( event, applicationKey -> this.applicationService.startApplication( applicationKey, false ) );
    }

    private void handleStopEvent( final Event event )
    {
        handleEvent( event, applicationKey -> this.applicationService.stopApplication( applicationKey, false ) );
    }

    private void handleEvent( final Event event, final Consumer<ApplicationKey> callback )
    {
        event.getValueAs( String.class, ApplicationClusterEvents.APPLICATION_KEY_PARAM ).
            map( ApplicationKey::from ).
            filter( Predicate.not( this.applicationService::isLocalApplication ) ).
            ifPresent( applicationKey -> ApplicationHelper.runAsAdmin( () -> callback.accept( applicationKey ) ) );
    }
}
