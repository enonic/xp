package com.enonic.xp.script.impl.event;

import java.util.concurrent.RejectedExecutionException;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.impl.async.ScriptAsyncService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScriptEventManagerImplTest
{
    private ScriptEventManagerImpl manager;

    private BundleContext newBundleContext()
    {
        try
        {
            // let the ServiceTracker construct and open against a mock registry (no initial services)
            final BundleContext bundleContext = mock( BundleContext.class );
            lenient().when( bundleContext.createFilter( anyString() ) )
                .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
            lenient().when( bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );
            return bundleContext;
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( e );
        }
    }

    @BeforeEach
    void setup()
    {
        final ScriptAsyncService scriptAsyncService = mock( ScriptAsyncService.class );
        when( scriptAsyncService.getAsyncExecutor( any() ) ).thenReturn( Runnable::run );

        this.manager = new ScriptEventManagerImpl( newBundleContext(), scriptAsyncService );
    }

    private ScriptEventListener newListener( final String app )
    {
        final ScriptEventListener listener = mock( ScriptEventListener.class );
        when( listener.getApplication() ).thenReturn( ApplicationKey.from( app ) );
        return listener;
    }

    @Test
    @SuppressWarnings("unchecked")
    void trackerCallbacksResolveTheService()
    {
        final ServiceReference<Application> reference = mock( ServiceReference.class );
        // the tracked service is what the customizer stores; a modification is not interesting
        // to a listener registry, so it is deliberately a no-op
        assertNull( this.manager.addingService( reference ) );
        assertDoesNotThrow( () -> this.manager.modifiedService( reference, mock( Application.class ) ) );
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
    @SuppressWarnings("unchecked")
    void appRemoval_removesOnlyItsListeners()
    {
        final ScriptEventListener listener1 = newListener( "foo.bar" );
        final ScriptEventListener listener2 = newListener( "foo.other" );
        this.manager.add( listener1 );
        this.manager.add( listener2 );

        assertEquals( 2, StreamSupport.stream( manager.spliterator(), false ).count() );

        // fires inside unregister(), before a reconfigure's replacement bootstraps: the
        // successor's freshly registered listeners can never be the ones removed
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );
        this.manager.removedService( mock( ServiceReference.class ), application );

        assertEquals( 1, StreamSupport.stream( manager.spliterator(), false ).count() );
        assertSame( listener2, this.manager.iterator().next() );
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

        this.manager = new ScriptEventManagerImpl( newBundleContext(), scriptAsyncService );

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
