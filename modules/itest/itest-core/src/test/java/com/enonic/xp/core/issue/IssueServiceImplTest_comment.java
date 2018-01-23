package com.enonic.xp.core.issue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class IssueServiceImplTest_comment
    extends AbstractIssueServiceTest
{
    @Test
    public void comment_issue()
        throws Exception
    {
        Issue issue = this.createIssue( CreateIssueParams.create().title( "issue-1" ) );

        final Instant created = Instant.now().minus( 1, ChronoUnit.MINUTES );
        final PrincipalKey creator = PrincipalKey.from( "user:store:me" );
        final String creatorDisplayName = "Me Myself";

        final CreateIssueCommentParams params = CreateIssueCommentParams.create().
            text( "text" ).
            issue( issue.getId() ).
            creator( creator ).
            creatorDisplayName( creatorDisplayName ).
            created( created ).
            build();

        final IssueComment comment = this.issueService.createComment( params );

        assertNotNull( comment );
        assertEquals( "text", comment.getText() );
        assertEquals( creator, comment.getCreator() );
        assertEquals( creatorDisplayName, comment.getCreatorDisplayName() );
        assertEquals( created, comment.getCreated() );
    }

    @Test(expected = NodeNotFoundException.class)
    public void comment_noIssue()
        throws Exception
    {
        final Instant created = Instant.now().minus( 1, ChronoUnit.MINUTES );
        final PrincipalKey creator = PrincipalKey.from( "user:store:me" );
        final String creatorDisplayName = "Me Myself";

        final CreateIssueCommentParams params = CreateIssueCommentParams.create().
            text( "text" ).
            issue( IssueId.create() ).
            creator( creator ).
            creatorDisplayName( creatorDisplayName ).
            created( created ).
            build();

        final IssueComment comment = this.issueService.createComment( params );
    }
}
