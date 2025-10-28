package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RefreshScriptTest
    extends ScriptTestSupport
{
    private NodeService nodeService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        nodeService = Mockito.mock( NodeService.class );
        addService( NodeService.class, nodeService );
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/repo/refresh.js" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.SEARCH );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.STORAGE );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.ALL );
    }

    @Test
    void testRefreshDefault()
    {
        runFunction( "/test/refresh-test.js", "refreshDefault" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.ALL );
    }

    @Test
    void testRefreshAll()
    {
        runFunction( "/test/refresh-test.js", "refreshAll" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.ALL );
    }

    @Test
    void testRefreshSearch()
    {
        runFunction( "/test/refresh-test.js", "refreshSearch" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.SEARCH );
    }

    @Test
    void testRefreshStorage()
    {
        runFunction( "/test/refresh-test.js", "refreshStorage" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.STORAGE );
    }

    @Test
    void testRefreshInvalidMode()
    {
        try
        {
            runFunction( "/test/refresh-test.js", "refreshInvalid" );
            fail( "Exception expected" );
        }
        catch ( ResourceProblemException e )
        {
            assertTrue( e.getCause() instanceof IllegalArgumentException );
        }
        verify( this.nodeService, never() ).refresh( any( RefreshMode.class ) );
    }
}
