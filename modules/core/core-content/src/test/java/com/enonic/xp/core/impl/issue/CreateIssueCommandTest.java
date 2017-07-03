package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;

import static org.junit.Assert.*;

public class CreateIssueCommandTest
{

    private NodeService nodeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );

        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenAnswer( this::mockNodeServiceCreate );
    }

    @Test
    public void create()
    {
        final CreateIssueParams params = createIssueParams().build();
        final CreateIssueCommand command = createIssueCommand( params );
        Mockito.when( this.nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).thenReturn(
            FindNodesByQueryResult.create().build() );

        final Issue issue = command.execute();

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( IssueNameFactory.create( issue.getIndex() ), issue.getName() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoTitle()
    {
        final CreateIssueParams params = CreateIssueParams.create().build();
        final CreateIssueCommand command = createIssueCommand( params );
        command.execute();
    }


    private CreateIssueParams.Builder createIssueParams()
    {
        return CreateIssueParams.create().
            title( "title" );
    }

    private CreateIssueCommand createIssueCommand( CreateIssueParams params )
    {
        return CreateIssueCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build();
    }

    private Node mockNodeServiceCreate( final InvocationOnMock invocation )
        throws Throwable
    {
        CreateNodeParams params = (CreateNodeParams) invocation.getArguments()[0];

        return Node.create().
            id( params.getNodeId() != null ? params.getNodeId() : new NodeId() ).
            parentPath( params.getParent() ).
            name( NodeName.from( params.getName() ) ).
            data( params.getData() ).
            indexConfigDocument( params.getIndexConfigDocument() ).
            childOrder( params.getChildOrder() != null ? params.getChildOrder() : IssueConstants.DEFAULT_CHILD_ORDER ).
            nodeType( params.getNodeType() != null ? params.getNodeType() : IssueConstants.ISSUE_NODE_COLLECTION ).
            timestamp( Instant.now() ).
            build();
    }
}
