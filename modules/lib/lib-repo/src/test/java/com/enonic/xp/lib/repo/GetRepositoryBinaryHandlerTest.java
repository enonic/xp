package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetRepositoryBinaryHandlerTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = mock( RepositoryService.class );
        when( repositoryService.getBinary( any(), any() ) ).thenReturn( ByteSource.empty() );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    void getBinary()
    {
        runScript( "/lib/xp/examples/repo/getBinary.js" );

        verify( repositoryService, Mockito.times( 1 ) ).
            getBinary( eq( RepositoryId.from( "my-repo" ) ), eq( BinaryReference.from( "myBinaryReference" ) ) );
    }
}
