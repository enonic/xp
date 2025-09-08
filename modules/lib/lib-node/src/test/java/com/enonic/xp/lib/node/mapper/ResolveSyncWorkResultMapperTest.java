package com.enonic.xp.lib.node.mapper;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.testing.helper.JsonAssert;

public class ResolveSyncWorkResultMapperTest
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

        JsonAssert.assertMapper( getClass(), "resolveSyncWork/full.json", new ResolveSyncWorkResultMapper( result ) );
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
