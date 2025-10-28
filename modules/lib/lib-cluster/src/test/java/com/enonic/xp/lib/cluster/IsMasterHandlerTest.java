package com.enonic.xp.lib.cluster;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;

class IsMasterHandlerTest
    extends ScriptTestSupport
{
    private IndexService indexService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.indexService = Mockito.mock( IndexService.class );
        addService( IndexService.class, this.indexService );

        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
    }

    @Test
    void testExamples()
    {
        Mockito.when( this.indexService.isMaster() ).thenReturn( true );

        runScript( "/lib/xp/examples/cluster/isMaster.js" );
    }
}
