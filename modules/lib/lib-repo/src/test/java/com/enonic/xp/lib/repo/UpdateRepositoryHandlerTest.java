package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.testing.ScriptTestSupport;

class UpdateRepositoryHandlerTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = Mockito.mock( RepositoryService.class );
        Mockito.when( repositoryService.updateRepository( Mockito.any() ) ).
            thenAnswer( invocation -> {
                final UpdateRepositoryParams argument = invocation.getArgument( 0 );
                return Repository.create().
                    id( argument.getRepositoryId() ).
                    branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
                    data( argument.getData() ).
                    build();
            } );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/repo/update.js" );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).updateRepository( Mockito.any() );
    }
}
