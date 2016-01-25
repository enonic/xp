package com.enonic.xp.core.impl.app;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class ApplicationEventListenerTest
    extends BundleBasedTest
{

    private ApplicationEventListener applicationEventListener;

    private ApplicationService applicationService;

    @Before
    public void setUp()
        throws Exception
    {
        applicationEventListener = new ApplicationEventListener();
        applicationService = Mockito.mock( ApplicationService.class );
        applicationEventListener.setApplicationService( applicationService );
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

        Mockito.when( applicationService.installApplication( node.id() ) ).
            thenReturn( application );

        this.applicationEventListener.onEvent( Event.create( ApplicationEvents.APPLICATION_INSTALLED_EVENT ).
            localOrigin( false ).
            value( ApplicationEvents.NODE_ID_PARAM, node.id() ).
            build() );

        Mockito.verify( this.applicationService, Mockito.times( 1 ) ).installApplication( node.id() );
    }
}