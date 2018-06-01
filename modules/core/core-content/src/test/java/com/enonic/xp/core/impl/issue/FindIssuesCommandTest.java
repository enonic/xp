package com.enonic.xp.core.impl.issue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;

import static org.junit.Assert.*;

public class FindIssuesCommandTest
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
        final IssueQuery issueQuery = IssueQuery.create().from( 0 ).size( 20 ).status( IssueStatus.OPEN ).build();
        final FindIssuesCommand command = createCommand( issueQuery );

        Mockito.when( nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).thenReturn(
            FindNodesByQueryResult.create().hits( 20 ).totalHits( 40 ).build() );
        Mockito.when( nodeService.getByIds( Mockito.any( NodeIds.class ) ) ).thenReturn(
            Nodes.from( IssueNodeTranslatorTest.createNode() ) );

        FindIssuesResult result = command.execute();

        Mockito.verify( nodeService, Mockito.times( 1 ) ).findByQuery( Mockito.any( NodeQuery.class ) );
        Mockito.verify( nodeService, Mockito.times( 1 ) ).getByIds( Mockito.any( NodeIds.class ) );

        assertEquals( 20, result.getHits() );
        assertEquals( 40, result.getTotalHits() );
        assertEquals( 1, result.getIssues().size() );
    }

    @Test
    public void testFindIssuesByItems()
        throws Exception
    {
        final IssueQuery issueQuery =
            IssueQuery.create().from( 0 ).size( 20 ).status( IssueStatus.OPEN ).items( ContentIds.from( "content-id" ) ).build();
        final FindIssuesCommand command = createCommand( issueQuery );

        Mockito.when( nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).thenReturn(
            FindNodesByQueryResult.create().hits( 20 ).totalHits( 40 ).build() );
        Mockito.when( nodeService.getByIds( Mockito.any( NodeIds.class ) ) ).thenReturn( Nodes.from( IssueNodeTranslatorTest.createNode() ) );

        FindIssuesResult result = command.execute();

        Mockito.verify( nodeService, Mockito.times( 1 ) ).findByQuery( Mockito.any( NodeQuery.class ) );
        Mockito.verify( nodeService, Mockito.times( 1 ) ).getByIds( Mockito.any( NodeIds.class ) );

        assertEquals( 20, result.getHits() );
        assertEquals( 40, result.getTotalHits() );
        assertEquals( 1, result.getIssues().size() );
    }

    private FindIssuesCommand createCommand( final IssueQuery query )
    {
        return FindIssuesCommand.create().
            query( query ).
            nodeService( nodeService ).
            build();
    }
}
