package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.INDEX;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateIssueCommandTest
{

    private NodeService nodeService;

    @BeforeEach
    void setUp()
    {
        this.nodeService = Mockito.mock( NodeService.class );
        Mockito.when( this.nodeService.update( Mockito.any( UpdateNodeParams.class ) ) ).thenAnswer( this::mockNodeServiceUpdate );
        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenAnswer( this::mockNodeServiceGet );
    }

    @Test
    void update()
    {
        final UpdateIssueParams params = makeUpdateIssueParams();
        final UpdateIssueCommand command = updateIssueCommand( params );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        final Issue issue = command.execute();
        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( issueName, issue.getName() );
    }

    private UpdateIssueParams makeUpdateIssueParams()
    {
        return UpdateIssueParams.create().id( IssueId.create() ).build();
    }

    private UpdateIssueCommand updateIssueCommand( UpdateIssueParams params )
    {
        return UpdateIssueCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build();
    }

    private Node mockNodeServiceUpdate( final InvocationOnMock invocation )
    {
        UpdateNodeParams params = (UpdateNodeParams) invocation.getArguments()[0];

        return createMockNode( params.getId() );
    }

    private Node mockNodeServiceGet( final InvocationOnMock invocation )
    {
        NodeId nodeId = (NodeId) invocation.getArguments()[0];

        return createMockNode( nodeId );
    }

    private Node createMockNode( NodeId nodeId )
    {
        return Node.create().
            id( nodeId ).
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

        issueAsData.ifNotNull().addString( STATUS, IssueStatus.OPEN.toString() );
        issueAsData.ifNotNull().addString( CREATOR, PrincipalKey.from( "user:myStore:me" ).toString() );
        issueAsData.ifNotNull().addString( TITLE, "title" );
        issueAsData.ifNotNull().addLong( INDEX, 1L );

        return propertyTree;
    }
}
