package com.enonic.xp.core.impl.app;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.node.NodeId;

@Component(immediate = true)
public class ApplicationEventListener
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

        switch ( type )
        {
            case ApplicationEvents.APPLICATION_INSTALLED_EVENT:
                handleInstalledEvent( event );
                break;
            case ApplicationEvents.APPLICATION_STATE_CHANGE_EVENT:
                handleStateChangedEvent( event );
                break;
            case ApplicationEvents.APPLICATION_UNINSTALLED_EVENT:
                handleUninstalledEvent( event );
                break;
        }
    }

    private void handleInstalledEvent( final Event event )
    {
        final Optional<String> valueAs = event.getValueAs( String.class, ApplicationEvents.NODE_ID_PARAM );

        if ( valueAs.isPresent() )
        {
            ApplicationHelper.runAsAdmin( () -> this.applicationService.installApplication( NodeId.from( valueAs.get() ) ) );
        }
    }

    private void handleUninstalledEvent( final Event event )
    {
        final Optional<String> appKey = event.getValueAs( String.class, ApplicationEvents.APPLICATION_KEY_PARAM );

        if ( appKey.isPresent() )
        {
            ApplicationHelper.runAsAdmin(
                () -> this.applicationService.uninstallApplication( ApplicationKey.from( appKey.get() ), true, false ) );
        }
    }

    private void handleStateChangedEvent( final Event event )
    {
        final Optional<String> appKey = event.getValueAs( String.class, ApplicationEvents.APPLICATION_KEY_PARAM );
        final Optional<Boolean> started = event.getValueAs( Boolean.class, ApplicationEvents.STARTED_PARAM );

        if ( appKey.isPresent() && started.isPresent() )
        {
            if ( started.get() )
            {
                ApplicationHelper.runAsAdmin(
                    () -> this.applicationService.startApplication( ApplicationKey.from( appKey.get() ), false ) );
            }
            else
            {
                ApplicationHelper.runAsAdmin( () -> this.applicationService.stopApplication( ApplicationKey.from( appKey.get() ), false ) );
            }
        }
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
