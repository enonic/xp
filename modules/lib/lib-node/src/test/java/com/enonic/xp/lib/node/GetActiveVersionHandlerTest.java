package com.enonic.xp.lib.node;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersionMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetActiveVersionHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testGetActiveVersionsHandler()
    {
        final NodeVersionMetadata nodeVersionMeta = NodeVersionMetadata.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionKey( NodeVersionKey.create()
                                .nodeBlobKey( BlobKey.from( "nodeBlobKey" ) )
                                .indexConfigBlobKey( BlobKey.from( "indexConfigBlobKey" ) )
                                .accessControlBlobKey( BlobKey.from( "accessControlBlobKey" ) )
                                .build() ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId1" ) ).
            binaryBlobKeys( BlobKeys.empty() ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.ofEpochSecond( 1000 ) ).
            build();

        final GetActiveNodeVersionsResult getActiveNodeVersionsResult = GetActiveNodeVersionsResult.create().
            add( Branch.from( "master" ), nodeVersionMeta ).
            build();
        final ArgumentCaptor<GetActiveNodeVersionsParams> getActiveNodeVersionsParamsCaptor =
            ArgumentCaptor.forClass( GetActiveNodeVersionsParams.class );
        Mockito.when( nodeService.getActiveVersions( Mockito.any() ) ).thenReturn( getActiveNodeVersionsResult );
        runScript( "/lib/xp/examples/node/getActiveVersion.js" );
        Mockito.verify( nodeService ).getActiveVersions( getActiveNodeVersionsParamsCaptor.capture() );

        final GetActiveNodeVersionsParams params = getActiveNodeVersionsParamsCaptor.getValue();
        assertEquals( "nodeId", params.getNodeId().toString() );
        assertTrue( params.getBranches().contains( Branch.from( "master" ) ) );
    }
}
