package com.enonic.xp.core.impl.issue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteIssueCommentCommandTest
{

    private NodeService nodeService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    public void delete()
    {
        DeleteIssueCommentParams params = DeleteIssueCommentParams.create().comment( NodeId.from( UUID.randomUUID() ) ).build();

        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );

        Mockito.when( this.nodeService.delete( Mockito.any() ) )
            .thenReturn( DeleteNodeResult.create()
                             .nodeBranchEntries(
                                 NodeBranchEntries.create().add( NodeBranchEntry.create().nodeId( params.getComment() ).nodePath( new NodePath("/some/path") ).build() ).build() )
                             .build() );

        final DeleteIssueCommentResult result = command.execute();

        assertNotNull( result );
        assertEquals( 1, result.getIds().getSize() );
        assertEquals( params.getComment(), result.getIds().first() );
    }

    @Test
    public void testNoCommentId()
    {
        final DeleteIssueCommentParams params = DeleteIssueCommentParams.create().build();
        final DeleteIssueCommentCommand command = createDeleteIssueCommentCommand( params );
        assertThrows(IllegalArgumentException.class, () -> command.execute());
    }

    private DeleteIssueCommentCommand createDeleteIssueCommentCommand( DeleteIssueCommentParams params )
    {
        return DeleteIssueCommentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build();
    }
}
