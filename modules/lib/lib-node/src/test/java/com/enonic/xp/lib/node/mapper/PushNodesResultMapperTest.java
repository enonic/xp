package com.enonic.xp.lib.node.mapper;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.testing.helper.JsonAssert;

public class PushNodesResultMapperTest
{
    @Test
    public void single_successful()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().addSuccess( createEntry( "a" ), new NodePath( "/a" ) ).build();

        JsonAssert.assertMapper( getClass(), "nodeResult/single_successful.json", new PushNodesResultMapper( result ) );
    }

    @Test
    public void single_failed()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().
            addFailed( createEntry( "a" ), PushNodesResult.Reason.ACCESS_DENIED ).
            build();

        JsonAssert.assertMapper( getClass(), "nodeResult/single_failed.json", new PushNodesResultMapper( result ) );
    }

    @Test
    public void complex()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create()
            .addSuccess( createEntry( "a" ), new NodePath( "/a" ) )
            .addSuccess( createEntry( "b" ), new NodePath( "/b" ) )
            .addSuccess( createEntry( "c" ), new NodePath( "/c" ) )
            .addFailed( createEntry( "d" ), PushNodesResult.Reason.ACCESS_DENIED )
            .addFailed( createEntry( "e" ), PushNodesResult.Reason.PARENT_NOT_FOUND )
            .addFailed( createEntry( "f" ), PushNodesResult.Reason.PARENT_NOT_FOUND )
            .build();

        JsonAssert.assertMapper( getClass(), "nodeResult/full.json", new PushNodesResultMapper( result ) );
    }

    static NodeBranchEntry createEntry( final String a )
    {
        return NodeBranchEntry.create()
            .nodeId( NodeId.from( a ) )
            .nodePath( new NodePath( "/" + a ) )
            .nodeVersionKey( NodeVersionKey.from( "nodeBlobKey", "indexConfigBlobKey", "accessControlBlobKey" ) )
            .nodeVersionId( new NodeVersionId() )
            .timestamp( Instant.EPOCH )
            .build();
    }
}
