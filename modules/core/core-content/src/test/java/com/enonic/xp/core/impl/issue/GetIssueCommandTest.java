package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.core.impl.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.INDEX;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetIssueCommandTest
{

    private NodeService nodeService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenAnswer( this::mockNodeServiceGet );
    }

    @Test
    public void getById()
    {
        final GetIssueByIdCommand command = getIssueCommand( IssueId.create() );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        final Issue issue = command.execute();
        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( issueName, issue.getName() );
    }

    private GetIssueByIdCommand getIssueCommand( IssueId issueId )
    {
        return GetIssueByIdCommand.create().
            issueId( issueId ).
            nodeService( this.nodeService ).
            build();
    }

    private Node mockNodeServiceGet( final InvocationOnMock invocation )
        throws Throwable
    {
        NodeId nodeId = (NodeId) invocation.getArguments()[0];

        return Node.create().
            id( nodeId != null ? nodeId : new NodeId() ).
            parentPath( IssueConstants.ISSUE_ROOT_PATH ).
            name( "title" ).
            data( this.createMockData() ).
            childOrder( IssueConstants.DEFAULT_CHILD_ORDER ).
            nodeType( IssueConstants.ISSUE_NODE_COLLECTION ).
            timestamp( Instant.now() ).
            build();
    }

    private PropertyTree createMockData()
    {
        final PropertyTree propertyTree = new PropertyTree();
        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.ifNotNull().addString( TITLE, "title" );
        issueAsData.ifNotNull().addString( STATUS, IssueStatus.OPEN.toString() );
        issueAsData.ifNotNull().addString( CREATOR, PrincipalKey.from( "user:myStore:me" ).toString() );
        issueAsData.ifNotNull().addLong( INDEX, 1L );

        return propertyTree;
    }
}
