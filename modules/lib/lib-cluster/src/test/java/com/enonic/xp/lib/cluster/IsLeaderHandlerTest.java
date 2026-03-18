package com.enonic.xp.lib.cluster;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;

class IsLeaderHandlerTest
    extends ScriptTestSupport
{
    private ClusterService clusterService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.clusterService = Mockito.mock( ClusterService.class );
        addService( ClusterService.class, this.clusterService );

        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
    }

    @Test
    void testExamples()
    {
        Mockito.when( this.clusterService.isLeader() ).thenReturn( true );

        runScript( "/lib/xp/examples/cluster/isLeader.js" );
    }
}
