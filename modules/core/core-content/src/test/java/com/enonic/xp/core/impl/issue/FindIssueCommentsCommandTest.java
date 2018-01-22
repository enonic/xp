package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.issue.FindIssueCommentsResult;
import com.enonic.xp.issue.IssueCommentQuery;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class FindIssueCommentsCommandTest
{
    private NodeService nodeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    public void testFindIssues()
        throws Exception
    {
        final IssueName issueName = IssueName.from( "issue-1" );
        final PrincipalKey creator = PrincipalKey.from( "user:store:one" );
        final IssueCommentQuery commentQuery = IssueCommentQuery.create().
            from( 0 ).
            size( 20 ).
            issueName( issueName ).
            creator( creator ).
            build();
        final FindIssueCommentsCommand command = createCommand( commentQuery );

        Mockito.when( nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).thenReturn(
            FindNodesByQueryResult.create().hits( 20 ).totalHits( 40 ).build() );

        Mockito.when( nodeService.getByIds( Mockito.any( NodeIds.class ) ) ).thenReturn(
            Nodes.from( IssueCommentNodeTranslatorTest.createNode( Instant.now() ) ) );

        FindIssueCommentsResult result = command.execute();

        Mockito.verify( nodeService, Mockito.times( 1 ) ).findByQuery( Mockito.any( NodeQuery.class ) );
        Mockito.verify( nodeService, Mockito.times( 1 ) ).getByIds( Mockito.any( NodeIds.class ) );

        assertEquals( 20, result.getHits() );
        assertEquals( 40, result.getTotalHits() );
        assertEquals( 1, result.getIssueComments().size() );
    }

    private FindIssueCommentsCommand createCommand( final IssueCommentQuery query )
    {
        return FindIssueCommentsCommand.create().
            query( query ).
            nodeService( nodeService ).
            build();
    }
}
