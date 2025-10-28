package com.enonic.xp.core.impl.issue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.issue.serializer.IssueCommentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateIssueCommentCommandTest
{
    private NodeService nodeService;

    @BeforeEach
    void setUp()
    {
        this.nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    void update()
    {
        final Node issueNode =
            Node.create().name( "parent-issue" ).parentPath( NodePath.ROOT ).id( NodeId.from( UUID.randomUUID() ) ).build();

        final PrincipalKey creator = PrincipalKey.from( "user:store:one" );

        final UpdateIssueCommentParams params = UpdateIssueCommentParams.create().
            comment( NodeId.from( UUID.randomUUID() ) ).
            text( "Comment text..." ).
            build();

        PropertyTree data = new IssueCommentDataSerializer().toCreateNodeData(
            CreateIssueCommentParams.create().text( params.getText() ).creator( creator ).creatorDisplayName( "Creator One" ).issue(
                IssueId.from( issueNode.id().toString() ) ).build() );

        final Node commentNode = Node.create().parentPath( issueNode.path() ).name( "comment-node" ).data( data ).build();

        final UpdateIssueCommentCommand command = updateIssueCommentCommand( params );

        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( issueNode );
        Mockito.when( this.nodeService.update( Mockito.any( UpdateNodeParams.class ) ) ).thenReturn( commentNode );

        final IssueComment comment = command.execute();

        assertNotNull( comment );
        assertEquals( "Comment text...", comment.getText() );
        assertEquals( creator, comment.getCreator() );
        assertEquals( "Creator One", comment.getCreatorDisplayName() );
    }

    @Test
    void updateCommentNotExists()
    {
        final UpdateIssueCommentParams params = UpdateIssueCommentParams.create().
            comment( NodeId.from( UUID.randomUUID() ) ).
            text( "Comment text..." ).
            build();

        final UpdateIssueCommentCommand command = updateIssueCommentCommand( params );

        Mockito.when( this.nodeService.update( Mockito.any( UpdateNodeParams.class ) ) ).thenThrow(
            new NodeNotFoundException( "Node not found" ) );

        assertThrows(NodeNotFoundException.class, () ->  command.execute());
    }

    @Test
    void updateNoText()
    {
        final UpdateIssueCommentParams params = UpdateIssueCommentParams.create().comment( NodeId.from( UUID.randomUUID() ) ).build();
        final UpdateIssueCommentCommand command = updateIssueCommentCommand( params );
        assertThrows(IllegalArgumentException.class, () -> command.execute());
    }

    @Test
    void updateNoCommentId()
    {
        final UpdateIssueCommentParams params = UpdateIssueCommentParams.create().text( "text" ).build();
        final UpdateIssueCommentCommand command = updateIssueCommentCommand( params );
        assertThrows(IllegalArgumentException.class, () -> command.execute());
    }

    private UpdateIssueCommentCommand updateIssueCommentCommand( UpdateIssueCommentParams params )
    {
        return UpdateIssueCommentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build();
    }
}
