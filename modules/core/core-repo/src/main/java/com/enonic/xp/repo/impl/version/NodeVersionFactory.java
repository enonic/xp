package com.enonic.xp.repo.impl.version;

import java.time.Instant;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.storage.GetResult;

class NodeVersionFactory
{
    public static NodeVersionMetadata create( final GetResult getResult )
    {
        final ReturnValues values = getResult.getReturnValues();

        final String versionId = values.getSingleValue( VersionIndexPath.VERSION_ID.getPath() ).toString();
        final String nodeBlobKey = values.getSingleValue( VersionIndexPath.NODE_BLOB_KEY.getPath() ).toString();
        final String indexConfigBlobKey = values.getSingleValue( VersionIndexPath.INDEX_CONFIG_BLOB_KEY.getPath() ).toString();
        final String accessControlBlobKey = values.getSingleValue( VersionIndexPath.ACCESS_CONTROL_BLOB_KEY.getPath() ).toString();
        final ReturnValue binaryBlobKeysReturnValue = values.get( VersionIndexPath.BINARY_BLOB_KEYS.getPath() );
        final Instant timestamp = Instant.parse( values.getSingleValue( VersionIndexPath.TIMESTAMP.getPath() ).toString() );
        final String id = values.getSingleValue( VersionIndexPath.NODE_ID.getPath() ).toString();
        final String path = values.getSingleValue( VersionIndexPath.NODE_PATH.getPath() ).toString();
        final Object commitId = values.getSingleValue( VersionIndexPath.COMMIT_ID.getPath() );

        final BlobKeys binaryBlobKeys = toBlobKeys( binaryBlobKeysReturnValue );

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
            .nodeCommitId( commitId == null ? null : NodeCommitId.from( commitId ) )
            .build();
    }

    private static BlobKeys toBlobKeys( final ReturnValue returnValue )
    {
        return returnValue != null ? returnValue.getValues()
            .stream()
            .map( value -> BlobKey.from( value.toString() ) )
            .collect( BlobKeys.collector() ) : BlobKeys.empty();
    }
}
