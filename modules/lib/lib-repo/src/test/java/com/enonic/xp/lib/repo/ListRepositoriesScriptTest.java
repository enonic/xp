package com.enonic.xp.lib.repo;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.ScriptTestSupport;

public class ListRepositoriesScriptTest
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
                    branchInfos( RepositoryConstants.MASTER_BRANCH_INFO  ).
                    build();
                final Repository anotherRepository = Repository.create().
                    id( RepositoryId.from( "another-repo" ) ).
                    branchInfos( RepositoryConstants.MASTER_BRANCH_INFO  ).
                    build();
                return Repositories.from( testRepository, anotherRepository );

            } );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    public void testExample()
    {
        runScript( "/lib/xp/examples/repo/list.js" );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).list();
    }
}
