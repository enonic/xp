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

            builder.attribute( (String) map.get( "k" ), fromRawJava( map.get( "v" ) ) );
        }
        return builder.build();
    }

    static GenericValue fromRawJava( final Object obj )
    {
        return switch ( obj )
        {
            case String s -> GenericValue.stringValue( s );
            case Boolean b -> GenericValue.booleanValue( (boolean) obj );
            case Byte b -> GenericValue.numberValue( (int) obj );
            case Short s -> GenericValue.numberValue( (int) obj );
            case Integer i -> GenericValue.numberValue( (int) obj );
            case Long l -> GenericValue.numberValue( (long) obj );
            case Float v -> GenericValue.numberValue( (float) obj );
            case Double v -> GenericValue.numberValue( (double) obj );

            case Collection<?> c ->
            {
                final var builder = GenericValue.list();
                c.stream().map( NodeVersionFactory::fromRawJava ).forEach( builder::add );
                yield builder.build();
            }
            case Map<?, ?> m ->
            {
                final var builder = GenericValue.object();
                m.forEach( ( key, value ) -> builder.put( key.toString(), fromRawJava( value ) ) );
                yield builder.build();
            }
            default -> throw new IllegalArgumentException( "Unknown object type: " + obj );
        };
    }
}
