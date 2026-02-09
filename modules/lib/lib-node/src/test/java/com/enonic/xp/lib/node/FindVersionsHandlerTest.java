package com.enonic.xp.lib.node;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.node.GetNodeVersionsResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FindVersionsHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testFindVersionsHandler()
    {
        final NodeVersion newNodeVersionMeta = NodeVersion.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionKey( NodeVersionKey.create()
                                .nodeBlobKey( BlobKey.from( "nodeBlobKey" ) )
                                .indexConfigBlobKey( BlobKey.from( "indexConfigBlobKey" ) )
                                .accessControlBlobKey( BlobKey.from( "accessControlBlobKey" ) )
                                .build() ).
            nodeVersionId( NodeVersionId.from( "nodeVersionNew" ) ).
            binaryBlobKeys( BlobKeys.empty() ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.ofEpochSecond( 1000 ) ).
            build();

        final NodeVersion oldNodeVersionMeta = NodeVersion.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionKey( NodeVersionKey.create()
                                .nodeBlobKey( BlobKey.from( "nodeBlobKey" ) )
                                .indexConfigBlobKey( BlobKey.from( "indexConfigBlobKey" ) )
                                .accessControlBlobKey( BlobKey.from( "accessControlBlobKey" ) )
                                .build() ).
            nodeVersionId( NodeVersionId.from( "nodeVersionOld" ) ).
            binaryBlobKeys( BlobKeys.empty() ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.ofEpochSecond( 500 ) ).
            build();

        final NodeVersions nodeVersions = NodeVersions.create().
            add( newNodeVersionMeta ).
            add( oldNodeVersionMeta ).
            build();

        final GetNodeVersionsResult result = GetNodeVersionsResult.create().
            entityVersions( nodeVersions ).
            totalHits( 40 ).
            build();

        final ArgumentCaptor<GetNodeVersionsParams> getNodeVersionsParamsCaptor = ArgumentCaptor.forClass( GetNodeVersionsParams.class );
        Mockito.when( nodeService.getVersions( Mockito.any( GetNodeVersionsParams.class ) ) ).thenReturn( result );
        runScript( "/lib/xp/examples/node/findVersions.js" );
        Mockito.verify( nodeService ).getVersions( getNodeVersionsParamsCaptor.capture() );

        assertEquals( "nodeId", getNodeVersionsParamsCaptor.getValue().getNodeId().toString() );
        assertNull( getNodeVersionsParamsCaptor.getValue().getCursor() );
        assertEquals( 2, getNodeVersionsParamsCaptor.getValue().getSize() );
    }
}
