package com.enonic.xp.portal.impl.websocket;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.Session;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketManagerImplTest
{
    private WebSocketManagerImpl manager;

    private WebSocketService webSocketService;

    private BundleContext bundleContext;

    @BeforeEach
    void setup()
        throws Exception
    {
        ContextBuilder.create().build().runWith( ContextAccessor::current );
        // let the ServiceTracker construct and open against a mock registry (no initial services)
        bundleContext = mock( BundleContext.class );
        lenient().when( bundleContext.createFilter( anyString() ) )
            .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        lenient().when( bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );
        this.webSocketService = mock( WebSocketService.class );
        this.manager = new WebSocketManagerImpl( bundleContext, this.webSocketService );
    }

    @Test
    @SuppressWarnings("unchecked")
    void trackerLifecycle()
    {
        final ServiceReference<Application> reference = mock( ServiceReference.class );
        final Application application = mock( Application.class );
        when( bundleContext.getService( reference ) ).thenReturn( application );

        assertEquals( application, manager.addingService( reference ) );
        manager.modifiedService( reference, application );
        manager.deactivate();
    }

    @Test
    @SuppressWarnings("unchecked")
    void appRemoval_survivesAFailingSessionClose()
        throws Exception
    {
        final Session session = openConnection( ApplicationKey.from( "myapp" ), "session1" );
        doThrow( new IOException( "already gone" ) ).when( session ).close( any( CloseReason.class ) );
        manager.addToGroup( "g", "session1" );
        assertEquals( 1, manager.getGroupSize( "g" ) );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "myapp" ) );
        assertDoesNotThrow( () -> manager.removedService( mock( ServiceReference.class ), application ) );

        // the dead entry left the registry even though the session refused to close
        assertEquals( 0, manager.getGroupSize( "g" ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    void appRemoval_closesOnlyItsConnections()
        throws Exception
    {
        final Session myAppSession = openConnection( ApplicationKey.from( "myapp" ), "session1" );
        final Session otherAppSession = openConnection( ApplicationKey.from( "otherapp" ), "session2" );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "myapp" ) );
        manager.removedService( mock( ServiceReference.class ), application );

        // the stopped application's session is closed; the other application's lives on
        verify( myAppSession ).close( any( CloseReason.class ) );
        verify( otherAppSession, never() ).close( any( CloseReason.class ) );
    }

    private Session openConnection( final ApplicationKey applicationKey, final String sessionId )
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        when( this.webSocketService.isUpgradeRequest( req ) ).thenReturn( true );

        final WebSocketEndpoint endpoint = mock( WebSocketEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( new WebSocketConfig() );
        when( endpoint.getApplication() ).thenReturn( applicationKey );

        final WebSocketContext context = manager.newContext( req, res );
        assertNotNull( context );
        context.apply( endpoint );

        // the container accepted the upgrade: capture the factory and open the session
        final ArgumentCaptor<EndpointFactory> factory = ArgumentCaptor.forClass( EndpointFactory.class );
        verify( this.webSocketService ).acceptWebSocket( eq( req ), eq( res ), factory.capture() );

        final Session session = mock( Session.class );
        when( session.getId() ).thenReturn( sessionId );
        ContextBuilder.create().build().runWith( () -> {
            final Endpoint entry = factory.getValue().newEndpoint();
            entry.onOpen( session, null );
        } );
        return session;
    }
}
