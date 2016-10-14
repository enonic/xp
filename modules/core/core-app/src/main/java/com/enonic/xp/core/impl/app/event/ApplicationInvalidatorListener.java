package com.enonic.xp.core.impl.app.event;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class ApplicationInvalidatorListener
    implements EventListener
{
    private ApplicationService service;

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null && ApplicationEvents.EVENT_TYPE.equals( event.getType() ) &&
            !ApplicationEvents.INSTALLATION_PROGRESS.equals( event.getData().get( ApplicationEvents.EVENT_TYPE_KEY ) ) )
        {
            onApplicationEvent( event );
        }
    }

    private void onApplicationEvent( final Event event )
    {
        final String applicationKey = (String) event.getValue( ApplicationEvents.APPLICATION_KEY_KEY ).
            get();
        this.service.invalidate( ApplicationKey.from( applicationKey ) );
    }

    @Reference
    public void setApplicationService( final ApplicationService service )
    {
        this.service = service;
    }
}
