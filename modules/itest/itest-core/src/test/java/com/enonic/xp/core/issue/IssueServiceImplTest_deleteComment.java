package com.enonic.xp.core.issue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.User;

import static org.junit.Assert.*;

public class IssueServiceImplTest_deleteComment
    extends AbstractIssueServiceTest
{
    private Issue issue;

    private IssueComment comment;

    @Before
    public void setup()
    {
        this.issue = this.createIssue( CreateIssueParams.create().title( "issue-1" ) );
        final User creator = User.ANONYMOUS;
        this.comment = this.createComment( creator, "Comment One" );
    }

    @Test
    public void comments_delete()
        throws Exception
    {
        DeleteIssueCommentParams params = DeleteIssueCommentParams.create().
            comment( this.comment.getId() ).
            build();

        final DeleteIssueCommentResult result = this.issueService.deleteComment( params );

        assertNotNull( result );
        assertEquals( 1, result.getIds().getSize() );
    }

    @Test
    public void comments_deleteNotExisting()
        throws Exception
    {
        DeleteIssueCommentParams params = DeleteIssueCommentParams.create().
            comment( NodeId.from( UUID.randomUUID() ) ).
            build();

        final DeleteIssueCommentResult result = this.issueService.deleteComment( params );

        assertNotNull( result );
        assertEquals( 0, result.getIds().getSize() );
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
