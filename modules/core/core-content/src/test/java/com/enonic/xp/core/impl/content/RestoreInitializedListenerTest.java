package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.event.Event;

class RestoreInitializedListenerTest
{
    private RestoreInitializedListener listener;

    private ComponentContext componentContext;


    @BeforeEach
    protected void setUpNode()
    {
        componentContext = Mockito.mock( ComponentContext.class );
        listener = new RestoreInitializedListener( componentContext );

    }

    @Test
    void testOnRestoreInitEvent()
    {
        listener.onEvent( Event.create( "repository.restoreInitialized" ).
            distributed( true ).
            localOrigin( true ).
            build() );

        Mockito.verify( componentContext, Mockito.times( 1 ) ).disableComponent(
            "com.enonic.xp.core.impl.content.ParentProjectSyncActivator" );
        Mockito.verify( componentContext, Mockito.times( 1 ) ).disableComponent(
            "com.enonic.xp.core.impl.content.ProjectContentEventListener" );
    }

    @Test
    void testOnRestoredEvent()
    {
        listener.onEvent( Event.create( "repository.restored" ).
            distributed( true ).
            localOrigin( true ).
            build() );

        Mockito.verify( componentContext, Mockito.times( 0 ) ).disableComponent(
            "com.enonic.xp.core.impl.content.ParentProjectSyncActivator" );
        Mockito.verify( componentContext, Mockito.times( 0 ) ).disableComponent(
            "com.enonic.xp.core.impl.content.ProjectContentEventListener" );
    }


}
