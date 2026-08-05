package com.enonic.nodb.bench;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.protobuf.ByteString;

import com.enonic.nodb.client.NodbClient;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.GetBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetChildrenRequest;
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;

/**
 * Seeds a node tree and measures per-operation latency over a real {@link NodbClient}
 * (BUILD-SLICE-1.md step 6, DESIGN.md §10 risk #2). Seed shape and sizing come from {@link
 * BenchConfig}; see its javadoc for the tree layout.
 *
 * <p>Index-config/ACL payload dedup: {@code sharedBlobVariants} index-config blobs and the
 * same number of ACL blobs are stored ONCE each (via {@code PutPayload}) before seeding
 * starts; every node's {@code Version} references one of those hashes directly, mirroring
 * real XP (most nodes under a content-type share identical index config/ACLs) rather than
 * writing ~100k identical blobs. Node DATA is unique per node (random bytes, effectively
 * guaranteed distinct across the run — see {@link Sha256}), matching the fact that node
 * data is the part that actually varies per node in XP.
 */
final class BenchRunner
{
    private static final String BRANCH = "master";

    private BenchRunner()
    {
    }

    static BenchResult run( NodbClient client, String repoId, BenchConfig config )
    {
        Random random = new Random( 42 );

        String[] sharedIndexHashes = storeSharedBlobs( client, random, config.sharedBlobVariants(), "index-config-" );
        String[] sharedAclHashes = storeSharedBlobs( client, random, config.sharedBlobVariants(), "acl-" );

        List<String> nodeIds = new ArrayList<>( config.nodeCount() );
        List<String> nodePaths = new ArrayList<>( config.nodeCount() );
        List<String> versionIds = new ArrayList<>( config.nodeCount() );
        List<String> folderPaths = new ArrayList<>( config.folderCount() );

        long seedStart = System.nanoTime();
        long written = 0;
        for ( int folder = 0; folder < config.folderCount(); folder++ )
        {
            String folderPath = String.format( "/folder-%03d", folder );
            folderPaths.add( folderPath );

            List<PayloadRef> payloads = new ArrayList<>();
            List<Version> versions = new ArrayList<>();
            List<BranchEntry> branchEntries = new ArrayList<>();

            addNode( folderPath, random, sharedIndexHashes, sharedAclHashes, payloads, versions, branchEntries, nodeIds, nodePaths,
                     versionIds );
            for ( int child = 0; child < config.childrenPerFolder(); child++ )
            {
                String childPath = folderPath + String.format( "/child-%06d", child );
                addNode( childPath, random, sharedIndexHashes, sharedAclHashes, payloads, versions, branchEntries, nodeIds, nodePaths,
                         versionIds );
            }

            // Flush in writeBatchSize-sized chunks (the folder's node count matches
            // writeBatchSize exactly for both BenchConfig.full()/reduced(), so this is
            // normally a single call per folder; the chunking loop is defensive, not load-
            // bearing, in case that invariant ever changes).
            int total = versions.size();
            for ( int from = 0; from < total; from += config.writeBatchSize() )
            {
                int to = Math.min( from + config.writeBatchSize(), total );
                WriteBatchRequest request = WriteBatchRequest.newBuilder()
                    .setRepoId( repoId )
                    .addAllPayloads( payloads.subList( from, to ) )
                    .addAllVersions( versions.subList( from, to ) )
                    .addAllBranchEntries( branchEntries.subList( from, to ) )
                    .build();
                WriteBatchResponse response = client.writeBatch( request );
                if ( response.getNeedPayloadCount() > 0 )
                {
                    throw new IllegalStateException( "Unexpected NEED_PAYLOAD during seeding: " + response.getNeedPayloadList() );
                }
                written += ( to - from );
            }
        }
        long seedWallMillis = ( System.nanoTime() - seedStart ) / 1_000_000L;

        List<LatencyStats> opStats = new ArrayList<>();
        opStats.add( measure( "getBranchEntry(by node_id)", config, () -> {
            String nodeId = nodeIds.get( random.nextInt( nodeIds.size() ) );
            client.getBranchEntry(
                GetBranchEntryRequest.newBuilder().setRepoId( repoId ).setBranch( BRANCH ).setNodeId( nodeId ).build() );
        } ) );
        opStats.add( measure( "getBranchEntry(by node_path)", config, () -> {
            String path = nodePaths.get( random.nextInt( nodePaths.size() ) );
            client.getBranchEntry(
                GetBranchEntryRequest.newBuilder().setRepoId( repoId ).setBranch( BRANCH ).setNodePath( path ).build() );
        } ) );
        opStats.add( measure( "getChildren(page=" + config.childrenPageSize() + ")", config, () -> {
            String parentPath = folderPaths.get( random.nextInt( folderPaths.size() ) );
            var it = client.getChildren( GetChildrenRequest.newBuilder()
                                              .setRepoId( repoId )
                                              .setBranch( BRANCH )
                                              .setParentPath( parentPath )
                                              .setFrom( 0 )
                                              .setSize( config.childrenPageSize() )
                                              .build() );
            while ( it.hasNext() )
            {
                it.next();
            }
        } ) );
        opStats.add( measure( "getVersion", config, () -> {
            String versionId = versionIds.get( random.nextInt( versionIds.size() ) );
            client.getVersion( GetVersionRequest.newBuilder().setRepoId( repoId ).setVersionId( versionId ).build() );
        } ) );
        opStats.add( measure( "writeBatch(1 node)", config, () -> {
            List<PayloadRef> payloads = new ArrayList<>();
            List<Version> versions = new ArrayList<>();
            List<BranchEntry> branchEntries = new ArrayList<>();
            addNode( "/bench-write/" + UUID.randomUUID(), random, sharedIndexHashes, sharedAclHashes, payloads, versions, branchEntries,
                     new ArrayList<>(), new ArrayList<>(), new ArrayList<>() );
            client.writeBatch( WriteBatchRequest.newBuilder()
                                    .setRepoId( repoId )
                                    .addAllPayloads( payloads )
                                    .addAllVersions( versions )
                                    .addAllBranchEntries( branchEntries )
                                    .build() );
        } ) );

        return new BenchResult( config, written, seedWallMillis, opStats );
    }

    private static void addNode( String path, Random random, String[] sharedIndexHashes, String[] sharedAclHashes,
                                  List<PayloadRef> payloads, List<Version> versions, List<BranchEntry> branchEntries,
                                  List<String> nodeIds, List<String> nodePaths, List<String> versionIds )
    {
        String nodeId = UUID.randomUUID().toString();
        String versionId = UUID.randomUUID().toString();
        byte[] nodeData = randomBytes( random, 200, 800 );
        String nodeDataHash = Sha256.hashOf( nodeData );
        String indexHash = sharedIndexHashes[random.nextInt( sharedIndexHashes.length )];
        String aclHash = sharedAclHashes[random.nextInt( sharedAclHashes.length )];
        long nowMillis = Instant.now().toEpochMilli();

        payloads.add( PayloadRef.newBuilder().setInline( ByteString.copyFrom( nodeData ) ).build() );
        versions.add( Version.newBuilder()
                          .setVersionId( versionId )
                          .setNodeId( nodeId )
                          .setNodePath( path )
                          .setTimestampMillis( nowMillis )
                          .setNodeDataHash( nodeDataHash )
                          .setIndexConfigHash( indexHash )
                          .setAclHash( aclHash )
                          .build() );
        branchEntries.add( BranchEntry.newBuilder()
                                .setBranch( BRANCH )
                                .setNodeId( nodeId )
                                .setVersionId( versionId )
                                .setNodePath( path )
                                .setTimestampMillis( nowMillis )
                                .build() );

        nodeIds.add( nodeId );
        nodePaths.add( path );
        versionIds.add( versionId );
    }

    private static String[] storeSharedBlobs( NodbClient client, Random random, int count, String label )
    {
        String[] hashes = new String[count];
        for ( int i = 0; i < count; i++ )
        {
            byte[] bytes = ( label + i + "-" + randomSuffix( random ) ).getBytes( java.nio.charset.StandardCharsets.UTF_8 );
            hashes[i] = client.putPayload( PutPayloadRequest.newBuilder().setBytes( ByteString.copyFrom( bytes ) ).build() ).getHash();
        }
        return hashes;
    }

    private static String randomSuffix( Random random )
    {
        return Long.toHexString( random.nextLong() );
    }

    private static byte[] randomBytes( Random random, int minSize, int maxSize )
    {
        int size = minSize + random.nextInt( maxSize - minSize + 1 );
        byte[] bytes = new byte[size];
        random.nextBytes( bytes );
        return bytes;
    }

    @FunctionalInterface
    private interface Op
    {
        void run();
    }

    private static LatencyStats measure( String name, BenchConfig config, Op op )
    {
        int total = config.warmupOps() + config.measuredOps();
        long[] samples = new long[config.measuredOps()];
        for ( int i = 0; i < total; i++ )
        {
            long start = System.nanoTime();
            op.run();
            long elapsed = System.nanoTime() - start;
            if ( i >= config.warmupOps() )
            {
                samples[i - config.warmupOps()] = elapsed;
            }
        }
        return LatencyStats.of( name, samples );
    }
}
