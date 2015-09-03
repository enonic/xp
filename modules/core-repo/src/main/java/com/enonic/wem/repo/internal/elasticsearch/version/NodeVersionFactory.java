package com.enonic.wem.repo.internal.elasticsearch.version;

import java.time.Instant;

import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.ReturnValues;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;

public class NodeVersionFactory
{

    public static NodeVersion create( final GetResult getResult )
    {
        final ReturnValues values = getResult.getReturnValues();

        final String versionId = values.getSingleValue( VersionIndexPath.VERSION_ID.getPath() ).toString();
        final Instant timestamp = Instant.parse( values.getSingleValue( VersionIndexPath.TIMESTAMP.getPath() ).toString() );
        final String id = values.getSingleValue( VersionIndexPath.NODE_ID.getPath() ).toString();
        final String path = values.getSingleValue( VersionIndexPath.NODE_PATH.getPath() ).toString();

        return NodeVersion.create().
            nodeId( NodeId.from( id ) ).
            nodePath( NodePath.create( path ).build() ).
            timestamp( timestamp ).
            nodeVersionId( NodeVersionId.from( versionId ) ).
            build();
    }

}
