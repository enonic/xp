package com.enonic.xp.core.issue;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;

import static org.junit.Assert.*;

public class IssueServiceImplTest_findIssues
    extends AbstractIssueServiceTest
{

    @Before
    public void setup()
    {
        createIssue( 1, 1, 2 );
        createIssue( 2, 1, 3 );
        createIssue( 3, 2, 4 );
    }

    @Test
    public void find()
        throws Exception
    {
        IssueQuery query = IssueQuery.create().build();

        final FindIssuesResult result = this.issueService.findIssues( query );

        assertNotNull( result );
        assertEquals( 3, result.getHits() );
    }

    @Test
    public void findByContentId1()
        throws Exception
    {
        ContentId contentId = ContentId.from( "issue-item-1" );

        IssueQuery query = IssueQuery.create().
            items( ContentIds.from( contentId ) ).
            build();

        final FindIssuesResult result = this.issueService.findIssues( query );

        assertNotNull( result );
        assertEquals( 2, result.getHits() );
        assertTrue( result.getIssues().stream().allMatch(
            issue -> issue.getPublishRequest().getItems().stream().anyMatch( item -> item.getId().equals( contentId ) ) ) );
    }

    @Test
    public void findByContentId2()
        throws Exception
    {
        ContentId contentId = ContentId.from( "issue-item-2" );

        IssueQuery query = IssueQuery.create().
            items( ContentIds.from( contentId ) ).
            build();

        final FindIssuesResult result = this.issueService.findIssues( query );

        assertNotNull( result );
        assertEquals( 3, result.getHits() );
        assertTrue( result.getIssues().stream().allMatch(
            issue -> issue.getPublishRequest().getItems().stream().anyMatch( item -> item.getId().equals( contentId ) ) ) );
    }

    @Test
    public void findByContentId4()
        throws Exception
    {
        ContentId contentId = ContentId.from( "issue-item-4" );

        IssueQuery query = IssueQuery.create().
            items( ContentIds.from( contentId ) ).
            build();

        final FindIssuesResult result = this.issueService.findIssues( query );

        assertNotNull( result );
        assertEquals( 1, result.getHits() );
        assertTrue( result.getIssues().stream().allMatch(
            issue -> issue.getPublishRequest().getItems().stream().anyMatch( item -> item.getId().equals( contentId ) ) ) );
    }

    private Issue createIssue( final int issueNum, final int itemStart, final int itemEnd )
    {
        PublishRequest.Builder publishRequest = PublishRequest.create();
        for ( int i = itemStart; i <= itemEnd; i++ )
        {
            publishRequest.addItem( createPublishItem( String.format( "issue-item-%d", i ) ) );
        }

        CreateIssueParams.Builder builder = CreateIssueParams.create().
            title( String.format( "issue-%d", issueNum ) ).
            setPublishRequest( publishRequest.build() );

        return this.createIssue( builder );
    }

    private PublishRequestItem createPublishItem( final String contentId )
    {
        return PublishRequestItem.create().
            id( ContentId.from( contentId ) ).
            build();
    }
}
