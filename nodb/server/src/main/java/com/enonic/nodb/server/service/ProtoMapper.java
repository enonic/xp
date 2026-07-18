package com.enonic.nodb.server.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;

import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.Commit;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.Version;

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
