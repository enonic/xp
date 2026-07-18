package com.enonic.xp.storage.nodb;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.VersionRecord;

/**
 * Proto &lt;-&gt; storage-SPI record conversions. Two shapes need translation notes beyond
 * a mechanical field copy:
 * <ul>
 *   <li>{@code proto.BranchEntry} mirrors {@code engine.model.BranchEntryRecord}, which is
 *   deliberately narrower than {@code spi.BranchEntryRecord}: it carries no
 *   {@code nodeDataHash}/{@code indexConfigHash}/{@code aclHash} (only {@code node_version}
 *   stores those). {@link #toSpiBranchEntry} therefore takes the joined {@code Version} as
 *   a second argument -- callers ({@link NodbNodeStore}) fetch it via a follow-up
 *   {@code GetVersion(version_id)} call. This is a real extra round trip per branch-entry
 *   read in Phase 1 (no batched join), a known and documented cost, not an oversight -- see
 *   {@code nodb/engine/.../model/BranchEntryRecord.java}'s own javadoc, which anticipated
 *   exactly this client-side join.</li>
 *   <li>{@code proto.Version.attributes} is string-only; see {@link AttributeCodec}.</li>
 *   <li>{@code proto.Commit}'s {@code message}/{@code committer} are plain proto3 strings
 *   (no field-presence tracking in this schema -- no {@code optional} keyword used), so an
 *   empty wire string is treated as the SPI's {@code null} and vice versa.</li>
 * </ul>
 */
final class RecordMapper
{
    private RecordMapper()
    {
    }

    static com.enonic.nodb.proto.v1.BranchEntry toProtoBranchEntry( final Branch branch, final BranchEntryRecord record )
    {
        return com.enonic.nodb.proto.v1.BranchEntry.newBuilder()
            .setBranch( branch.getValue() )
            .setNodeId( record.nodeId() )
            .setVersionId( record.versionId() )
            .setNodePath( record.nodePath() )
            .setTimestampMillis( record.timestamp().toEpochMilli() )
            .build();
    }

    /**
     * @param version the {@code node_version} row for {@code entry.getVersionId()},
     *                already fetched by the caller -- see class javadoc.
     */
    static BranchEntryRecord toSpiBranchEntry( final com.enonic.nodb.proto.v1.BranchEntry entry,
                                                final com.enonic.nodb.proto.v1.Version version )
    {
        return new BranchEntryRecord( entry.getNodeId(), entry.getNodePath(), entry.getVersionId(), version.getNodeDataHash(),
                                       emptyToNull( version.getIndexConfigHash() ), emptyToNull( version.getAclHash() ),
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
