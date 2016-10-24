package com.enonic.xp.lib.node.mapper;

import org.junit.Test;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.script.serializer.JsonMapGenerator;

public class PushNodesResultMapperTest
    extends BaseMapperTest
{
    @Test
    public void single_successful()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().
            addSuccess( createEntry( "a" ) ).
            build();

        final JsonMapGenerator jsonGenerator = new JsonMapGenerator();
        new PushNodesResultMapper( result, NodeIds.empty() ).serialize( jsonGenerator );

        assertJson( "nodeResult/single_successful.json", jsonGenerator );
    }

    @Test
    public void single_failed()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().
            addFailed( createEntry( "a" ), PushNodesResult.Reason.ACCESS_DENIED ).
            build();

        final JsonMapGenerator jsonGenerator = new JsonMapGenerator();
        new PushNodesResultMapper( result, NodeIds.empty() ).serialize( jsonGenerator );

        assertJson( "nodeResult/single_failed.json", jsonGenerator );
    }

    @Test
    public void single_deleted()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().
            build();

        final JsonMapGenerator jsonGenerator = new JsonMapGenerator();
        new PushNodesResultMapper( result, NodeIds.from( "a" ) ).serialize( jsonGenerator );

        assertJson( "nodeResult/single_deleted.json", jsonGenerator );
    }

    @Test
    public void complex()
        throws Exception
    {
        final PushNodesResult result = PushNodesResult.create().
            addSuccess( createEntry( "a" ) ).
            addSuccess( createEntry( "b" ) ).
            addSuccess( createEntry( "c" ) ).
            addFailed( createEntry( "d" ), PushNodesResult.Reason.ACCESS_DENIED ).
            addFailed( createEntry( "e" ), PushNodesResult.Reason.PARENT_NOT_FOUND ).
            addFailed( createEntry( "f" ), PushNodesResult.Reason.PARENT_NOT_FOUND ).
            build();

        final JsonMapGenerator jsonGenerator = new JsonMapGenerator();
        new PushNodesResultMapper( result, NodeIds.from( "g", "h", "i" ) ).serialize( jsonGenerator );

        assertJson( "nodeResult/full.json", jsonGenerator );
    }

}