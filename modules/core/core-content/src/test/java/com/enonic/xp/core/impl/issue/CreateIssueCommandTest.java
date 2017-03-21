package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssuePath;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
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
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        final Issue issue = command.execute();
        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( issueName, issue.getName() );
        assertEquals( IssuePath.from( issueName ), issue.getPath() );
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
            permissions( IssueConstants.ACCESS_CONTROL_ENTRIES ).
            nodeType( params.getNodeType() != null ? params.getNodeType() : IssueConstants.ISSUE_NODE_COLLECTION ).
            timestamp( Instant.now() ).
            build();
    }
}
