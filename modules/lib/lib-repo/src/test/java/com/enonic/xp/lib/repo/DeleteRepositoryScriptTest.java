package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.ScriptTestSupport;

class DeleteRepositoryScriptTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = Mockito.mock( RepositoryService.class );
        Mockito.when( repositoryService.deleteRepository( Mockito.any() ) ).
            thenReturn( RepositoryId.from( "test-repo" ) );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/repo/delete.js" );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).deleteRepository( Mockito.any() );
    }
}
