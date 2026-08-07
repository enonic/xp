package com.enonic.nodb.bench;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.ByteString;

import com.enonic.nodb.client.NodbClient;
import com.enonic.nodb.engine.search.OpenSearchClient;
import com.enonic.nodb.proto.v1.AwaitRefreshRequest;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.GetBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetChildrenRequest;
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.IndexAck;
import com.enonic.nodb.proto.v1.IndexDoc;
import com.enonic.nodb.proto.v1.IndexDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexField;
import com.enonic.nodb.proto.v1.IndexValue;
import com.enonic.nodb.proto.v1.NodeSearchGrpc;
import com.enonic.nodb.proto.v1.PayloadRef;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.SearchRequest;
import com.enonic.nodb.proto.v1.SearchSourceRef;
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

    private static final String ADMIN_PRINCIPAL = "role:system.admin";

    private static final int CATEGORY_COUNT = 20;

    private static final int TITLE_WORDS = 6;

    private static final int BODY_WORDS = 150;

    private static final String[] VOCABULARY =
        { "north", "south", "harbour", "mountain", "river", "forest", "island", "bridge", "castle", "garden", "winter", "summer",
            "autumn", "spring", "market", "village", "capital", "coast", "valley", "glacier", "fjord", "lighthouse", "museum",
            "festival", "concert", "gallery", "library", "station", "airport", "ferry", "railway", "highway", "tunnel", "square",
            "cathedral", "fortress", "monument", "meadow", "orchard", "vineyard", "brewery", "bakery", "workshop", "studio",
            "theatre", "cinema", "stadium", "arena", "campus", "laboratory", "observatory", "archive", "harvest", "voyage",
            "journey", "expedition", "discovery", "heritage", "tradition", "culture", "history", "nature", "wildlife", "landscape" };

    private BenchRunner()
    {
    }

    static BenchResult run( BenchEnvironment env, BenchConfig config )
    {
        NodbClient client = env.client();
        String repoId = env.repoId();
        NodeSearchGrpc.NodeSearchBlockingStub search = env.nodeSearch();
        Random random = new Random( 42 );

        String[] sharedIndexHashes = storeSharedBlobs( client, random, config.sharedBlobVariants(), "index-config-" );
        String[] sharedAclHashes = storeSharedBlobs( client, random, config.sharedBlobVariants(), "acl-" );

        List<String> nodeIds = new ArrayList<>( config.nodeCount() );
        List<String> nodePaths = new ArrayList<>( config.nodeCount() );
        List<String> versionIds = new ArrayList<>( config.nodeCount() );
        List<String> folderPaths = new ArrayList<>( config.folderCount() );

        long seedStart = System.nanoTime();
        long written = 0;
        long searchDocsShipped = 0;
        long lastOutboxSeq = 0;
        for ( int folder = 0; folder < config.folderCount(); folder++ )
        {
            String folderPath = String.format( "/folder-%03d", folder );
            folderPaths.add( folderPath );

            List<PayloadRef> payloads = new ArrayList<>();
            List<Version> versions = new ArrayList<>();
            List<BranchEntry> branchEntries = new ArrayList<>();
            List<IndexDoc> searchDocs = new ArrayList<>();

            addNode( folderPath, random, sharedIndexHashes, sharedAclHashes, payloads, versions, branchEntries, nodeIds, nodePaths,
                     versionIds );
            searchDocs.add( searchDocument( nodeIds.get( nodeIds.size() - 1 ), random ) );
            for ( int child = 0; child < config.childrenPerFolder(); child++ )
            {
                String childPath = folderPath + String.format( "/child-%06d", child );
                addNode( childPath, random, sharedIndexHashes, sharedAclHashes, payloads, versions, branchEntries, nodeIds, nodePaths,
                         versionIds );
                searchDocs.add( searchDocument( nodeIds.get( nodeIds.size() - 1 ), random ) );
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

                IndexAck ack = search.indexDocuments( IndexDocumentsRequest.newBuilder()
                                                          .setRepoId( repoId )
                                                          .setBranch( BRANCH )
                                                          .addAllDocuments( searchDocs.subList( from, to ) )
                                                          .build() );
                lastOutboxSeq = ack.getOutboxSeq();
                searchDocsShipped += ( to - from );
            }
        }
        long seedWallMillis = ( System.nanoTime() - seedStart ) / 1_000_000L;

        long drainStart = System.nanoTime();
        search.awaitRefresh( AwaitRefreshRequest.newBuilder()
                                 .setSeq( lastOutboxSeq )
                                 .addRepoIds( repoId )
                                 .setTimeoutMillis( 600_000 )
                                 .build() );
        long indexDrainMillis = ( System.nanoTime() - drainStart ) / 1_000_000L;

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

        opStats.add( measure( "search: term(data.category)", config, () -> search.search(
            searchRequest( repoId ).setQuery( "{\"term\":{\"field\":\"data.category\",\"value\":\"" + randomCategory( random ) + "\"}}" )
                .build() ) ) );

        opStats.add( measure( "search: fulltext(data.title,data.body)", config, () -> search.search( searchRequest( repoId ).setQuery(
            "{\"fulltext\":{\"fields\":[\"data.title\",\"data.body\"],\"query\":\"" + randomWord( random ) + " " + randomWord( random ) +
                "\",\"operator\":\"OR\"}}" ).build() ) ) );

        opStats.add( measure( "search: aggregation terms(data.category), size 0", config, () -> search.search(
            searchRequest( repoId ).setSize( 0 )
                .setQuery( "{\"matchAll\":{}}" )
                .setAggregations( "{\"byCategory\":{\"terms\":{\"field\":\"data.category\",\"size\":" + CATEGORY_COUNT +
                                      ",\"order\":{\"type\":\"DOC_COUNT\",\"direction\":\"DESC\"}}}}" )
                .build() ) ) );

        opStats.add( measure( "search: fulltext + highlight(data.body) via NoDB (plain)", config, () -> search.search(
            searchRequest( repoId ).setQuery(
                    "{\"fulltext\":{\"fields\":[\"data.body\"],\"query\":\"" + randomWord( random ) + "\",\"operator\":\"OR\"}}" )
                .setHighlight( "{\"settings\":{},\"properties\":[{\"name\":\"data.body\"}]}" )
                .build() ) ) );

        opStats.add( measure( "indexDocuments(1 doc)", config, () -> search.indexDocuments( IndexDocumentsRequest.newBuilder()
                                                                                                .setRepoId( repoId )
                                                                                                .setBranch( BRANCH )
                                                                                                .addDocuments( searchDocument(
                                                                                                    UUID.randomUUID().toString(),
                                                                                                    random ) )
                                                                                                .build() ) ) );

        opStats.add( measure( "refresh(SEARCH): indexDocuments(1 doc) + awaitRefresh", config, () -> {
            IndexAck ack = search.indexDocuments( IndexDocumentsRequest.newBuilder()
                                                      .setRepoId( repoId )
                                                      .setBranch( BRANCH )
                                                      .addDocuments( searchDocument( UUID.randomUUID().toString(), random ) )
                                                      .build() );
            search.awaitRefresh( AwaitRefreshRequest.newBuilder()
                                     .setSeq( ack.getOutboxSeq() )
                                     .addRepoIds( repoId )
                                     .setTimeoutMillis( 30_000 )
                                     .build() );
        } ) );

        opStats.add( measure( "refresh(SEARCH): awaitRefresh, nothing pending", config, () -> search.awaitRefresh(
            AwaitRefreshRequest.newBuilder().setSeq( 0 ).addRepoIds( repoId ).setTimeoutMillis( 30_000 ).build() ) ) );

        List<LatencyStats> highlightStats = new ArrayList<>();
        for ( String type : List.of( "plain", "unified" ) )
        {
            Random highlightRandom = new Random( 7 );
            highlightStats.add( measure( "highlight type=" + type + " (direct OpenSearch, match on data.body._fulltext)", config,
                                          () -> env.openSearchClient()
                                              .search( env.searchAlias(), highlightBody( randomWord( highlightRandom ), type ) ) ) );
        }

        return new BenchResult( config, written, seedWallMillis, searchDocsShipped, indexDrainMillis, opStats, highlightStats );
    }

    private static SearchRequest.Builder searchRequest( String repoId )
    {
        return SearchRequest.newBuilder()
            .setFormatVersion( 1 )
            .addSources( SearchSourceRef.newBuilder().setRepoId( repoId ).setBranch( BRANCH ).addPrincipals( ADMIN_PRINCIPAL ) )
            .setSize( 10 );
    }

    private static ObjectNode highlightBody( String word, String type )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.put( "size", 10 );
        body.putObject( "query" ).putObject( "match" ).putObject( "data.body._fulltext" ).put( "query", word );
        ObjectNode highlight = body.putObject( "highlight" );
        highlight.put( "type", type );
        ObjectNode fields = highlight.putObject( "fields" );
        for ( String field : List.of( "data.body._text", "data.body._fulltext", "data.body._ngram" ) )
        {
            fields.putObject( field ).put( "require_field_match", false );
        }
        return body;
    }

    private static IndexDoc searchDocument( String nodeId, Random random )
    {
        String title = randomWords( random, TITLE_WORDS );
        String body = randomWords( random, BODY_WORDS );
        String category = randomCategory( random );
        long nowMillis = Instant.now().toEpochMilli();

        return IndexDoc.newBuilder()
            .setId( nodeId )
            .addFields( textField( "data.title", title ) )
            .addFields( textField( "data.title._analyzed", title ) )
            .addFields( textField( "data.body", body ) )
            .addFields( textField( "data.body._analyzed", body ) )
            .addFields( textField( "data.category", category ) )
            .addFields( IndexField.newBuilder()
                            .setName( "data.price._number" )
                            .addValues( IndexValue.newBuilder().setDoubleValue( 10 + random.nextInt( 990 ) ) ) )
            .addFields( IndexField.newBuilder()
                            .setName( "_ts._datetime" )
                            .addValues( IndexValue.newBuilder().setInstantMillis( nowMillis ) ) )
            .addFields( textField( "_permissions.read", "role:system.everyone" ) )
            .build();
    }

    private static IndexField textField( String name, String value )
    {
        return IndexField.newBuilder().setName( name ).addValues( IndexValue.newBuilder().setStringValue( value ) ).build();
    }

    private static String randomWord( Random random )
    {
        return VOCABULARY[random.nextInt( VOCABULARY.length )];
    }

    private static String randomWords( Random random, int count )
    {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < count; i++ )
        {
            if ( i > 0 )
            {
                sb.append( ' ' );
            }
            sb.append( randomWord( random ) );
        }
        return sb.toString();
    }

    private static String randomCategory( Random random )
    {
        return String.format( "category-%02d", random.nextInt( CATEGORY_COUNT ) );
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
