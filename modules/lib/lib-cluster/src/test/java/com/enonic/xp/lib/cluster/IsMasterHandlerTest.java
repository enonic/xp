package com.enonic.xp.lib.cluster;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.ScriptTestSupport;

public class IsMasterHandlerTest
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

        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testExamples()
    {
        Mockito.when( this.indexService.isMaster() ).thenReturn( true );

        runScript( "/lib/xp/examples/cluster/isMaster.js" );
    }
}
