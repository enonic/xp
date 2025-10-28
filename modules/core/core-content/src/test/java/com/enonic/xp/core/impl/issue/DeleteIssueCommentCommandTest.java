package com.enonic.xp.core.impl.issue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteIssueCommentCommandTest
{

    private NodeService nodeService;

    @BeforeEach
    void setUp()
    {
        this.nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    void delete()
    {
        DeleteIssueCommentParams params = DeleteIssueCommentParams.create().comment( NodeId.from( UUID.randomUUID() ) ).build();

        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );

        Mockito.when( this.nodeService.delete( Mockito.any() ) )
            .thenAnswer( DeleteIssueCommentCommandTest::answerDeleted );

        final DeleteIssueCommentResult result = command.execute();

        assertNotNull( result );
        assertEquals( 1, result.getIds().getSize() );
        assertEquals( params.getComment(), result.getIds().first() );
    }

    @Test
    void testNoCommentId()
    {
        final DeleteIssueCommentParams params = DeleteIssueCommentParams.create().build();
        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );
        assertThrows( IllegalArgumentException.class, command::execute );
    }

    private DeleteIssueCommentCommand createDeleteIssueCommentCommand( DeleteIssueCommentParams params )
    {
        return DeleteIssueCommentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build();
    }

    private static DeleteNodeResult answerDeleted( InvocationOnMock answer )
    {
        return DeleteNodeResult.create()
            .nodeIds( NodeIds.from( answer.getArgument( 0, DeleteNodeParams.class ).getNodeId() ) )
            .build();
    }
}
