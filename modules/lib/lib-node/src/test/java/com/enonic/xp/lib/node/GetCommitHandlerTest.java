package com.enonic.xp.lib.node;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.PrincipalKey;

class GetCommitHandlerTest
    extends BaseNodeHandlerTest
{
    private void mock()
    {
        Mockito.when( nodeService.getByPaths( Mockito.any() ) ).thenReturn( Nodes.empty() );

        final Answer<NodeCommitEntry> answer = invocation -> {
            final NodeCommitId commitId = invocation.getArgument( 0, NodeCommitId.class );
            return NodeCommitEntry.create().
                nodeCommitId( commitId ).
                committer( PrincipalKey.from( "user:system:anonymous" ) ).
                timestamp( Instant.parse( "2019-01-24T15:16:36.260799Z" ) ).
                build();
        };

        Mockito.when( nodeService.commit( Mockito.any(), Mockito.any( NodeIds.class ) ) ).
            thenReturn( NodeCommitEntry.create().
                nodeCommitId( NodeCommitId.from( "aa1f76bf-4bb9-41be-b166-03561c1555b2" ) ).
                committer( PrincipalKey.from( "user:system:anonymous" ) ).
                timestamp( Instant.parse( "2019-01-24T15:16:36.260799Z" ) ).
                build() );

        Mockito.when( nodeService.getCommit( Mockito.any( NodeCommitId.class ) ) ).
            thenAnswer( answer );
    }

    @Test
    void testExample()
    {
        mock();
        runScript( "/lib/xp/examples/node/getCommit.js" );
    }

    @Test
    void testEmpty()
    {
        runFunction( "/test/GetCommitHandlerTest.js", "testEmpty" );
    }
}
