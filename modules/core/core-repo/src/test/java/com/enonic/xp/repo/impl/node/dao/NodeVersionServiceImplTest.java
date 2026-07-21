package com.enonic.xp.repo.impl.node.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.internal.blobstore.MemoryBlobRecord;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.security.acl.AccessControlList;

import static com.enonic.xp.repo.impl.node.NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL;
import static com.enonic.xp.repo.impl.node.NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL;
import static com.enonic.xp.repo.impl.node.NodeConstants.NODE_SEGMENT_LEVEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeVersionServiceImplTest
{
    private static final MemoryBlobStore BLOB_STORE = new MemoryBlobStore();

    private NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( BLOB_STORE, new RepoConfiguration( Map.of() ) );

    @BeforeEach
    void setUp()
    {
        this.nodeDao = new NodeVersionServiceImpl( BLOB_STORE, new RepoConfiguration( Map.of() ) );
    }

    @Test
    void store()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data )
            .permissions( AccessControlList.empty() )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();
        final NodeVersionKey nodeVersionKey = executeInContext( () -> storeAndPersist( nodeVersion, createInternalContext() ) );

        assertNotNull( nodeVersionKey );

        final BlobRecord nodeBlobRecord =
            BLOB_STORE.getRecord( executeInContext( () -> createSegment( NODE_SEGMENT_LEVEL ) ), nodeVersionKey.getNodeBlobKey() );
        assertNotNull( nodeBlobRecord );
        final BlobRecord indexBlobRecord = BLOB_STORE.getRecord( executeInContext( () -> createSegment( INDEX_CONFIG_SEGMENT_LEVEL ) ),
                                                                 nodeVersionKey.getIndexConfigBlobKey() );
        assertNotNull( indexBlobRecord );
    }

    /**
     * Phase 3 Gate B (nodb/BUILD-PHASE-3.md): {@code serialize} is pure -- persisting the
     * segment bytes moved to the storage SPI ({@code NodeStore#storeVersion}/
     * {@code #storeNode}), so calling it alone must NOT write anything to the injected
     * {@code BlobStore} (unlike the old {@code store}, which always did). Checks the
     * NODE segment only, not index-config/ACL: {@code BLOB_STORE} is a shared static field
     * across this class's tests and index-config/ACL content is often byte-identical across
     * tests (same {@code IndexConfig}/{@code AccessControlList} fixtures), so those segments
     * can legitimately already be populated by an unrelated, earlier test's real write
     * (content-addressed dedup) -- not evidence of a leak from THIS call. The node-data
     * segment always embeds a fresh random {@code NodeId}, so its hash is guaranteed unique
     * per test run.
     */
    @Test
    void serialize_isPure_writesNothingToBlobStore()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data )
            .permissions( AccessControlList.empty() )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();

        final SerializedNodeVersion serialized = nodeDao.serialize( nodeVersion );

        assertNotNull( serialized.key() );
        assertNull( BLOB_STORE.getRecord( executeInContext( () -> createSegment( NODE_SEGMENT_LEVEL ) ), serialized.key().getNodeBlobKey() ) );
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
        set.ensureProperty( "myNoValuePropertySet", ValueTypes.PROPERTY_SET );
        set.ensureProperty( "myNoValueString", ValueTypes.STRING );
        set.addSet( "myNullSet", null );
        set.addString( "myNullString", null );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();

        final NodeVersionKey nodeVersionKey = executeInContext( () -> storeAndPersist( nodeVersion, createInternalContext() ) );

        final NodeStoreVersion returnedNodeVersion = executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) );

        assertEquals( returnedNodeVersion.id(), nodeVersion.id() );
        assertEquals( returnedNodeVersion.data(), nodeVersion.data() );
    }

    @Test
    void getVersion_issue_10558()
        throws Exception
    {
        final List<PropertyArrayJson> list = ObjectMapperHelper.create()
            .readValue(
                "[\n" + "    {\n" + "        \"name\": \"target\",\n" + "        \"type\": \"Reference\",\n" + "        \"values\": [\n" +
                    "            \n" + "        ]\n" + "    },\n" + "    {\n" + "        \"name\": \"parameters\",\n" +
                    "        \"type\": \"PropertySet\",\n" + "        \"values\": []\n" + "    }\n" + "]", new TypeReference<>()
                {
                } );

        final PropertyTree data = PropertyTreeJson.fromJson( list );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();

        final NodeVersionKey nodeVersionKey = executeInContext( () -> storeAndPersist( nodeVersion, createInternalContext() ) );
        final NodeStoreVersion returnedNodeVersion = executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) );

        assertEquals( returnedNodeVersion.id(), nodeVersion.id() );
        assertEquals( returnedNodeVersion.data(), nodeVersion.data() );
    }

    @Test
    void getVersions()
    {
        final PropertyTree data1 = new PropertyTree();
        data1.addString( "myName", "myValue1" );

        final NodeStoreVersion nodeVersion1 = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data1 )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();

        final NodeVersionKey nodeVersionKey1 = executeInContext( () -> storeAndPersist( nodeVersion1, createInternalContext() ) );

        final PropertyTree data2 = new PropertyTree();
        data2.addString( "myName", "myValue2" );

        final NodeStoreVersion nodeVersion2 = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data2 )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();

        final NodeVersionKey nodeVersionKey2 = executeInContext( () -> storeAndPersist( nodeVersion2, createInternalContext() ) );

        List<NodeStoreVersion> nodeVersions = new ArrayList<>();
        List.of( nodeVersionKey1, nodeVersionKey2 )
            .forEach(
                nodeVersionKey -> nodeVersions.add( executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) ) ) );

        assertEquals( 2, nodeVersions.size() );
        assertEquals( nodeVersion1.id(), nodeVersions.get( 0 ).id() );
        assertEquals( nodeVersion1.data(), nodeVersions.get( 0 ).data() );
        assertEquals( nodeVersion2.id(), nodeVersions.get( 1 ).id() );
        assertEquals( nodeVersion2.data(), nodeVersions.get( 1 ).data() );
    }

    @Test
    void getVersionCorrupted()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();

        final NodeVersionKey nodeVersionKey = executeInContext( () -> storeAndPersist( nodeVersion, createInternalContext() ) );

        final Segment segment = executeInContext( () -> createSegment( NODE_SEGMENT_LEVEL ) );
        final BlobRecord blob = BLOB_STORE.getRecord( segment, nodeVersionKey.getNodeBlobKey() );
        byte[] blobData = blob.getBytes().read();
        blobData = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobData ) );
        BLOB_STORE.addRecord( segment, corruptedBlob );

        RuntimeException e =
            assertThrows( RuntimeException.class, () -> executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) ) );
        assertTrue( e.getMessage().startsWith( "Failed to load blob" ) );
    }

    @Test
    void avoidCachingVersionCorrupted()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myName", "myValue" );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .id( new NodeId() )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).build() )
            .build();

        final NodeVersionKey nodeVersionKey = executeInContext( () -> storeAndPersist( nodeVersion, createInternalContext() ) );

        final Segment segment = executeInContext( () -> createSegment( NODE_SEGMENT_LEVEL ) );
        final BlobRecord blob = BLOB_STORE.getRecord( segment, nodeVersionKey.getNodeBlobKey() );
        final byte[] blobData = blob.getBytes().read();
        final byte[] blobDataTruncated = Arrays.copyOf( blobData, blobData.length / 2 );
        final MemoryBlobRecord corruptedBlob = new MemoryBlobRecord( blob.getKey(), ByteSource.wrap( blobDataTruncated ) );
        BLOB_STORE.addRecord( segment, corruptedBlob );

        RuntimeException e =
            assertThrows( RuntimeException.class, () -> executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) ) );
        assertTrue( e.getMessage().startsWith( "Failed to load blob" ) );

        // restore original blob in source blob store
        BLOB_STORE.addRecord( segment, blob );

        final NodeStoreVersion returnedNodeVersion = executeInContext( () -> nodeDao.get( nodeVersionKey, createInternalContext() ) );
        assertNotNull( returnedNodeVersion );
    }

    /**
     * Test-only stand-in for what {@code ElasticsearchNodeStore#storeVersion} now does in
     * production (Phase 3 Gate B, nodb/BUILD-PHASE-3.md): serialize (pure), then persist the
     * three segments to the BlobStore under this repository's segments -- same keys, same
     * bytes, same {@code addRecord} calls the old {@code NodeVersionServiceImpl#store} made
     * directly. Kept here (rather than resurrecting the old method) so this test class stays
     * a focused unit test of {@link NodeVersionServiceImpl}, not a re-implementation of the
     * ES backend.
     */
    private NodeVersionKey storeAndPersist( final NodeStoreVersion nodeVersion, final InternalContext context )
    {
        final SerializedNodeVersion serialized = nodeDao.serialize( nodeVersion );
        final RepositoryId repositoryId = context.getRepositoryId();
        BLOB_STORE.addRecord( RepositorySegmentUtils.toSegment( repositoryId, NODE_SEGMENT_LEVEL ),
                               ByteSource.wrap( serialized.nodeDataBytes() ) );
        BLOB_STORE.addRecord( RepositorySegmentUtils.toSegment( repositoryId, INDEX_CONFIG_SEGMENT_LEVEL ),
                               ByteSource.wrap( serialized.indexConfigBytes() ) );
        BLOB_STORE.addRecord( RepositorySegmentUtils.toSegment( repositoryId, ACCESS_CONTROL_SEGMENT_LEVEL ),
                               ByteSource.wrap( serialized.accessControlBytes() ) );
        return serialized.key();
    }

    protected Segment createSegment( SegmentLevel blobTypeLevel )
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        return RepositorySegmentUtils.toSegment( repositoryId, blobTypeLevel );
    }

    protected InternalContext createInternalContext()
    {
        return InternalContext.from( ContextAccessor.current() );
    }

    private <T> T executeInContext( final Callable<T> runnable )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( ContentConstants.BRANCH_DRAFT )
            .build()
            .callWith( runnable );
    }
}
