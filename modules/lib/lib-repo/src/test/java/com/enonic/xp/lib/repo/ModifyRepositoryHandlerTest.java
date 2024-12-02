package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.repository.EditableRepository;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModifyRepositoryHandlerTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    private static final Repository MOCK_REPO;

    static
    {
        final PropertyTree value = new PropertyTree();
        value.setString( "toBeRemoved", "toBeRemoved" );
        value.setString( "myScopedObject.myScopedString", "toBeModified" );
        value.setString( "toBeKept", "toBeKeptValue" );

        MOCK_REPO = Repository.create().
            id( RepositoryId.from( "my-repo" ) ).
            data( value ).
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
    void modify()
        throws Exception
    {
        runScript( "/lib/xp/examples/repo/modify.js" );

        ArgumentCaptor<UpdateRepositoryParams> captor = ArgumentCaptor.forClass( UpdateRepositoryParams.class );
        Mockito.verify( this.repositoryService, Mockito.times( 1 ) ).updateRepository( captor.capture() );

        final UpdateRepositoryParams capturedParams = captor.getValue();
        assertEquals( RepositoryId.from( "my-repo" ), capturedParams.getRepositoryId() );

        final EditableRepository edited = new EditableRepository( MOCK_REPO );
        capturedParams.getEditor().accept( edited );

        assertSame( MOCK_REPO, edited.source );

        assertEquals( "modified", edited.data.getString( "myString" ) );

        assertTrue( edited.source.getData().hasProperty( "toBeRemoved" ) );
        assertFalse( edited.data.hasProperty( "toBeRemoved" ) );

        final BinaryAttachment attachment = edited.binaryAttachments.get( 0 );
        assertEquals( BinaryReference.from( "myFile" ), attachment.getReference() );
        assertTrue( attachment.getByteSource().contentEquals( ByteSource.wrap( "Hello World".getBytes() ) ) );

        assertFalse( edited.source.isTransient() );
        assertTrue( edited.transientFlag );

    }

    @SuppressWarnings("unused")
    public ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
