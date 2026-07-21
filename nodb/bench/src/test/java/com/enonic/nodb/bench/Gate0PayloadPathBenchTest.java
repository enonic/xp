package com.enonic.nodb.bench;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.client.NodbClient;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.StoreBranchEntryRequest;
import com.enonic.nodb.proto.v1.StoreVersionRequest;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PHASE-3 GATE-0 SCRATCH (BUILD-PHASE-3.md "Node payloads into NoDB" work order, Gate 0
 * deliverable 2) -- a THROWAWAY micro-bench, not a regression gate for any slice/phase and
 * not exercised by {@code ../gradlew build}'s default test task selection beyond whatever
 * JUnit auto-discovers. Its numbers get transcribed into
 * {@code nodb/BUILD-PHASE-3.md}'s "Gate 0 results" section; this class can be deleted once
 * that is done -- nothing downstream depends on it existing.
 *
 * <p>Compares the two node-payload write-path shapes Gate 0 must decide between
 * (BUILD-PHASE-3.md Gate 0(b), DESIGN.md §2/§2.1):
 * <ul>
 *   <li><b>Path A (decorator shape)</b> -- extending the Phase-2 {@code
 *   NodbBinaryBlobStore} pattern (core-storage-nodb-client) to the node-data/index-config/
 *   ACL segments: each {@code BlobStore.addRecord} becomes a {@code PutPayload} RPC, and
 *   the version/branch-entry rows that {@code NodeStorageServiceImpl.store} (core-repo)
 *   already writes right afterwards -- SAME sequence as today, source-verified in Gate 0's
 *   call-site inventory -- become 2 more separate RPCs ({@code StoreVersion}, {@code
 *   StoreBranchEntry}). 5 sequential RPCs per save. Modelled here with a raw {@code
 *   NodeStoreGrpc} stub since {@link NodbClient}'s thin surface does not expose {@code
 *   storeVersion}/{@code storeBranchEntry} (see {@link BenchEnvironment#port()}'s javadoc).
 *   <li><b>Path B (WriteBatch shape)</b> -- ONE {@code WriteBatch} RPC carries the version +
 *   branch entry + the node-data payload inline; index-config/ACL are referenced by hash
 *   only, with NO accompanying {@code PayloadRef} at all once their hash is already known-
 *   good -- {@code engine.store.WriteService.write} only validates hashes that actually
 *   appear in the request's {@code payloads} list, never a version's own {@code
 *   indexConfigHash}/{@code aclHash} fields, so a steady-state save that reuses a
 *   previously-stored index-config/ACL hash need not resend those bytes at all. 1 RPC per
 *   save.
 * </ul>
 * Node-data is ~2KB unique random bytes per save on both paths (matches
 * {@link BenchRunner}'s own node-data sizing rationale: it is the part that actually varies
 * per node in XP). Index-config/ACL bytes are IDENTICAL across every save on both paths
 * (mirrors real XP: a handful of distinct index-config/ACL blobs shared across thousands of
 * nodes under a content type) -- on path A this means the 2nd and 3rd {@code PutPayload}
 * calls hit server-side dedup (Postgres {@code INSERT .. ON CONFLICT DO NOTHING}) every
 * time; on path B it means no bytes for those two segments cross the wire at all after the
 * one-time priming write below.
 *
 * <p>Both paths run against the SAME real loopback {@link NodbServer} + testcontainers
 * {@code postgres:17} as the rest of {@code nodb/bench} ({@link BenchEnvironment}) -- this
 * is a LOOPBACK-FLOOR number (localhost TCP, no real network hop). A real network would
 * multiply each RPC's cost by one round-trip time, which widens path A's disadvantage
 * proportionally to RTT (4 extra RPCs vs 0), since path B's entire save is one round trip
 * regardless of RTT.
 */
class Gate0PayloadPathBenchTest
{
    private static final Logger LOG = LoggerFactory.getLogger( Gate0PayloadPathBenchTest.class );

    private static final int WARMUP_OPS = 200;

    private static final int MEASURED_OPS = 2000;

    private static final String BRANCH = "master";

    @Test
    void pathAVsPathB()
        throws Exception
    {
        try (BenchEnvironment env = BenchEnvironment.start())
        {
            NodbClient client = env.client();
            String repoId = env.repoId();

            ManagedChannel rawChannel = ManagedChannelBuilder.forAddress( "localhost", env.port() ).usePlaintext().build();
            try
            {
                NodeStoreGrpc.NodeStoreBlockingStub rawStub =
                    NodeStoreGrpc.newBlockingStub( rawChannel ).withCallCredentials( bearerToken( env.runtimeToken() ) );

                Random random = new Random( 20260721L );

                // Shared index-config/ACL bytes, primed once via PutPayload so BOTH paths
                // start from "hash already known-good" steady state (see class javadoc).
                byte[] indexConfigBytes = "gate0-index-config-fixture".getBytes( StandardCharsets.UTF_8 );
                byte[] aclBytes = "gate0-acl-fixture".getBytes( StandardCharsets.UTF_8 );
                String indexConfigHash =
                    client.putPayload( PutPayloadRequest.newBuilder().setBytes( ByteString.copyFrom( indexConfigBytes ) ).build() )
                        .getHash();
                String aclHash =
                    client.putPayload( PutPayloadRequest.newBuilder().setBytes( ByteString.copyFrom( aclBytes ) ).build() ).getHash();

                LatencyStats pathA = measure( "Path A (decorator: 3xPutPayload+StoreVersion+StoreBranchEntry, 5 RPCs/save)",
                                               () -> saveViaPathA( rawStub, client, repoId, random, indexConfigBytes, aclBytes ) );
                LatencyStats pathB = measure( "Path B (WriteBatch: 1 RPC/save)",
                                               () -> saveViaPathB( client, repoId, random, indexConfigHash, aclHash ) );

                assertEquals( MEASURED_OPS, pathA.count() );
                assertEquals( MEASURED_OPS, pathB.count() );
                assertTrue( pathA.p50Micros() > 0 );
                assertTrue( pathB.p50Micros() > 0 );

                double p50DeltaUs = pathA.p50Micros() - pathB.p50Micros();
                double p95DeltaUs = pathA.p95Micros() - pathB.p95Micros();

                LOG.info( "=== Gate 0 payload-path bench (loopback NodbServer + testcontainers postgres:17) ===" );
                logStats( pathA );
                logStats( pathB );
                LOG.info( "p50 delta (A-B): {} us ({}% of A)", String.format( "%.1f", p50DeltaUs ),
                           String.format( "%.1f", 100.0 * p50DeltaUs / pathA.p50Micros() ) );
                LOG.info( "p95 delta (A-B): {} us ({}% of A)", String.format( "%.1f", p95DeltaUs ),
                           String.format( "%.1f", 100.0 * p95DeltaUs / pathA.p95Micros() ) );
            }
            finally
            {
                rawChannel.shutdown();
                if ( !rawChannel.awaitTermination( 5, TimeUnit.SECONDS ) )
                {
                    rawChannel.shutdownNow();
                }
            }
        }
    }

    private static void logStats( LatencyStats stats )
    {
        LOG.info( "{}: p50={}us p95={}us p99={}us mean={}us n={}", stats.operation(), stats.p50Micros(), stats.p95Micros(),
                   stats.p99Micros(), stats.meanMicros(), stats.count() );
    }

    /** Path A: 3x PutPayload (node-data unique, index-config+acl repeated) + StoreVersion + StoreBranchEntry. 5 sequential RPCs. */
    private static void saveViaPathA( NodeStoreGrpc.NodeStoreBlockingStub rawStub, NodbClient client, String repoId, Random random,
                                       byte[] indexConfigBytes, byte[] aclBytes )
    {
        String nodeId = UUID.randomUUID().toString();
        String versionId = UUID.randomUUID().toString();
        String path = "/gate0-a/" + nodeId;
        byte[] nodeData = randomBytes( random, 1800, 2200 );
        long nowMillis = Instant.now().toEpochMilli();

        String nodeDataHash =
            client.putPayload( PutPayloadRequest.newBuilder().setBytes( ByteString.copyFrom( nodeData ) ).build() ).getHash();
        String indexConfigHash =
            client.putPayload( PutPayloadRequest.newBuilder().setBytes( ByteString.copyFrom( indexConfigBytes ) ).build() ).getHash();
        String aclHash = client.putPayload( PutPayloadRequest.newBuilder().setBytes( ByteString.copyFrom( aclBytes ) ).build() ).getHash();

        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( nodeId )
            .setNodePath( path )
            .setTimestampMillis( nowMillis )
            .setNodeDataHash( nodeDataHash )
            .setIndexConfigHash( indexConfigHash )
            .setAclHash( aclHash )
            .build();
        rawStub.storeVersion( StoreVersionRequest.newBuilder().setRepoId( repoId ).setVersion( version ).build() );

        BranchEntry entry = BranchEntry.newBuilder()
            .setBranch( BRANCH )
            .setNodeId( nodeId )
            .setVersionId( versionId )
            .setNodePath( path )
            .setTimestampMillis( nowMillis )
            .build();
        rawStub.storeBranchEntry( StoreBranchEntryRequest.newBuilder().setRepoId( repoId ).setEntry( entry ).build() );
    }

    /** Path B: ONE WriteBatch carrying version + branch entry + inline node-data; index-config/acl referenced by hash only. */
    private static void saveViaPathB( NodbClient client, String repoId, Random random, String indexConfigHash, String aclHash )
    {
        String nodeId = UUID.randomUUID().toString();
        String versionId = UUID.randomUUID().toString();
        String path = "/gate0-b/" + nodeId;
        byte[] nodeData = randomBytes( random, 1800, 2200 );
        String nodeDataHash = Sha256.hashOf( nodeData );
        long nowMillis = Instant.now().toEpochMilli();

        Version version = Version.newBuilder()
            .setVersionId( versionId )
            .setNodeId( nodeId )
            .setNodePath( path )
            .setTimestampMillis( nowMillis )
            .setNodeDataHash( nodeDataHash )
            .setIndexConfigHash( indexConfigHash )
            .setAclHash( aclHash )
            .build();
        BranchEntry entry = BranchEntry.newBuilder()
            .setBranch( BRANCH )
            .setNodeId( nodeId )
            .setVersionId( versionId )
            .setNodePath( path )
            .setTimestampMillis( nowMillis )
            .build();
        WriteBatchRequest request = WriteBatchRequest.newBuilder()
            .setRepoId( repoId )
            .addPayloads( PayloadRef.newBuilder().setInline( ByteString.copyFrom( nodeData ) ).build() )
            .addVersions( version )
            .addBranchEntries( entry )
            .build();
        WriteBatchResponse response = client.writeBatch( request );
        if ( !response.getNeedPayloadList().isEmpty() )
        {
            throw new IllegalStateException( "Unexpected NEED_PAYLOAD in Gate-0 bench: " + response.getNeedPayloadList() );
        }
    }

    @FunctionalInterface
    private interface Op
    {
        void run();
    }

    private static LatencyStats measure( String name, Op op )
    {
        int total = WARMUP_OPS + MEASURED_OPS;
        long[] samples = new long[MEASURED_OPS];
        for ( int i = 0; i < total; i++ )
        {
            long start = System.nanoTime();
            op.run();
            long elapsed = System.nanoTime() - start;
            if ( i >= WARMUP_OPS )
            {
                samples[i - WARMUP_OPS] = elapsed;
            }
        }
        return LatencyStats.of( name, samples );
    }

    private static byte[] randomBytes( Random random, int minSize, int maxSize )
    {
        int size = minSize + random.nextInt( maxSize - minSize + 1 );
        byte[] bytes = new byte[size];
        random.nextBytes( bytes );
        return bytes;
    }

    private static CallCredentials bearerToken( String token )
    {
        Metadata.Key<String> authorizationKey = Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER );
        String headerValue = "Bearer " + token;
        return new CallCredentials()
        {
            @Override
            public void applyRequestMetadata( RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier )
            {
                Metadata headers = new Metadata();
                headers.put( authorizationKey, headerValue );
                applier.apply( headers );
            }
        };
    }
}
