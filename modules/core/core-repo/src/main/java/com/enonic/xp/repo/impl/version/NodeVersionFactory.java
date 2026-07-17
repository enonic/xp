package com.enonic.xp.repo.impl.version;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.storage.spi.VersionRecord;
import com.enonic.xp.util.GenericValue;

public class NodeVersionFactory
{
    public static NodeVersion create( final ReturnValues values )
    {
        final String versionId = values.getStringValue( VersionIndexPath.VERSION_ID );
        final String nodeBlobKey = values.getStringValue( VersionIndexPath.NODE_BLOB_KEY );
        final String indexConfigBlobKey = values.getStringValue( VersionIndexPath.INDEX_CONFIG_BLOB_KEY );
        final String accessControlBlobKey = values.getStringValue( VersionIndexPath.ACCESS_CONTROL_BLOB_KEY );
        final Instant timestamp = Instant.parse( values.getStringValue( VersionIndexPath.TIMESTAMP ) );
        final String id = values.getStringValue( VersionIndexPath.NODE_ID );
        final String path = values.getStringValue( VersionIndexPath.NODE_PATH );
        final NodeCommitId commitId =
            values.getOptional( VersionIndexPath.COMMIT_ID ).map( Object::toString ).map( NodeCommitId::from ).orElse( null );
        final ReturnValue attributes = values.get( VersionIndexPath.ATTRIBUTES );
        final BlobKeys binaryBlobKeys = toBlobKeys( values.get( VersionIndexPath.BINARY_BLOB_KEYS ) );

        return NodeVersion.create()
            .nodeId( NodeId.from( id ) )
            .nodePath( new NodePath( path ) )
            .timestamp( timestamp )
            .nodeVersionId( NodeVersionId.from( versionId ) )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( nodeBlobKey ) )
                                 .indexConfigBlobKey( BlobKey.from( indexConfigBlobKey ) )
                                 .accessControlBlobKey( BlobKey.from( accessControlBlobKey ) )
                                 .build() )
            .binaryBlobKeys( binaryBlobKeys )
            .nodeCommitId( commitId )
            .attributes( toAttributes( attributes ) )
            .build();
    }

    private static BlobKeys toBlobKeys( final ReturnValue returnValue )
    {
        return returnValue != null ? returnValue.getValues()
            .stream()
            .map( value -> BlobKey.from( value.toString() ) )
            .collect( BlobKeys.collector() ) : BlobKeys.empty();
    }

    private static Attributes toAttributes( ReturnValue val )
    {
        if ( val == null )
        {
            return null;
        }
        final Attributes.Builder builder = Attributes.create();
        for ( Object value : val.getValues() )
        {
            final Map<String, Object> map = (Map<String, Object>) value;

            builder.attribute( (String) map.get( "k" ), GenericValue.fromRawJava( map.get( "v" ) ) );
        }
        return builder.build();
    }

    /** Adapts a domain {@link NodeVersion} onto the SPI {@link VersionRecord}, for {@code NodeStore} store/get results. */
    public static VersionRecord toRecord( final NodeVersion nodeVersion )
    {
        return new VersionRecord( nodeVersion.getNodeVersionId().toString(), nodeVersion.getNodeId().toString(),
                                   nodeVersion.getNodePath().toString(), nodeVersion.getTimestamp(),
                                   nodeVersion.getNodeVersionKey().getNodeBlobKey().toString(),
                                   nodeVersion.getNodeVersionKey().getIndexConfigBlobKey().toString(),
                                   nodeVersion.getNodeVersionKey().getAccessControlBlobKey().toString(),
                                   nodeVersion.getBinaryBlobKeys().stream().map( BlobKey::toString ).collect( Collectors.toList() ),
                                   nodeVersion.getNodeCommitId() == null ? null : nodeVersion.getNodeCommitId().toString(),
                                   toRawAttributes( nodeVersion.getAttributes() ) );
    }

    private static Map<String, Object> toRawAttributes( final Attributes attributes )
    {
        if ( attributes == null )
        {
            return null;
        }
        final Map<String, Object> result = new LinkedHashMap<>();
        for ( final Map.Entry<String, GenericValue> entry : attributes.entrySet() )
        {
            result.put( entry.getKey(), entry.getValue().toRawJava() );
        }
        return result;
    }

    /** Inverse of {@link #toRecord}, for consumers of {@code NodeStore} rebuilding the domain object. */
    public static NodeVersion fromRecord( final VersionRecord record )
    {
        return NodeVersion.create()
            .nodeVersionId( NodeVersionId.from( record.versionId() ) )
            .nodeId( NodeId.from( record.nodeId() ) )
            .nodePath( new NodePath( record.nodePath() ) )
            .timestamp( record.timestamp() )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( record.nodeDataHash() ) )
                                 .indexConfigBlobKey( BlobKey.from( record.indexConfigHash() ) )
                                 .accessControlBlobKey( BlobKey.from( record.aclHash() ) )
                                 .build() )
            .binaryBlobKeys( record.binaryKeys().stream().map( BlobKey::from ).collect( BlobKeys.collector() ) )
            .nodeCommitId( record.commitId() == null ? null : NodeCommitId.from( record.commitId() ) )
            .attributes( fromRawAttributes( record.attributes() ) )
            .build();
    }

    private static Attributes fromRawAttributes( final Map<String, Object> raw )
    {
        if ( raw == null )
        {
            return null;
        }
        final Attributes.Builder builder = Attributes.create();
        raw.forEach( ( key, value ) -> builder.attribute( key, GenericValue.fromRawJava( value ) ) );
        return builder.build();
    }
}
