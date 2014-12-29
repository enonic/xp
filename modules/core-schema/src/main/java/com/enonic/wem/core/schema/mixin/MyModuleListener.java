package com.enonic.wem.core.schema.mixin;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventListener;
import com.enonic.wem.api.module.ModuleUpdatedEvent;

@Component(immediate = true)
public final class MyModuleListener
    implements EventListener
{
    @Override
    public void onEvent( final Event event )
    {
        if ( event instanceof ModuleUpdatedEvent )
        {
            onEvent( (ModuleUpdatedEvent) event );
        }
    }

    private void onEvent( final ModuleUpdatedEvent event )
    {
        System.out.println( event );
    }
}
