package com.enonic.xp.lib.repo;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RefreshScriptTest
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
    public void testExample()
    {
        runScript( "/lib/xp/examples/repo/refresh.js" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.SEARCH );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.STORAGE );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.ALL );
    }

    @Test
    public void testRefreshDefault()
        throws Exception
    {
        runFunction( "/test/refresh-test.js", "refreshDefault" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.ALL );
    }

    @Test
    public void testRefreshAll()
        throws Exception
    {
        runFunction( "/test/refresh-test.js", "refreshAll" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.ALL );
    }

    @Test
    public void testRefreshSearch()
        throws Exception
    {
        runFunction( "/test/refresh-test.js", "refreshSearch" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.SEARCH );
    }

    @Test
    public void testRefreshStorage()
        throws Exception
    {
        runFunction( "/test/refresh-test.js", "refreshStorage" );
        verify( this.nodeService, times( 1 ) ).refresh( RefreshMode.STORAGE );
    }

    @Test
    public void testRefreshInvalidMode()
        throws Exception
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
