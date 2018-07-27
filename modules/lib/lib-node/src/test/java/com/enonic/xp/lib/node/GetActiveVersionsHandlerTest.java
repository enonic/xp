package com.enonic.xp.lib.node;

import java.time.Instant;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;

import static org.junit.Assert.*;

public class GetActiveVersionsHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testGetActiveVersionsHandler()
    {
        final NodeVersionMetadata nodeVersionMetaDraft = NodeVersionMetadata.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId1" ) ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.ofEpochSecond( 1000 ) ).
            build();

        final NodeVersionMetadata nodeVersionMetaMaster = NodeVersionMetadata.create().
            nodeId( NodeId.from( "nodeId2" ) ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId2" ) ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.ofEpochSecond( 1000 ) ).
            build();

        final GetActiveNodeVersionsResult getActiveNodeVersionsResult = GetActiveNodeVersionsResult.create().
            add( Branch.from( "draft" ), nodeVersionMetaDraft ).
            add( Branch.from( "master" ), nodeVersionMetaMaster ).
            build();
        final ArgumentCaptor<GetActiveNodeVersionsParams> getActiveNodeVersionsParamsCaptor =
            ArgumentCaptor.forClass( GetActiveNodeVersionsParams.class );
        Mockito.when( nodeService.getActiveVersions( Mockito.any() ) ).thenReturn( getActiveNodeVersionsResult );
        runScript( "/site/lib/xp/examples/node/getActiveVersions.js" );
        Mockito.verify( nodeService ).getActiveVersions( getActiveNodeVersionsParamsCaptor.capture() );

        final GetActiveNodeVersionsParams params = getActiveNodeVersionsParamsCaptor.getValue();
        assertEquals( "nodeId", params.getNodeId().toString() );
        assertTrue( params.getBranches().contains( Branch.from( "draft" ) ) );
        assertTrue( params.getBranches().contains( Branch.from( "master" ) ) );
    }
}
