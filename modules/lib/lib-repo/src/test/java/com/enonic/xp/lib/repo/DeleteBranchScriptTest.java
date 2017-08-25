package com.enonic.xp.lib.repo;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.ScriptTestSupport;

public class DeleteBranchScriptTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = Mockito.mock( RepositoryService.class );
        Mockito.when( repositoryService.deleteBranch( Mockito.any() ) ).thenReturn( Branch.from( "test-branch" ) );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    public void testExample()
    {
        runScript( "/site/lib/xp/examples/repo/deleteBranch.js" );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).deleteBranch( Mockito.any() );
    }
}
