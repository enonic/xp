package com.enonic.xp.core.issue;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.FindIssueCommentsResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueCommentQuery;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;

import static org.junit.Assert.*;

public class IssueServiceImplTest_findComments
    extends AbstractIssueServiceTest
{
    private Issue issue;

    @Before
    public void setup()
    {
        this.issue = this.createIssue( CreateIssueParams.create().title( "issue-1" ) );
        final User creator = User.ANONYMOUS;
        final User creator2 = User.create().
            key( PrincipalKey.from( "user:store:user2" ) ).
            login( "user2" ).
            email( "user2@email.com" ).
            displayName( "User 2" ).
            build();
        this.createComment( creator, "Comment One" );
        this.createComment( creator, "Comment Two" );
        this.createComment( creator2, "Another Comment" );
    }

    @Test
    public void comments_find()
        throws Exception
    {
        IssueCommentQuery query = IssueCommentQuery.create().
            issue( this.issue.getId() ).
            build();

        final FindIssueCommentsResult result = this.issueService.findComments( query );

        assertNotNull( result );
        assertEquals( 3, result.getHits() );
    }

    @Test
    public void comments_findByUser()
        throws Exception
    {
        IssueCommentQuery query = IssueCommentQuery.create().
            issue( this.issue.getId() ).
            creator( User.ANONYMOUS.getKey() ).
            build();

        final FindIssueCommentsResult result = this.issueService.findComments( query );

        assertNotNull( result );
        assertEquals( 2, result.getHits() );
        assertTrue( result.getIssueComments().stream().allMatch( c -> c.getCreator().equals( User.ANONYMOUS.getKey() ) ) );
    }

    private IssueComment createComment( final User creator, final String text )
    {
        CreateIssueCommentParams params = CreateIssueCommentParams.create().
            issue( this.issue.getId() ).
            text( text ).
            creator( creator.getKey() ).
            creatorDisplayName( creator.getDisplayName() ).
            build();

        return this.issueService.createComment( params );
    }
}
