package com.enonic.xp.repo.impl.node.dao;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.internal.blobstore.MemoryBlobRecord;
import com.enonic.xp.internal.blobstore.cache.CachedBlobStore;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;

import static com.enonic.xp.repo.impl.node.NodeConstants.NODE_SEGMENT;
import static org.junit.Assert.*;

public class NodeVersionServiceImplTest
    extends AbstractNodeTest
{

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void store()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );

        final NodeVersion nodeVersion = NodeVersion.create().
            nodeType( NodeType.DEFAULT_NODE_COLLECTION ).
            id( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            data( data ).
            build();
        final NodeVersionId nodeVersionId = nodeDao.store( nodeVersion );

        assertNotNull( nodeVersionId );

        final BlobRecord blob = blobStore.getRecord( NODE_SEGMENT, BlobKey.from( nodeVersionId.toString() ) );
        assertNotNull( blob );
    }

    @Test
    public void getVersion()
        throws Exception
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final NodeVersion nodeVersion = nodeDao.get( createdNode.getNodeVersionId() );

        assertEquals( createdNode.id(), nodeVersion.getId() );
        assertEquals( createdNode.getNodeVersionId(), nodeVersion.getVersionId() );
        assertEquals( createdNode.data(), nodeVersion.getData() );
    }

    @Test
    public void getVersions()
        throws Exception
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );
        final CreateNodeParams createNodeParams2 = CreateNodeParams.create().
            name( "my-node2" ).
            parent( NodePath.ROOT ).
            data( data ).
            build();

        final Node createdNode = createNode( createNodeParams );
        final Node createdNode2 = createNode( createNodeParams2 );

        final NodeVersions nodeVersions =
            nodeDao.get( NodeVersionIds.from( createdNode.getNodeVersionId(), createdNode2.getNodeVersionId() ) );

        assertEquals( 2, nodeVersions.getSize() );
        assertEquals( createdNode.id(), nodeVersions.get( 0 ).getId() );
        assertEquals( createdNode.getNodeVersionId(), nodeVersions.get( 0 ).getVersionId() );
        assertEquals( createdNode.data(), nodeVersions.get( 0 ).getData() );
        assertEquals( createdNode2.id(), nodeVersions.get( 1 ).getId() );
        assertEquals( createdNode2.getNodeVersionId(), nodeVersions.get( 1 ).getVersionId() );
        assertEquals( createdNode2.data(), nodeVersions.get( 1 ).getData() );
    }

    @Test
    public void getVersionCorrupted()
        throws Exception
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final BlobRecord blob = this.blobStore.getRecord( NODE_SEGMENT, BlobKey.from( createdNode.getNodeVersionId().toString() ) );
        byte[] blobData = blob.getBytes().read();
        blobData = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobData ) );
        this.blobStore.addRecord( NODE_SEGMENT, corruptedBlob );

        try
        {
            nodeDao.get( createdNode.getNodeVersionId() );
            fail( "Expected exception" );
        }
        catch ( RuntimeException e )
        {
            assertTrue( e.getMessage().startsWith( "Failed to load blob with key" ) );
        }
    }

    @Test
    public void avoidCachingVersionCorrupted()
        throws Exception
    {
        final CachedBlobStore cachedBlobStore = CachedBlobStore.create().blobStore( this.blobStore ).build();
        this.nodeDao.setBlobStore( cachedBlobStore );

        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final BlobRecord blob = this.blobStore.getRecord( NODE_SEGMENT, BlobKey.from( createdNode.getNodeVersionId().toString() ) );
        final byte[] blobData = blob.getBytes().read();
        final byte[] blobDataTruncated = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobDataTruncated ) );
        this.blobStore.addRecord( NODE_SEGMENT, corruptedBlob );
        cachedBlobStore.invalidate( NODE_SEGMENT, blob.getKey() );

        try
        {
            nodeDao.get( createdNode.getNodeVersionId() );
            fail( "Expected exception" );
        }
        catch ( RuntimeException e )
        {
            assertTrue( e.getMessage().startsWith( "Failed to load blob with key" ) );
        }

        // restore original blob in source blob store
        this.blobStore.addRecord( NODE_SEGMENT, blob );

        final NodeVersion nodeVersion = nodeDao.get( createdNode.getNodeVersionId() );
        assertNotNull( nodeVersion );
    }
}