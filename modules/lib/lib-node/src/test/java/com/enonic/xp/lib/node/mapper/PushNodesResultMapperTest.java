package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodeResult;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.testing.helper.JsonAssert;

class PushNodesResultMapperTest
{
    @Test
    void single_successful()
    {
        final PushNodesResult result = PushNodesResult.create().add( createPushNodeResult( "a", "/a" ) ).build();

        JsonAssert.assertMapper( getClass(), "nodeResult/single_successful.json", new PushNodesResultMapper( result ) );
    }

    @Test
    void single_failed()
    {
        final PushNodesResult result = PushNodesResult.create().
            add( createNodePushResultFailed( "a", PushNodeResult.Reason.ACCESS_DENIED ) ).
            build();

        JsonAssert.assertMapper( getClass(), "nodeResult/single_failed.json", new PushNodesResultMapper( result ) );
    }

    @Test
    void complex()
    {
        final PushNodesResult result = PushNodesResult.create()
            .add( createPushNodeResult( "a", "/a") )
            .add( createPushNodeResult( "b", "/b" ) )
            .add( createPushNodeResult( "c", "/c" ) )
            .add( createNodePushResultFailed( "d", PushNodeResult.Reason.ACCESS_DENIED ) )
            .add( createNodePushResultFailed( "e", PushNodeResult.Reason.PARENT_NOT_FOUND ) )
            .add( createNodePushResultFailed( "f", PushNodeResult.Reason.PARENT_NOT_FOUND ) )
            .build();

        JsonAssert.assertMapper( getClass(), "nodeResult/full.json", new PushNodesResultMapper( result ) );
    }

    private static PushNodeResult createPushNodeResult( String a, String targetPath)
    {
        return PushNodeResult.success( NodeId.from( a ), new NodeVersionId(), new NodePath( "/" + a ), new NodePath( targetPath ) );
    }

    private static PushNodeResult createNodePushResultFailed( String a, PushNodeResult.Reason reason )
    {
        return PushNodeResult.failure( NodeId.from( a ), new NodePath( "/" + a ), reason );
    }
}
