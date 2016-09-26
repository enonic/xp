package com.enonic.xp.lib.node;

import org.mockito.Mockito;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class BaseNodeHandlerTest
    extends ScriptTestSupport
{
    protected NodeService nodeService;

    protected RepositoryService repositoryService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.nodeService = Mockito.mock( NodeService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
        addService( NodeService.class, this.nodeService );
        addService( RepositoryService.class, this.repositoryService );
    }
}
