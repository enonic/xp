package com.enonic.xp.core.impl.content;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public class RestoreInitializedListener
    implements EventListener
{
    private final ComponentContext componentContext;

    @Activate
    public RestoreInitializedListener( final ComponentContext componentContext )
    {
        this.componentContext = componentContext;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event.isType( "repository.restoreInitialized" ) )
        {
            componentContext.disableComponent( "com.enonic.xp.core.impl.content.ParentProjectSyncActivator" );
            componentContext.disableComponent( "com.enonic.xp.core.impl.content.ProjectContentEventListener" );
        }
    }

}
