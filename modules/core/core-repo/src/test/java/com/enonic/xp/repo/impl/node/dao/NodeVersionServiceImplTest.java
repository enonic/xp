package com.enonic.xp.repo.impl.node.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.NodeVersionKeys;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.internal.blobstore.MemoryBlobRecord;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.internal.blobstore.cache.CachedBlobStore;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.config.RepoConfigurationImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.security.acl.AccessControlList;

import static com.enonic.xp.repo.impl.node.NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL;
import static com.enonic.xp.repo.impl.node.NodeConstants.NODE_SEGMENT_LEVEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeVersionServiceImplTest
{
    private static final MemoryBlobStore BLOB_STORE = new MemoryBlobStore();

    private NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( BLOB_STORE, new RepoConfigurationImpl( Map.of() ) );

    @BeforeEach
    void setUp()
    {
        this.nodeDao = new NodeVersionServiceImpl( BLOB_STORE, new RepoConfigurationImpl( Map.of() ) );
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
            permissions( AccessControlList.empty() ).
            indexConfigDocument( PatternIndexConfigDocument.create()
                                     .defaultConfig( IndexConfig.BY_TYPE ).build() ).
            build();
        final NodeVersionKey nodeVersionKey = executeInContext( () -> nodeDao.store( nodeVersion, createInternalContext() ) );

        assertNotNull( nodeVersionKey );

        final BlobRecord nodeBlobRecord =
            BLOB_STORE.getRecord( executeInContext( () -> createSegment( NODE_SEGMENT_LEVEL ) ), nodeVersionKey.getNodeBlobKey() );
        assertNotNull( nodeBlobRecord );
        final BlobRecord indexBlobRecord = BLOB_STORE.getRecord( executeInContext( () -> createSegment( INDEX_CONFIG_SEGMENT_LEVEL ) ),
                                                                 nodeVersionKey.getIndexConfigBlobKey() );
        assertNotNull( indexBlobRecord );
    }

    @Test
    void getVersion()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );
        final PropertySet set = data.newSet();
        set.setString( "myNameInSet", "myValueInSet" );
        set.addSet( "mySet", set );
        set.addSet( "myEmptySet", data.newSet() );
        set.ensurePropertySet( "myNoValuePropertySet" );
        set.addSet( "myNullSet", null );
        set.addString( "myNullString", null );

        final NodeVersion nodeVersion = NodeVersion.create().
            nodeType( NodeType.DEFAULT_NODE_COLLECTION ).
            id( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create()
                                     .defaultConfig( IndexConfig.BY_TYPE ).build() ).
            build();

        final NodeVersionKey nodeVersionKey = nodeDao.store( nodeVersion, createInternalContext() );

        final NodeVersion returnedNodeVersion = nodeDao.get( nodeVersionKey, createInternalContext() );

        assertEquals( returnedNodeVersion.getId(), nodeVersion.getId() );
        assertEquals( returnedNodeVersion.getData(), nodeVersion.getData() );
    }

    @Test
    void getVersion_issue_10558() throws Exception
    {
        final List<PropertyArrayJson> list =
            ObjectMapperHelper.create().readValue( "[\n" + "    {\n" + "        \"name\": \"target\",\n" +
                                                          "        \"type\": \"Reference\",\n" + "        \"values\": [\n" +
                                                          "            {\n" +
                                                          "                \"v\": \"a0f4f654-82c5-4e56-9018-9ffe3f61c6ff\"\n" +
                                                          "            }\n" + "        ]\n" + "    },\n" + "    {\n" +
                                                          "        \"name\": \"parameters\",\n" + "        \"type\": \"PropertySet\",\n" +
                                                          "        \"values\": []\n" + "    }\n" + "]", new TypeReference<>()
            {
            } );

        final PropertyTree data = PropertyTreeJson.fromJson( list );

        final NodeVersion nodeVersion = NodeVersion.create().
            nodeType( NodeType.DEFAULT_NODE_COLLECTION ).
            id( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create()
                                     .defaultConfig( IndexConfig.BY_TYPE ).build() ).
            build();

        final NodeVersionKey nodeVersionKey = executeInContext( () -> nodeDao.store( nodeVersion, createInternalContext() ) );
        final NodeVersion returnedNodeVersion = executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) );

        assertEquals( returnedNodeVersion.getId(), nodeVersion.getId() );
        assertEquals( returnedNodeVersion.getData(), nodeVersion.getData() );
    }

    @Test
    public void getVersions()
        throws Exception
    {
        final PropertyTree data1 = new PropertyTree();
        data1.addString( "myName", "myValue1" );

        final NodeVersion nodeVersion1 = NodeVersion.create().
            nodeType( NodeType.DEFAULT_NODE_COLLECTION ).
            id( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            data( data1 ).
            indexConfigDocument( PatternIndexConfigDocument.create()
                                     .defaultConfig( IndexConfig.BY_TYPE ).build() ).
            build();

        final NodeVersionKey nodeVersionKey1 = executeInContext( () -> nodeDao.store( nodeVersion1, createInternalContext() ) );

        final PropertyTree data2 = new PropertyTree();
        data2.addString( "myName", "myValue2" );

        final NodeVersion nodeVersion2 = NodeVersion.create().
            nodeType( NodeType.DEFAULT_NODE_COLLECTION ).
            id( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            data( data2 ).
            indexConfigDocument( PatternIndexConfigDocument.create()
                                     .defaultConfig( IndexConfig.BY_TYPE ).build() ).
            build();

        final NodeVersionKey nodeVersionKey2 = executeInContext( () -> nodeDao.store( nodeVersion2, createInternalContext() ) );

        NodeVersions.Builder builder = NodeVersions.create();
        NodeVersionKeys.from( nodeVersionKey1, nodeVersionKey2 )
            .forEach( nodeVersionKey -> builder.add( executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) ) ) );

        final NodeVersions nodeVersions = builder.build();

        assertEquals( 2, nodeVersions.getSize() );
        assertEquals( nodeVersion1.getId(), nodeVersions.get( 0 ).getId() );
        assertEquals( nodeVersion1.getData(), nodeVersions.get( 0 ).getData() );
        assertEquals( nodeVersion2.getId(), nodeVersions.get( 1 ).getId() );
        assertEquals( nodeVersion2.getData(), nodeVersions.get( 1 ).getData() );
    }

    @Test
    public void getVersionCorrupted()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );

        final NodeVersion nodeVersion = NodeVersion.create().
            nodeType( NodeType.DEFAULT_NODE_COLLECTION ).
            id( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create()
                                     .defaultConfig( IndexConfig.BY_TYPE ).build() ).
            build();

        final NodeVersionKey nodeVersionKey = executeInContext( () -> nodeDao.store( nodeVersion, createInternalContext() ) );

        final Segment segment = executeInContext( () -> createSegment( NODE_SEGMENT_LEVEL ) );
        final BlobRecord blob = BLOB_STORE.getRecord( segment, nodeVersionKey.getNodeBlobKey() );
        byte[] blobData = blob.getBytes().read();
        blobData = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobData ) );
        BLOB_STORE.addRecord( segment, corruptedBlob );

        RuntimeException e =
            assertThrows( RuntimeException.class, () -> executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) ) );
        assertTrue( e.getMessage().startsWith( "Failed to load blobs with keys" ) );
    }

    @Test
    public void avoidCachingVersionCorrupted()
        throws Exception
    {
        final CachedBlobStore cachedBlobStore = CachedBlobStore.create().blobStore( BLOB_STORE ).build();

        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );

        final NodeVersion nodeVersion = NodeVersion.create().
            nodeType( NodeType.DEFAULT_NODE_COLLECTION ).
            id( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create()
                                     .defaultConfig( IndexConfig.BY_TYPE ).build() ).
            build();

        final NodeVersionKey nodeVersionKey = executeInContext( () -> nodeDao.store( nodeVersion, createInternalContext() ) );

        final Segment segment = executeInContext( () -> createSegment( NODE_SEGMENT_LEVEL ) );
        final BlobRecord blob = BLOB_STORE.getRecord( segment, nodeVersionKey.getNodeBlobKey() );
        final byte[] blobData = blob.getBytes().read();
        final byte[] blobDataTruncated = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobDataTruncated ) );
        BLOB_STORE.addRecord( segment, corruptedBlob );
        cachedBlobStore.invalidate( segment, blob.getKey() );

        RuntimeException e =
            assertThrows( RuntimeException.class, () -> executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) ) );
        assertTrue( e.getMessage().startsWith( "Failed to load blobs with keys" ) );

        // restore original blob in source blob store
        BLOB_STORE.addRecord( segment, blob );

        final NodeVersion returnedNodeVersion = executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) );
        assertNotNull( returnedNodeVersion );
    }

    protected Segment createSegment( SegmentLevel blobTypeLevel )
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        return RepositorySegmentUtils.toSegment( repositoryId, blobTypeLevel );
    }

    protected InternalContext createInternalContext()
    {
        final Context currentContext = ContextAccessor.current();
        return InternalContext.create( currentContext ).build();
    }

    private <T> T executeInContext( final Callable<T> runnable )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .build()
            .callWith( runnable );
    }
}
