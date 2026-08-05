package com.enonic.nodb.server.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;

import com.enonic.nodb.proto.v1.BlobKeyField;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.Commit;
import com.enonic.nodb.proto.v1.FindVersionsRequest;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.VersionOrder;

/**
 * Proto &lt;-&gt; engine model conversions for the NodeStore RPCs. Kept in one place since
 * both {@link NodeStoreService} and (indirectly, via shared message shapes) any future
 * caller need the exact same mapping. Fully-qualifies engine model types throughout since
 * several (Version, Commit) share a simple name with their proto counterpart.
 */
final class ProtoMapper
{
    private ProtoMapper()
    {
    }

    static com.enonic.nodb.engine.model.VersionRecord toEngineVersion( Version v )
    {
        return new com.enonic.nodb.engine.model.VersionRecord( v.getVersionId(), v.getNodeId(), v.getNodePath(),
                                                                 Instant.ofEpochMilli( v.getTimestampMillis() ), v.getNodeDataHash(),
                                                                 v.getIndexConfigHash(), v.getAclHash(),
                                                                 List.copyOf( v.getBinaryKeysList() ),
                                                                 v.getCommitId().isEmpty() ? null : v.getCommitId(),
                                                                 Map.copyOf( v.getAttributesMap() ) );
    }

    static Version fromEngineVersion( com.enonic.nodb.engine.model.VersionRecord r )
    {
        Version.Builder builder = Version.newBuilder()
            .setVersionId( r.versionId() )
            .setNodeId( r.nodeId() )
            .setNodePath( r.nodePath() )
            .setTimestampMillis( r.timestamp().toEpochMilli() )
            .setNodeDataHash( r.nodeDataHash() )
            .setIndexConfigHash( r.indexConfigHash() )
            .setAclHash( r.aclHash() )
            .addAllBinaryKeys( r.binaryKeys() )
            .putAllAttributes( r.attributes() );
        if ( r.commitId() != null )
        {
            builder.setCommitId( r.commitId() );
        }
        return builder.build();
    }

    static com.enonic.nodb.engine.model.BranchEntryRecord toEngineBranchEntry( BranchEntry e )
    {
        return new com.enonic.nodb.engine.model.BranchEntryRecord( e.getBranch(), e.getNodeId(), e.getVersionId(), e.getNodePath(),
                                                                     Instant.ofEpochMilli( e.getTimestampMillis() ) );
    }

    static BranchEntry fromEngineBranchEntry( com.enonic.nodb.engine.model.BranchEntryRecord r )
    {
        BranchEntry.Builder builder = BranchEntry.newBuilder()
            .setBranch( r.branch() )
            .setNodeId( r.nodeId() )
            .setVersionId( r.versionId() )
            .setNodePath( r.nodePath() )
            .setTimestampMillis( r.timestamp().toEpochMilli() );
        // Populated on every read (BranchStore's JOINED_SELECT always fetches them -- the
        // FK guarantees a matching node_version row); null only for records built via the
        // write-side 5-arg BranchEntryRecord constructor, which never reaches this method.
        if ( r.nodeDataHash() != null )
        {
            builder.setNodeDataHash( r.nodeDataHash() );
        }
        if ( r.indexConfigHash() != null )
        {
            builder.setIndexConfigHash( r.indexConfigHash() );
        }
        if ( r.aclHash() != null )
        {
            builder.setAclHash( r.aclHash() );
        }
        return builder.build();
    }

    static com.enonic.nodb.engine.model.CommitRecord toEngineCommit( Commit c )
    {
        return new com.enonic.nodb.engine.model.CommitRecord( c.getCommitId(), c.getMessage(), c.getCommitter(),
                                                                Instant.ofEpochMilli( c.getTimestampMillis() ) );
    }

    static Commit fromEngineCommit( com.enonic.nodb.engine.model.CommitRecord r )
    {
        Commit.Builder builder = Commit.newBuilder().setCommitId( r.commitId() ).setTimestampMillis( r.timestamp().toEpochMilli() );
        if ( r.message() != null )
        {
            builder.setMessage( r.message() );
        }
        if ( r.committer() != null )
        {
            builder.setCommitter( r.committer() );
        }
        return builder.build();
    }

    /**
     * Wire query -&gt; engine query (Phase 3.5 Gate A). proto3 defaults (empty string, 0,
     * absent message) map to "no predicate" ({@code null}), per FindVersionsRequest's own
     * field comments; {@code from}/{@code size} pass through untranslated ({@code size} 0 =
     * count-only, -1 = all — the same convention at both layers).
     */
    static com.enonic.nodb.engine.model.VersionQuery toEngineVersionQuery( FindVersionsRequest request )
    {
        return new com.enonic.nodb.engine.model.VersionQuery( request.getNodeId().isEmpty() ? null : request.getNodeId(),
                                                                request.getTsFloorMillis() == 0
                                                                    ? null
                                                                    : Instant.ofEpochMilli( request.getTsFloorMillis() ),
                                                                request.getTsCeilingMillis() == 0
                                                                    ? null
                                                                    : Instant.ofEpochMilli( request.getTsCeilingMillis() ),
                                                                request.getVersionIdAfter().isEmpty()
                                                                    ? null
                                                                    : request.getVersionIdAfter(),
                                                                request.hasBlobKey()
                                                                    ? new com.enonic.nodb.engine.model.VersionQuery.BlobKeyTerm(
                                                                        request.getBlobKey().getBlobKey(),
                                                                        toEngineBlobKeyField( request.getBlobKey().getField() ) )
                                                                    : null, request.hasCursor()
                                                                    ? new com.enonic.nodb.engine.model.VersionQuery.Cursor(
                                                                        Instant.ofEpochMilli( request.getCursor().getTsMillis() ),
                                                                        request.getCursor().getVersionId() )
                                                                    : null, toEngineVersionOrder( request.getOrder() ),
                                                                request.getFrom(), request.getSize() );
    }

    private static com.enonic.nodb.engine.model.VersionQuery.BlobKeyField toEngineBlobKeyField( BlobKeyField field )
    {
        return switch ( field )
        {
            case BLOB_KEY_FIELD_BINARY_KEYS -> com.enonic.nodb.engine.model.VersionQuery.BlobKeyField.BINARY_KEYS;
            case BLOB_KEY_FIELD_NODE_DATA_HASH -> com.enonic.nodb.engine.model.VersionQuery.BlobKeyField.NODE_DATA_HASH;
            case UNRECOGNIZED -> throw new IllegalArgumentException( "Unrecognized blob key field" );
        };
    }

    private static com.enonic.nodb.engine.model.VersionQuery.Order toEngineVersionOrder( VersionOrder order )
    {
        return switch ( order )
        {
            case VERSION_ORDER_UNORDERED -> com.enonic.nodb.engine.model.VersionQuery.Order.UNORDERED;
            case VERSION_ORDER_TS_DESC_ID_ASC -> com.enonic.nodb.engine.model.VersionQuery.Order.TS_DESC_ID_ASC;
            case VERSION_ORDER_ID_ASC -> com.enonic.nodb.engine.model.VersionQuery.Order.ID_ASC;
            case UNRECOGNIZED -> throw new IllegalArgumentException( "Unrecognized version order" );
        };
    }

    static com.enonic.nodb.engine.store.PayloadRef toEnginePayloadRef( com.enonic.nodb.proto.v1.PayloadRef ref )
    {
        return switch ( ref.getRefCase() )
        {
            case INLINE -> new com.enonic.nodb.engine.store.PayloadRef.Inline( ref.getInline().toByteArray() );
            case HASH -> new com.enonic.nodb.engine.store.PayloadRef.HashOnly( ref.getHash() );
            case REF_NOT_SET -> throw new IllegalArgumentException( "PayloadRef must set either inline or hash" );
        };
    }

    static Payload fromEnginePayload( String hash, byte[] bytes )
    {
        return Payload.newBuilder().setHash( hash ).setBytes( ByteString.copyFrom( bytes ) ).build();
    }
}
