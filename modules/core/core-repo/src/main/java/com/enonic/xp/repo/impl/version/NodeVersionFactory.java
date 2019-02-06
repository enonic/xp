package com.enonic.xp.repo.impl.version;

import java.time.Instant;

import com.google.common.base.Strings;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
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
        final Instant timestamp = Instant.parse( values.getSingleValue( VersionIndexPath.TIMESTAMP.getPath() ).toString() );
        final String id = values.getSingleValue( VersionIndexPath.NODE_ID.getPath() ).toString();
        final String path = values.getSingleValue( VersionIndexPath.NODE_PATH.getPath() ).toString();
        final Object commitId = values.getSingleValue( VersionIndexPath.COMMIT_ID.getPath() );

        final NodeVersionKey nodeVersionKey = NodeVersionKey.from( nodeBlobKey, indexConfigBlobKey, accessControlBlobKey );

        return NodeVersionMetadata.create().
            nodeId( NodeId.from( id ) ).
            nodePath( NodePath.create( path ).build() ).
            timestamp( timestamp ).
            nodeVersionId( NodeVersionId.from( versionId ) ).
            nodeVersionKey( nodeVersionKey ).
            nodeCommitId( commitId == null ? null : NodeCommitId.from( commitId.toString() )  ).
            build();
    }

}
