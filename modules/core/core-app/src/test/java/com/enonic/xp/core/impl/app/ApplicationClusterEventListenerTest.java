package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEventListener;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationClusterEventListenerTest
    extends BundleBasedTest
{
    private ApplicationClusterEventListener applicationClusterEventListener;

    private ApplicationService applicationService;

    @BeforeEach
    void setUp()
    {
        applicationService = mock( ApplicationService.class );
        applicationClusterEventListener = new ApplicationClusterEventListener( applicationService );
    }

    @Test
    void installed()
    {
        final Node node = Node.create().
            name( "myNode" ).
            id( NodeId.from( "myNodeId" ) ).
            parentPath( NodePath.ROOT ).
            build();

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "appKey" ) );

        when( applicationService.installStoredApplication( eq( node.id() ), any() ) ).thenReturn( application );

        this.applicationClusterEventListener.onEvent( Event.create( ApplicationClusterEvents.EVENT_TYPE ).
            localOrigin( false ).
            value( ApplicationClusterEvents.NODE_ID_PARAM, node.id() ).
            value( ApplicationClusterEvents.EVENT_TYPE_KEY, ApplicationClusterEvents.INSTALLED ).
            build() );

        verify( this.applicationService, times( 1 ) ).
            installStoredApplication( node.id(), ApplicationInstallationParams.create().start( false ).build() );
    }

    @Test
    void start()
    {
        final Application application = mock( Application.class );
        final ApplicationKey appKey = ApplicationKey.from( "appKey" );
        when( application.getKey() ).thenReturn( appKey );

        this.applicationClusterEventListener.onEvent( Event.create( ApplicationClusterEvents.EVENT_TYPE ).
            localOrigin( false ).
            value( ApplicationClusterEvents.EVENT_TYPE_KEY, ApplicationClusterEvents.START ).
            value( ApplicationClusterEvents.APPLICATION_KEY_PARAM, appKey ).
            build() );

        verify( this.applicationService, times( 1 ) ).
            startApplication( appKey, false );
    }

    @Test
    void stop()
    {
        final Application application = mock( Application.class );
        final ApplicationKey appKey = ApplicationKey.from( "appKey" );
        when( application.getKey() ).thenReturn( appKey );

        this.applicationClusterEventListener.onEvent( Event.create( ApplicationClusterEvents.EVENT_TYPE ).
            localOrigin( false ).
            value( ApplicationClusterEvents.EVENT_TYPE_KEY, ApplicationClusterEvents.STOP ).
            value( ApplicationClusterEvents.APPLICATION_KEY_PARAM, appKey ).
            build() );

        verify( this.applicationService, times( 1 ) ).
            stopApplication( appKey, false );
    }

    @Test
    void uninstall()
    {
        final Application application = mock( Application.class );
        final ApplicationKey appKey = ApplicationKey.from( "appKey" );
        when( application.getKey() ).thenReturn( appKey );

        this.applicationClusterEventListener.onEvent( Event.create( ApplicationClusterEvents.EVENT_TYPE ).
            localOrigin( false ).
            value( ApplicationClusterEvents.EVENT_TYPE_KEY, ApplicationClusterEvents.UNINSTALL ).
            value( ApplicationClusterEvents.APPLICATION_KEY_PARAM, appKey ).
            build() );

        verify( this.applicationService, times( 1 ) ).
            uninstallApplication( appKey, false );
    }
}
