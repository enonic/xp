package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.ScriptTestSupport;

class ListRepositoriesScriptTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = Mockito.mock( RepositoryService.class );
        Mockito.when( repositoryService.list() ).
            thenAnswer( invocation -> {
                final Repository testRepository = Repository.create().
                    id( RepositoryId.from( "test-repo" ) ).
                    branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
                    build();
                final Repository anotherRepository = Repository.create().
                    id( RepositoryId.from( "another-repo" ) ).
                    branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
                    build();
                return Repositories.from( testRepository, anotherRepository );

            } );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/repo/list.js" );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).list();
    }
}
