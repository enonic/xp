package com.enonic.xp.core.impl.issue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;

import static org.junit.Assert.*;

public class DeleteIssueCommentCommandTest
{

    private NodeService nodeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    public void delete()
    {
        final Node issueNode = Node.create().name( "parent-issue" ).id( NodeId.from( "issue-id" ) ).parentPath( NodePath.ROOT ).build();

        DeleteIssueCommentParams params =
            DeleteIssueCommentParams.create().issue( IssueId.create() ).comment( NodeName.from( "comment-one" ) ).build();

        final NodeIds nodeIds = NodeIds.from( NodeId.from( "node-id" ) );
        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );

        Mockito.when( this.nodeService.deleteByPath( Mockito.any( NodePath.class ) ) ).thenReturn( nodeIds );
        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( issueNode );

        final DeleteIssueCommentResult result = command.execute();

        assertNotNull( result );
        assertEquals( "/parent-issue/comment-one", result.getPath().toString() );
        assertEquals( 1, result.getIds().getSize() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCommentName()
    {
        final DeleteIssueCommentParams params = DeleteIssueCommentParams.create().issue( IssueId.create() ).build();
        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );
        command.execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoIssueId()
    {
        final DeleteIssueCommentParams params = DeleteIssueCommentParams.create().comment( NodeName.from( "node-name" ) ).build();
        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );
        command.execute();
    }

    private DeleteIssueCommentCommand createDeleteIssueCommentCommand( DeleteIssueCommentParams params )
    {
        return DeleteIssueCommentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build();
    }
}
