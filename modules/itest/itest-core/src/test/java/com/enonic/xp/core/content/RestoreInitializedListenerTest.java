package com.enonic.xp.core.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.core.impl.content.RestoreInitializedListener;
import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.RepositoryEvents;

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
        listener.onEvent( Event.create( RepositoryEvents.RESTORE_INITIALIZED_EVENT_TYPE ).
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
        listener.onEvent( Event.create( RepositoryEvents.RESTORED_EVENT_TYPE ).
            distributed( true ).
            localOrigin( true ).
            build() );

        Mockito.verify( componentContext, Mockito.times( 0 ) ).disableComponent(
            "com.enonic.xp.core.impl.content.ParentProjectSyncActivator" );
        Mockito.verify( componentContext, Mockito.times( 0 ) ).disableComponent(
            "com.enonic.xp.core.impl.content.ProjectContentEventListener" );
    }


}