package com.enonic.xp.core.impl.issue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
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
        DeleteIssueCommentParams params = DeleteIssueCommentParams.create().comment( NodeId.from( UUID.randomUUID() ) ).build();

        final NodeIds nodeIds = NodeIds.from( params.getComment() );
        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );

        Mockito.when( this.nodeService.deleteById( Mockito.any( NodeId.class ) ) ).thenReturn( nodeIds );

        final DeleteIssueCommentResult result = command.execute();

        assertNotNull( result );
        assertEquals( 1, result.getIds().getSize() );
        assertEquals( params.getComment(), result.getIds().first() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCommentId()
    {
        final DeleteIssueCommentParams params = DeleteIssueCommentParams.create().build();
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
