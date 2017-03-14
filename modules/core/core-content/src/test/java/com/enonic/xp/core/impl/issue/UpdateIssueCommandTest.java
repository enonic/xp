package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssuePath;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;
import static org.junit.Assert.*;

public class UpdateIssueCommandTest
{

    private NodeService nodeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        Mockito.when( this.nodeService.update( Mockito.any( UpdateNodeParams.class ) ) ).thenAnswer( this::mockNodeServiceUpdate );
        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenAnswer( this::mockNodeServiceGet );
    }

    @Test
    public void update()
    {
        final UpdateIssueParams params = updateIssueParams().build();
        final UpdateIssueCommand command = updateIssueCommand( params );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        final Issue issue = command.execute();
        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( issueName, issue.getName() );
        assertEquals( IssuePath.from( issueName ), issue.getPath() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoModifier()
    {
        final UpdateIssueParams params = UpdateIssueParams.create().title( "title" ).build();
        final UpdateIssueCommand command = updateIssueCommand( params );

        command.execute();
    }

    private UpdateIssueParams.Builder updateIssueParams()
    {
        return UpdateIssueParams.create().
            id( IssueId.create() ).
            title( "title" ).
            modifier( PrincipalKey.from( "user:myStore:other-user" ) );
    }

    private UpdateIssueCommand updateIssueCommand( UpdateIssueParams params )
    {
        return UpdateIssueCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build();
    }

    private Node mockNodeServiceUpdate( final InvocationOnMock invocation )
        throws Throwable
    {
        UpdateNodeParams params = (UpdateNodeParams) invocation.getArguments()[0];

        return createMockNode( params.getId() );
    }

    private Node mockNodeServiceGet( final InvocationOnMock invocation )
        throws Throwable
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
            permissions( IssueConstants.ACCESS_CONTROL_ENTRIES ).
            nodeType( IssueConstants.ISSUE_NODE_COLLECTION ).
            timestamp( Instant.now() ).
            build();
    }

    private PropertyTree createMockData()
    {
        final PropertyTree propertyTree = new PropertyTree();
        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.ifNotNull().addString( STATUS, IssueStatus.Open.toString() );
        issueAsData.ifNotNull().addString( CREATOR, PrincipalKey.from( "user:myStore:me" ).toString() );
        issueAsData.ifNotNull().addString( TITLE, "title" );

        return propertyTree;
    }
}
