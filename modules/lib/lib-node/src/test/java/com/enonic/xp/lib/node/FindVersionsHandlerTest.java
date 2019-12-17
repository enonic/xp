package com.enonic.xp.lib.node;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersionsMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindVersionsHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testFindVersionsHandler()
    {
        final NodeVersionMetadata newNodeVersionMeta = NodeVersionMetadata.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionId( NodeVersionId.from( "nodeVersionNew" ) ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.ofEpochSecond( 1000 ) ).
            build();

        final NodeVersionMetadata oldNodeVersionMeta = NodeVersionMetadata.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionId( NodeVersionId.from( "nodeVersionOld" ) ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.ofEpochSecond( 500 ) ).
            build();

        final NodeVersionsMetadata nodeVersionsMetadata = NodeVersionsMetadata.create( NodeId.from( "nodeId1" ) ).
            add( newNodeVersionMeta ).
            add( oldNodeVersionMeta ).
            build();

        final NodeVersionQueryResult result = NodeVersionQueryResult.create().
            entityVersions( nodeVersionsMetadata ).
            from( 0 ).
            to( 2 ).
            hits( 2 ).
            totalHits( 40 ).
            build();

        final ArgumentCaptor<GetNodeVersionsParams> getNodeVersionsParamsCaptor = ArgumentCaptor.forClass( GetNodeVersionsParams.class );
        Mockito.when( nodeService.findVersions( Mockito.any( GetNodeVersionsParams.class ) ) ).thenReturn( result );
        runScript( "/lib/xp/examples/node/findVersions.js" );
        Mockito.verify( nodeService ).findVersions( getNodeVersionsParamsCaptor.capture() );

        assertEquals( "nodeId", getNodeVersionsParamsCaptor.getValue().getNodeId().toString() );
        assertEquals( 0, getNodeVersionsParamsCaptor.getValue().getFrom() );
        assertEquals( 2, getNodeVersionsParamsCaptor.getValue().getSize() );
    }
}
