package com.enonic.xp.core.node;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
import com.enonic.xp.repository.RepositorySegmentUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Phase 3 Gate C (nodb/BUILD-PHASE-3.md), work order item 2's per-segment lazy read
 * check (DESIGN.md §2.1): a permissions-only read must fetch ONLY the ACL segment over
 * the wire -- never node-data or index-config -- while a full {@code get()} fetches all
 * three. Verified honestly by counting actual {@code BlobStore#getRecord} calls per
 * segment kind against a FRESH {@link NodeVersionServiceImpl} instance (fresh so its three
 * independent Guava caches, per {@code NodeVersionServiceImpl}'s own javadoc, start empty
 * -- a warm cache would make every read a cache hit and the count meaningless), wrapping
 * the real nodb-routed {@link #blobStore} (so this also proves NoDB's {@code GetPayload}
 * RPC itself is invoked exactly once per segment actually needed, not merely that some
 * in-JVM cache short-circuits the call).
 */
class PayloadLazyReadTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "proving the read goes over NoDB's wire has no ES-mode equivalent" );
        createDefaultRootNode();
    }

    @Test
    void getPermissions_fetchesOnlyTheAccessControlSegment()
    {
        final Node node = createNode( CreateNodeParams.create()
                                           .parent( NodePath.ROOT )
                                           .name( "lazy-read-perm-" + UUID.randomUUID() )
                                           .data( uniqueData() )
                                           .build() );
        final NodeVersionKey key = versionKey( node );

        final CountingBlobStore counting = new CountingBlobStore( this.blobStore );
        final NodeVersionServiceImpl fresh = new NodeVersionServiceImpl( counting, new RepoConfiguration( Map.of() ) );

        fresh.getPermissions( key, InternalContext.from( ctxDefault() ) );

        assertEquals( 0, counting.count( NodeConstants.NODE_SEGMENT_LEVEL ), "getPermissions must not fetch node-data" );
        assertEquals( 0, counting.count( NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL ), "getPermissions must not fetch index-config" );
        assertEquals( 1, counting.count( NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL ), "getPermissions must fetch ACL exactly once" );
    }

    @Test
    void get_fetchesAllThreeSegmentsExactlyOnceEach()
    {
        final Node node = createNode( CreateNodeParams.create()
                                           .parent( NodePath.ROOT )
                                           .name( "lazy-read-full-" + UUID.randomUUID() )
                                           .data( uniqueData() )
                                           .build() );
        final NodeVersionKey key = versionKey( node );

        final CountingBlobStore counting = new CountingBlobStore( this.blobStore );
        final NodeVersionServiceImpl fresh = new NodeVersionServiceImpl( counting, new RepoConfiguration( Map.of() ) );

        fresh.get( key, InternalContext.from( ctxDefault() ) );

        assertEquals( 1, counting.count( NodeConstants.NODE_SEGMENT_LEVEL ), "get() must fetch node-data exactly once" );
        assertEquals( 1, counting.count( NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL ), "get() must fetch index-config exactly once" );
        assertEquals( 1, counting.count( NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL ), "get() must fetch ACL exactly once" );
    }

    private NodeVersionKey versionKey( final Node node )
    {
        return this.storageService.getVersion( node.getNodeVersionId(), InternalContext.from( ctxDefault() ) ).getNodeVersionKey();
    }

    private static PropertyTree uniqueData()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "unique", UUID.randomUUID().toString() );
        return data;
    }

    /** Counts {@link #getRecord} calls by blob-type level, delegating every call unchanged. */
    private static final class CountingBlobStore
        implements BlobStore
    {
        private static final SegmentLevel[] TRACKED =
            { NodeConstants.NODE_SEGMENT_LEVEL, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL };

        private final BlobStore delegate;

        private final Map<SegmentLevel, AtomicInteger> countsByLevel = new ConcurrentHashMap<>();

        CountingBlobStore( final BlobStore delegate )
        {
            this.delegate = delegate;
        }

        int count( final SegmentLevel blobType )
        {
            final AtomicInteger counter = countsByLevel.get( blobType );
            return counter == null ? 0 : counter.get();
        }

        @Override
        public BlobRecord getRecord( final Segment segment, final BlobKey key )
            throws BlobStoreException
        {
            for ( final SegmentLevel level : TRACKED )
            {
                if ( RepositorySegmentUtils.hasBlobTypeLevel( segment, level ) )
                {
                    countsByLevel.computeIfAbsent( level, l -> new AtomicInteger() ).incrementAndGet();
                    break;
                }
            }
            return delegate.getRecord( segment, key );
        }

        @Override
        public BlobRecord addRecord( final Segment segment, final ByteSource in )
            throws BlobStoreException
        {
            return delegate.addRecord( segment, in );
        }

        @Override
        public BlobRecord addRecord( final Segment segment, final BlobRecord record )
            throws BlobStoreException
        {
            return delegate.addRecord( segment, record );
        }

        @Override
        public void removeRecord( final Segment segment, final BlobKey key )
            throws BlobStoreException
        {
            delegate.removeRecord( segment, key );
        }

        @Override
        public Stream<BlobRecord> list( final Segment segment )
        {
            return delegate.list( segment );
        }

        @Override
        public Stream<Segment> listSegments()
        {
            return delegate.listSegments();
        }

        @Override
        public void deleteSegment( final Segment segment )
        {
            delegate.deleteSegment( segment );
        }
    }
}
