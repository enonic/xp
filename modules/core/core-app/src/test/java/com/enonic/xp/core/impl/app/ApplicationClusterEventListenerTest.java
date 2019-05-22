package com.enonic.xp.core.impl.app;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEventListener;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class ApplicationClusterEventListenerTest
    extends BundleBasedTest
{

    private ApplicationClusterEventListener applicationClusterEventListener;

    private ApplicationService applicationService;

    @Before
    public void setUp()
        throws Exception
    {
        applicationClusterEventListener = new ApplicationClusterEventListener();
        applicationService = Mockito.mock( ApplicationService.class );
        applicationClusterEventListener.setApplicationService( applicationService );
    }

    @Test
    public void installed()
        throws Exception
    {
        final Node node = Node.create().
            name( "myNode" ).
            id( NodeId.from( "myNodeId" ) ).
            parentPath( NodePath.ROOT ).
            build();

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).
            thenReturn( ApplicationKey.from( "appKey" ) );

        Mockito.when( applicationService.installStoredApplication( node.id(), false, false ) ).
            thenReturn( application );

        this.applicationClusterEventListener.onEvent( Event.create( ApplicationClusterEvents.EVENT_TYPE ).
            localOrigin( false ).
            value( ApplicationClusterEvents.NODE_ID_PARAM, node.id() ).
            value( ApplicationClusterEvents.EVENT_TYPE_KEY, ApplicationClusterEvents.INSTALLED ).
            build() );

        Mockito.verify( this.applicationService, Mockito.times( 1 ) ).installStoredApplication( node.id(), false, false );
    }
}