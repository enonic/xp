package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.testing.helper.JsonAssert;

class ResolveSyncWorkResultMapperTest
{
    @Test
    void full()
    {
        final ResolveSyncWorkResult result = ResolveSyncWorkResult.create().
            add( createNodeComparison( "a", "a", CompareStatus.NEW ) ).
            add( createNodeComparison( "b" , "b", CompareStatus.MOVED ) ).
            add( createNodeComparison( "c" , "c", CompareStatus.NEWER ) ).
            build();

        JsonAssert.assertMapper( getClass(), "resolveSyncWork/full.json", new ResolveSyncWorkResultMapper( result ) );
    }

    private NodeComparison createNodeComparison( String a, String b, CompareStatus status )
    {
        return new NodeComparison( NodeId.from( a ), new NodePath( "/" + a ), NodeId.from( b ), new NodePath( "/" + b ),
                                   status );
    }
}
