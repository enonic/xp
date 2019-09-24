package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.EditableRepository;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryData;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateRepositoryHandlerTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    private static final Repository MOCK_REPO;

    static
    {
        MOCK_REPO = Repository.create().
            id( RepositoryId.from( "my-repo" ) ).
            data( RepositoryData.from( new PropertyTree() ) ).
            branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
            build();
    }

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = Mockito.mock( RepositoryService.class );
        Mockito.when( repositoryService.updateRepository( Mockito.any() ) ).
            thenReturn( MOCK_REPO );
        addService( RepositoryService.class, repositoryService );
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/repo/update.js" );

        ArgumentCaptor<UpdateRepositoryParams> captor = ArgumentCaptor.forClass( UpdateRepositoryParams.class );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).updateRepository( captor.capture() );

        final UpdateRepositoryParams capturedParams = captor.getValue();
        assertEquals( RepositoryId.from( "my-repo" ), capturedParams.getRepositoryId() );
        final EditableRepository edited = new EditableRepository( MOCK_REPO );
        capturedParams.getEditor().accept( edited );
        assertSame( MOCK_REPO, edited.source );
        //todo assert edited fields
    }

    @SuppressWarnings("unused")
    public ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
