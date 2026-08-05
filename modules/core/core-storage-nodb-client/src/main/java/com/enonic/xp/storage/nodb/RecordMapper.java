package com.enonic.xp.storage.nodb;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.VersionQuery;
import com.enonic.xp.storage.spi.VersionRecord;

/**
 * Proto &lt;-&gt; storage-SPI record conversions. Shapes needing translation notes beyond a
 * mechanical field copy:
 * <ul>
 *   <li>{@code proto.BranchEntry} now carries {@code node_data_hash}/{@code index_config_hash}/
 *   {@code acl_hash} directly (Phase 1 Gate C N+1 fix, BUILD-PHASE-1.md): the server joins
 *   {@code node_version} ON {@code (repo_key, version_id)} in the same query that reads
 *   {@code branch_entry}, so {@link #toSpiBranchEntry} maps the wire message straight across
 *   with no follow-up {@code GetVersion} call -- the extra round trip the Phase 1 Gate B
 *   client used to need per branch-entry read is gone.</li>
 *   <li>{@code proto.Version.attributes} is string-only; see {@link AttributeCodec}.</li>
 *   <li>{@code proto.Commit}'s {@code message}/{@code committer} are plain proto3 strings
 *   (no field-presence tracking in this schema -- no {@code optional} keyword used), so an
 *   empty wire string is treated as the SPI's {@code null} and vice versa. The same
 *   empty-string/null convention applies to {@code BranchEntry.index_config_hash}/
 *   {@code acl_hash} (nullable at the SPI boundary, same as {@code Version}'s own fields),
 *   while {@code node_data_hash} is always non-empty on reads (NOT NULL in node_version).</li>
 * </ul>
 */
final class RecordMapper
{
    private RecordMapper()
    {
    }

    static com.enonic.nodb.proto.v1.BranchEntry toProtoBranchEntry( final Branch branch, final BranchEntryRecord record )
    {
        final com.enonic.nodb.proto.v1.BranchEntry.Builder builder = com.enonic.nodb.proto.v1.BranchEntry.newBuilder()
            .setBranch( branch.getValue() )
            .setNodeId( record.nodeId() )
            .setVersionId( record.versionId() )
            .setNodePath( record.nodePath() )
            .setTimestampMillis( record.timestamp().toEpochMilli() )
            .setNodeDataHash( record.nodeDataHash() );
        // Carried for fidelity with the SPI record (the server ignores these on the write
        // path -- BranchStore.store()'s INSERT never touches them, only reads join them in).
        if ( record.indexConfigHash() != null )
        {
            builder.setIndexConfigHash( record.indexConfigHash() );
        }
        if ( record.aclHash() != null )
        {
            builder.setAclHash( record.aclHash() );
        }
        return builder.build();
    }

    static BranchEntryRecord toSpiBranchEntry( final com.enonic.nodb.proto.v1.BranchEntry entry )
    {
        return new BranchEntryRecord( entry.getNodeId(), entry.getNodePath(), entry.getVersionId(), entry.getNodeDataHash(),
                                       emptyToNull( entry.getIndexConfigHash() ), emptyToNull( entry.getAclHash() ),
                                       Instant.ofEpochMilli( entry.getTimestampMillis() ) );
    }

    static com.enonic.nodb.proto.v1.Version toProtoVersion( final VersionRecord record )
    {
        final com.enonic.nodb.proto.v1.Version.Builder builder = com.enonic.nodb.proto.v1.Version.newBuilder()
            .setVersionId( record.versionId() )
            .setNodeId( record.nodeId() )
            .setNodePath( record.nodePath() )
            .setTimestampMillis( record.timestamp().toEpochMilli() )
            .setNodeDataHash( record.nodeDataHash() )
            .addAllBinaryKeys( record.binaryKeys() )
            .putAllAttributes( AttributeCodec.encode( record.attributes() ) );
        if ( record.indexConfigHash() != null )
        {
            builder.setIndexConfigHash( record.indexConfigHash() );
        }
        if ( record.aclHash() != null )
        {
            builder.setAclHash( record.aclHash() );
        }
        if ( record.commitId() != null )
        {
            builder.setCommitId( record.commitId() );
        }
        return builder.build();
    }

    static VersionRecord toSpiVersion( final com.enonic.nodb.proto.v1.Version version )
    {
        return new VersionRecord( version.getVersionId(), version.getNodeId(), version.getNodePath(),
                                   Instant.ofEpochMilli( version.getTimestampMillis() ), version.getNodeDataHash(),
                                   emptyToNull( version.getIndexConfigHash() ), emptyToNull( version.getAclHash() ),
                                   List.copyOf( version.getBinaryKeysList() ), emptyToNull( version.getCommitId() ),
                                   AttributeCodec.decode( version.getAttributesMap() ) );
    }

    static com.enonic.nodb.proto.v1.Commit toProtoCommit( final CommitRecord record )
    {
        final com.enonic.nodb.proto.v1.Commit.Builder builder =
            com.enonic.nodb.proto.v1.Commit.newBuilder().setCommitId( record.commitId() ).setTimestampMillis(
                record.timestamp().toEpochMilli() );
        if ( record.message() != null )
        {
            builder.setMessage( record.message() );
        }
        if ( record.committer() != null )
        {
            builder.setCommitter( record.committer() );
        }
        return builder.build();
    }

    /**
     * SPI query -&gt; wire query (Phase 3.5 Gate A). {@code null} SPI fields stay at their
     * proto3 defaults (empty string / 0 / absent message), which the server reads as "no
     * predicate"; {@code from}/{@code size} pass through untranslated ({@code size} 0 =
     * count-only, -1 = all — the same convention at both layers). Returns a builder so the
     * caller can stamp the repo id on it (record mapping here never handles repo identity).
     */
    static com.enonic.nodb.proto.v1.FindVersionsRequest.Builder toProtoVersionQuery( final VersionQuery query )
    {
        final com.enonic.nodb.proto.v1.FindVersionsRequest.Builder builder = com.enonic.nodb.proto.v1.FindVersionsRequest.newBuilder()
            .setOrder( toProtoVersionOrder( query.order() ) )
            .setFrom( query.from() )
            .setSize( query.size() );
        if ( query.nodeId() != null )
        {
            builder.setNodeId( query.nodeId() );
        }
        if ( query.tsFloor() != null )
        {
            builder.setTsFloorMillis( query.tsFloor().toEpochMilli() );
        }
        if ( query.tsCeiling() != null )
        {
            builder.setTsCeilingMillis( query.tsCeiling().toEpochMilli() );
        }
        if ( query.versionIdAfter() != null )
        {
            builder.setVersionIdAfter( query.versionIdAfter() );
        }
        if ( query.blobKeyTerm() != null )
        {
            builder.setBlobKey( com.enonic.nodb.proto.v1.BlobKeyTerm.newBuilder()
                                    .setBlobKey( query.blobKeyTerm().blobKey() )
                                    .setField( toProtoBlobKeyField( query.blobKeyTerm().field() ) ) );
        }
        if ( query.cursor() != null )
        {
            builder.setCursor( com.enonic.nodb.proto.v1.VersionCursor.newBuilder()
                                   .setTsMillis( query.cursor().ts().toEpochMilli() )
                                   .setVersionId( query.cursor().versionId() ) );
        }
        return builder;
    }

    private static com.enonic.nodb.proto.v1.VersionOrder toProtoVersionOrder( final VersionQuery.Order order )
    {
        return switch ( order )
        {
            case UNORDERED -> com.enonic.nodb.proto.v1.VersionOrder.VERSION_ORDER_UNORDERED;
            case TS_DESC_ID_ASC -> com.enonic.nodb.proto.v1.VersionOrder.VERSION_ORDER_TS_DESC_ID_ASC;
            case ID_ASC -> com.enonic.nodb.proto.v1.VersionOrder.VERSION_ORDER_ID_ASC;
        };
    }

    private static com.enonic.nodb.proto.v1.BlobKeyField toProtoBlobKeyField( final VersionQuery.BlobKeyField field )
    {
        return switch ( field )
        {
            case BINARY_KEYS -> com.enonic.nodb.proto.v1.BlobKeyField.BLOB_KEY_FIELD_BINARY_KEYS;
            case NODE_DATA_HASH -> com.enonic.nodb.proto.v1.BlobKeyField.BLOB_KEY_FIELD_NODE_DATA_HASH;
        };
    }

    static CommitRecord toSpiCommit( final com.enonic.nodb.proto.v1.Commit commit )
    {
        return new CommitRecord( commit.getCommitId(), emptyToNull( commit.getMessage() ), emptyToNull( commit.getCommitter() ),
                                  Instant.ofEpochMilli( commit.getTimestampMillis() ) );
    }

    @Nullable
    private static String emptyToNull( final String value )
    {
        return value == null || value.isEmpty() ? null : value;
    }
}
