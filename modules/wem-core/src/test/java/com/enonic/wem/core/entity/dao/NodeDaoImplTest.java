package com.enonic.wem.core.entity.dao;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.blobstore.memory.MemoryBlobRecord;
import com.enonic.wem.core.workspace.WorkspaceStore;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public class NodeDaoImplTest
{
    private final NodeDaoImpl nodeDao = new NodeDaoImpl();

    private final WorkspaceStore workspaceStore = Mockito.mock( WorkspaceStore.class );

    private final BlobService blobService = Mockito.mock( BlobService.class );

    private final Workspace prod = new Workspace( "prod" );

    @Before
    public void setUp()
        throws Exception
    {
        nodeDao.setBlobService( blobService );
        nodeDao.setWorkspaceStore( workspaceStore );
    }

    @Test
    public void push()
        throws Exception
    {
        final BlobKey blobKey = new BlobKey( "a" );
        Mockito.when( workspaceStore.getById( Mockito.isA( WorkspaceIdQuery.class ) ) ).
            thenReturn( blobKey );

        final String serializedNode = readFromFile( "serialized-node.json" );

        Mockito.when( blobService.get( blobKey ) ).
            thenReturn( new MemoryBlobRecord( blobKey, serializedNode.getBytes() ) );

        final PushNodeArguments pushNodeArguments = new PushNodeArguments( prod, EntityId.from( "1" ) );
        nodeDao.push( pushNodeArguments, prod );
    }


    protected String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, Charsets.UTF_8 );
    }

}
