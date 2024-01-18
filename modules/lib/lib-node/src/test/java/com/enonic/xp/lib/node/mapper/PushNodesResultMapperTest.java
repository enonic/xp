package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;

public class PushNodesResultMapperTest
    extends BaseMapperTest
{
    @Test
    public void single_successful()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().addSuccess( createEntry( "a" ), new NodePath( "/a" ) ).build();

        assertJson( "single_successful", new PushNodesResultMapper( result ) );
    }

    @Test
    public void single_failed()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().
            addFailed( createEntry( "a" ), PushNodesResult.Reason.ACCESS_DENIED ).
            build();

        assertJson( "single_failed", new PushNodesResultMapper( result ) );
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

        assertJson( "full", new PushNodesResultMapper( result ) );
    }

}
