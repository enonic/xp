package com.enonic.xp.repo.impl.version;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.util.GenericValue;

public class NodeVersionFactory
{
    public static NodeVersionMetadata create( final ReturnValues values )
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

        return NodeVersionMetadata.create()
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

}
