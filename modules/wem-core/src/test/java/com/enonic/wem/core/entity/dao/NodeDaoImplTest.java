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
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public class NodeDaoImplTest
{
    private final NodeDaoImpl nodeDao = new NodeDaoImpl();

    private final WorkspaceService workspaceService = Mockito.mock( WorkspaceService.class );

    private final BlobService blobService = Mockito.mock( BlobService.class );

    private final Workspace WORKSPACE = Workspace.from( "test" );

    @Before
    public void setUp()
        throws Exception
    {
        nodeDao.setBlobService( blobService );
        nodeDao.setWorkspaceService( workspaceService );
    }

    @Test
    public void push()
        throws Exception
    {
        final BlobKey blobKey = new BlobKey( "a" );
        Mockito.when( workspaceService.getById( Mockito.isA( WorkspaceIdQuery.class ) ) ).
            thenReturn( blobKey );

        final String serializedNode = readFromFile( "serialized-node.json" );

        Mockito.when( blobService.get( blobKey ) ).
            thenReturn( new MemoryBlobRecord( blobKey, serializedNode.getBytes() ) );

        final PushNodeArguments pushNodeArguments = new PushNodeArguments( WORKSPACE, EntityId.from( "1" ) );
        nodeDao.push( pushNodeArguments, WORKSPACE );
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
