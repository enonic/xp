package com.enonic.xp.script.impl.event;

import java.util.concurrent.RejectedExecutionException;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.impl.async.ScriptAsyncService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScriptEventManagerImplTest
{
    private ScriptEventManagerImpl manager;

    @BeforeEach
    void setup()
    {
        final ScriptAsyncService scriptAsyncService = mock( ScriptAsyncService.class );
        when( scriptAsyncService.getAsyncExecutor( any() ) ).thenReturn( Runnable::run );

        this.manager = new ScriptEventManagerImpl( scriptAsyncService );
    }

    private ScriptEventListener newListener( final String app )
    {
        final ScriptEventListener listener = mock( ScriptEventListener.class );
        when( listener.getApplication() ).thenReturn( ApplicationKey.from( app ) );
        return listener;
    }

    @Test
    void testIterable()
    {
        final ScriptEventListener listener = newListener( "foo.bar" );
        this.manager.add( listener );

        assertEquals( 1, StreamSupport.stream( manager.spliterator(), false ).count() );
        assertSame( listener, this.manager.iterator().next() );
    }

    @Test
    void testInvalidate()
    {
        final ScriptEventListener listener1 = newListener( "foo.bar" );
        final ScriptEventListener listener2 = newListener( "foo.other" );
        this.manager.add( listener1 );
        this.manager.add( listener2 );

        assertEquals( 2, StreamSupport.stream( manager.spliterator(), false ).count() );

        Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        this.manager.invalidate( ApplicationKey.from( "foo.bar" ), ApplicationInvalidationLevel.FULL );
        assertEquals( 1, StreamSupport.stream( manager.spliterator(), false ).count() );
    }

    @Test
    void testOnEvent()
    {
        final ScriptEventListener listener1 = newListener( "foo.bar" );
        final ScriptEventListener listener2 = newListener( "foo.other" );
        this.manager.add( listener1 );
        this.manager.add( listener2 );

        final Event event = Event.create( "myEvent" ).build();
        this.manager.onEvent( event );

        verify( listener1, Mockito.times( 1 ) ).onEvent( event );
        verify( listener2, Mockito.times( 1 ) ).onEvent( event );
    }

    @Test
    void testRejectedExecution()
    {
        final ScriptAsyncService scriptAsyncService = mock( ScriptAsyncService.class );

        when( scriptAsyncService.getAsyncExecutor( any() ) ).thenReturn( c -> {
            throw new RejectedExecutionException();
        } );

        this.manager = new ScriptEventManagerImpl( scriptAsyncService );

        final ScriptEventListener listener1 = newListener( "foo.bar" );
        this.manager.add( listener1 );

        assertEquals( 1, StreamSupport.stream( manager.spliterator(), false ).count() );

        final Event event = Event.create( "myEvent" ).build();
        this.manager.onEvent( event );

        assertEquals( 0, StreamSupport.stream( manager.spliterator(), false ).count() );

        verify( listener1, Mockito.never() ).onEvent( event );
    }


    @Test
    void testDeactivate()
    {
        final ScriptEventListener listener1 = newListener( "foo.bar" );
        this.manager.add( listener1 );

        final Event event = Event.create( "myEvent" ).build();
        this.manager.onEvent( event );

        this.manager.deactivate();

        this.manager.onEvent( event );

        verify( listener1, Mockito.times( 1 ) ).onEvent( event );
    }
}
