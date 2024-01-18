package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.ResolveSyncWorkResult;

public class ResolveSyncWorkResultMapperTest
    extends BaseMapperTest
{
    @Test
    public void full()
        throws Exception
    {
        final ResolveSyncWorkResult result = ResolveSyncWorkResult.create().
            add( new NodeComparison( createEntry( "a" ), createEntry( "a" ), CompareStatus.NEW ) ).
            add( new NodeComparison( createEntry( "b" ), createEntry( "b" ), CompareStatus.MOVED ) ).
            add( new NodeComparison( createEntry( "c" ), createEntry( "c" ), CompareStatus.NEWER ) ).
            build();

        assertJson( "full", new ResolveSyncWorkResultMapper( result ) );
    }
}
