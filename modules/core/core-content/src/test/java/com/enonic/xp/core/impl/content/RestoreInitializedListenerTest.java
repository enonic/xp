package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.event.Event;

public class RestoreInitializedListenerTest
{
    private RestoreInitializedListener listener;

    private ComponentContext componentContext;


    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        componentContext = Mockito.mock( ComponentContext.class );
        listener = new RestoreInitializedListener( componentContext );

    }

    @Test
    public void testOnRestoreInitEvent()
        throws InterruptedException
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
    public void testOnRestoredEvent()
        throws InterruptedException
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
