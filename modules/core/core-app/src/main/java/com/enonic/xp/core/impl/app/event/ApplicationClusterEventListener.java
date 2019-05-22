package com.enonic.xp.core.impl.app.event;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
    private ApplicationService applicationService;

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
        final String type = event.getType();

        if ( type.equals( ApplicationClusterEvents.EVENT_TYPE ) )
        {
            final String eventSubType = event.getValueAs( String.class, ApplicationClusterEvents.EVENT_TYPE_KEY ).get();

            switch ( eventSubType )
            {
                case ApplicationClusterEvents.INSTALLED:
                    handleInstalledEvent( event );
                    break;
                case ApplicationClusterEvents.STATE_CHANGE:
                    handleStateChangedEvent( event );
                    break;
                case ApplicationClusterEvents.UNINSTALLED:
                    handleUninstalledEvent( event );
                    break;
            }
        }
    }

    private void handleInstalledEvent( final Event event )
    {
        final Optional<String> valueAs = event.getValueAs( String.class, ApplicationClusterEvents.NODE_ID_PARAM );

        if ( valueAs.isPresent() )
        {
            ApplicationHelper.runAsAdmin( () -> this.applicationService.installStoredApplication( NodeId.from( valueAs.get() ), false ) );
        }
    }

    private void handleUninstalledEvent( final Event event )
    {
        final Optional<String> appKey = event.getValueAs( String.class, ApplicationClusterEvents.APPLICATION_KEY_PARAM );

        if ( appKey.isPresent() )
        {
            final ApplicationKey applicationKey = ApplicationKey.from( appKey.get() );
            if ( !( this.applicationService.isLocalApplication( applicationKey ) ) )
            {
                ApplicationHelper.runAsAdmin(
                    () -> this.applicationService.uninstallApplication( ApplicationKey.from( appKey.get() ), false ) );
            }
        }
    }

    private void handleStateChangedEvent( final Event event )
    {
        final Optional<String> appKey = event.getValueAs( String.class, ApplicationClusterEvents.APPLICATION_KEY_PARAM );
        final Optional<Boolean> started = event.getValueAs( Boolean.class, ApplicationClusterEvents.STARTED_PARAM );

        if ( appKey.isPresent() && started.isPresent() )
        {
            final ApplicationKey applicationKey = ApplicationKey.from( appKey.get() );
            if ( !( this.applicationService.isLocalApplication( applicationKey ) ) )
            {
                if ( started.get() )
                {

                    ApplicationHelper.runAsAdmin( () -> this.applicationService.startApplication( applicationKey, false ) );
                }
                else
                {
                    ApplicationHelper.runAsAdmin( () -> this.applicationService.stopApplication( applicationKey, false ) );
                }
            }
        }
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
