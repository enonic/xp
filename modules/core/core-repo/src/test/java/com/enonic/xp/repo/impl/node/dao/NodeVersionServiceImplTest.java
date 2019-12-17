package com.enonic.xp.repo.impl.node.dao;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.NodeVersionKeys;
import com.enonic.xp.blob.Segment;
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
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;

import static com.enonic.xp.repo.impl.node.NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL;
import static com.enonic.xp.repo.impl.node.NodeConstants.NODE_SEGMENT_LEVEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class NodeVersionServiceImplTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
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
        final NodeVersionKey nodeVersionKey = nodeDao.store( nodeVersion, createInternalContext() );

        assertNotNull( nodeVersionKey );

        final BlobRecord nodeBlobRecord = blobStore.getRecord( createSegment( NODE_SEGMENT_LEVEL ), nodeVersionKey.getNodeBlobKey() );
        assertNotNull( nodeBlobRecord );
        final BlobRecord indexBlobRecord =
            blobStore.getRecord( createSegment( INDEX_CONFIG_SEGMENT_LEVEL ), nodeVersionKey.getIndexConfigBlobKey() );
        assertNotNull( indexBlobRecord );
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

        final NodeVersion nodeVersion = nodeDao.get( getNodeVersionKey( createdNode ), createInternalContext() );

        assertEquals( createdNode.id(), nodeVersion.getId() );
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
            nodeDao.get( NodeVersionKeys.from( getNodeVersionKey( createdNode ), getNodeVersionKey( createdNode2 ) ),
                         createInternalContext() );

        assertEquals( 2, nodeVersions.getSize() );
        assertEquals( createdNode.id(), nodeVersions.get( 0 ).getId() );
        assertEquals( createdNode.data(), nodeVersions.get( 0 ).getData() );
        assertEquals( createdNode2.id(), nodeVersions.get( 1 ).getId() );
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
        final NodeVersionKey nodeVersionKey = getNodeVersionKey( createdNode );

        final Segment segment = createSegment( NODE_SEGMENT_LEVEL );
        final BlobRecord blob = this.blobStore.getRecord( segment, nodeVersionKey.getNodeBlobKey() );
        byte[] blobData = blob.getBytes().read();
        blobData = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobData ) );
        this.blobStore.addRecord( segment, corruptedBlob );

        try
        {
            nodeDao.get( nodeVersionKey, createInternalContext() );
            fail( "Expected exception" );
        }
        catch ( RuntimeException e )
        {
            assertTrue( e.getMessage().startsWith( "Failed to load blobs with keys" ) );
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
        final NodeVersionKey nodeVersionKey = getNodeVersionKey( createdNode );

        final Segment segment = createSegment( NODE_SEGMENT_LEVEL );
        final BlobRecord blob = this.blobStore.getRecord( segment, nodeVersionKey.getNodeBlobKey() );
        final byte[] blobData = blob.getBytes().read();
        final byte[] blobDataTruncated = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobDataTruncated ) );
        this.blobStore.addRecord( segment, corruptedBlob );
        cachedBlobStore.invalidate( segment, blob.getKey() );

        try
        {
            nodeDao.get( nodeVersionKey, createInternalContext() );
            fail( "Expected exception" );
        }
        catch ( RuntimeException e )
        {
            assertTrue( e.getMessage().startsWith( "Failed to load blobs with keys" ) );
        }

        // restore original blob in source blob store
        this.blobStore.addRecord( segment, blob );

        final NodeVersion nodeVersion = nodeDao.get( nodeVersionKey, createInternalContext() );
        assertNotNull( nodeVersion );
    }

    private NodeVersionKey getNodeVersionKey( Node node )
    {
        return branchService.get( node.id(), createInternalContext() ).
            getNodeVersionKey();
    }
}
