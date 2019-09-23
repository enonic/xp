package com.enonic.xp.lib.repo;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryAttachment;
import com.enonic.xp.repository.RepositoryAttachments;
import com.enonic.xp.repository.RepositoryBinaryAttachments;
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
                final UpdateRepositoryParams updateRepositoryParams = invocation.getArgument( 0 );
                final RepositoryBinaryAttachments binaryAttachments = updateRepositoryParams.getAttachments();
                final List<RepositoryAttachment> attachments = binaryAttachments.stream().
                    map( rba -> new RepositoryAttachment( rba.getReference(), "mockKey" ) ).
                    collect( Collectors.toList() );

                return Repository.create().
                    id( updateRepositoryParams.getRepositoryId() ).
                    branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
                    data( updateRepositoryParams.getData() ).
                    attachments( RepositoryAttachments.from( attachments ) ).
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
