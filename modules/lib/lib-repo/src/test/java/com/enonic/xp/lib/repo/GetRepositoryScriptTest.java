package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.ScriptTestSupport;

class GetRepositoryScriptTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = Mockito.mock( RepositoryService.class );
        Mockito.when( repositoryService.get( Mockito.any() ) ).
            thenAnswer( invocation -> {
                final RepositoryId repositoryId = (RepositoryId) invocation.getArguments()[0];
                return Repository.create().
                    id( repositoryId ).
                    branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
                    build();
            } );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/repo/get.js" );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).get( Mockito.any() );
    }
}
