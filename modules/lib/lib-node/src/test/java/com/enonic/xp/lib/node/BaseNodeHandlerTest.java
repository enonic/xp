package com.enonic.xp.lib.node;

import org.mockito.Mockito;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class BaseNodeHandlerTest
    extends ScriptTestSupport
{
    protected NodeService nodeService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.nodeService = Mockito.mock( NodeService.class );
        addService( NodeService.class, this.nodeService );
    }
}
