package com.enonic.xp.admin.impl.rest.resource.commit;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.commit.CommitService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.security.PrincipalKey;

public class CommitResourceTest
    extends AdminResourceTestSupport
{
    private CommitService commitService;

    private NodeService nodeService;

    @Override
    protected CommitResource getResourceInstance()
    {
        commitService = Mockito.mock( CommitService.class );
        nodeService = Mockito.mock( NodeService.class );

        final CommitResource resource = new CommitResource();

        resource.setCommitService( commitService );
        resource.setNodeService( nodeService );

        return resource;
    }

    @Test
    public void get_commit_by_commit_id()
        throws Exception
    {
        final NodeCommitEntry nodeCommitEntry = NodeCommitEntry.create().
            nodeCommitId( NodeCommitId.from( "commitId" ) ).
            committer( PrincipalKey.from( "user:system:admin" ) ).
            message( "commit message" ).
            timestamp( Instant.now() ).
            build();

        final CommitResource commitResource = getResourceInstance();

        Mockito.when( commitService.get( NodeCommitId.from( "commitId" ), InternalContext.from( ContextAccessor.current() ) ) ).
            thenReturn( nodeCommitEntry );

        final GetCommitResultJson result = commitResource.getCommit( new GetCommitJson( "nodeId", "commitId" ) );

        assertEquals( "user:system:admin", result.getCommiter() );
        assertEquals( "commit message", result.getMessage() );
        assertEquals( "commitId", result.getNodeCommitId() );
    }

    @Test
    public void get_commit_by_node_id()
        throws Exception
    {
        final CommitResource commitResource = getResourceInstance();

        final NodeCommitEntry nodeCommitEntry = NodeCommitEntry.create().
            nodeCommitId( NodeCommitId.from( "commitId" ) ).
            committer( PrincipalKey.from( "user:system:admin" ) ).
            message( "commit message" ).
            timestamp( Instant.now() ).
            build();

        Mockito.when( commitService.get( NodeCommitId.from( "commitId" ), InternalContext.from( ContextAccessor.current() ) ) ).
            thenReturn( nodeCommitEntry );

        final GetActiveNodeVersionsResult activeNodeVersionsResult = GetActiveNodeVersionsResult.create().
            add( ContextAccessor.current().getBranch(), NodeVersionMetadata.create().
                nodeCommitId( NodeCommitId.from( "commitId" ) ).
                build() ).
            build();

        Mockito.when( nodeService.getActiveVersions( GetActiveNodeVersionsParams.create().
            nodeId( NodeId.from( "nodeId" ) ).
            branches( Branches.from( ContextAccessor.current().getBranch() ) ).
            build() ) ).
            thenReturn( activeNodeVersionsResult );

        final GetCommitResultJson result = commitResource.getCommit( new GetCommitJson( "nodeId", null ) );

        assertEquals( "user:system:admin", result.getCommiter() );
        assertEquals( "commit message", result.getMessage() );
        assertEquals( "commitId", result.getNodeCommitId() );
    }

    @Test(expected = CommitNotFoundException.class)
    public void get_commit_not_found()
        throws Exception
    {
        final CommitResource commitResource = getResourceInstance();

        Mockito.when( nodeService.getActiveVersions( Mockito.any(GetActiveNodeVersionsParams.class ))).
            thenReturn( GetActiveNodeVersionsResult.create().build() );

        commitResource.getCommit( new GetCommitJson( "nodeId", null ) );

    }
}
