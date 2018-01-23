package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class CreateIssueCommentCommandTest
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
        final Node issueNode = Node.create().name( "parent-issue" ).build();
        final PrincipalKey creator = PrincipalKey.from( "user:store:one" );
        final CreateIssueCommentParams params = CreateIssueCommentParams.create().
            issue( IssueId.create() ).
            creator( creator ).
            creatorDisplayName( "Creator One" ).
            text( "Comment text..." ).
            build();

        final CreateIssueCommentCommand command = createIssueCommentCommand( params );
        Mockito.when( this.nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).thenReturn(
            FindNodesByQueryResult.create().build() );

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( issueNode );

        final IssueComment comment = command.execute();

        assertNotNull( comment );
        assertEquals( "Comment text...", comment.getText() );
        assertEquals( creator, comment.getCreator() );
        assertEquals( "Creator One", comment.getCreatorDisplayName() );
    }

    @Test(expected = NodeNotFoundException.class)
    public void createIssueNotExists()
    {
        final PrincipalKey creator = PrincipalKey.from( "user:store:one" );

        final CreateIssueCommentParams params = CreateIssueCommentParams.create().
            creator( creator ).
            issue( IssueId.create() ).
            creatorDisplayName( "Creator One" ).
            text( "Comment text..." ).
            build();

        final CreateIssueCommentCommand command = createIssueCommentCommand( params );
        Mockito.when( this.nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).thenReturn(
            FindNodesByQueryResult.create().build() );

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenThrow( new NodeNotFoundException( "Node not found" ) );

        final IssueComment comment = command.execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoText()
    {
        final CreateIssueCommentParams params = CreateIssueCommentParams.create().issue( IssueId.create() ).build();
        final CreateIssueCommentCommand command = createIssueCommentCommand( params );
        command.execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoIssueId()
    {
        final CreateIssueCommentParams params = CreateIssueCommentParams.create().text( "text" ).build();
        final CreateIssueCommentCommand command = createIssueCommentCommand( params );
        command.execute();
    }

    private CreateIssueCommentCommand createIssueCommentCommand( CreateIssueCommentParams params )
    {
        return CreateIssueCommentCommand.create().
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
            childOrder( params.getChildOrder() != null ? params.getChildOrder() : IssueCommentConstants.DEFAULT_CHILD_ORDER ).
            nodeType( params.getNodeType() != null ? params.getNodeType() : IssueCommentConstants.NODE_COLLECTION ).
            timestamp( Instant.now() ).
            build();
    }
}
