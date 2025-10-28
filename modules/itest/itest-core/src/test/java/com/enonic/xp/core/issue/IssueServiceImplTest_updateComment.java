package com.enonic.xp.core.issue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssueServiceImplTest_updateComment
    extends AbstractIssueServiceTest
{
    @Test
    void updateComment()
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

        final IssueComment updatedComment =
            this.issueService.updateComment( UpdateIssueCommentParams.create().comment( comment.getId() ).text( "updated text" ).build() );

        assertNotNull( updatedComment );
        assertEquals( "updated text", updatedComment.getText() );
        assertEquals( creator, updatedComment.getCreator() );
        assertEquals( creatorDisplayName, updatedComment.getCreatorDisplayName() );
        assertEquals( created, updatedComment.getCreated() );
    }

    @Test
    void udpateComment_noComment()
    {
        final UpdateIssueCommentParams params = UpdateIssueCommentParams.create().
            text( "text" ).
            comment( NodeId.from( UUID.randomUUID() ) ).
            build();

        assertThrows(NodeNotFoundException.class, () ->  this.issueService.updateComment( params ));
    }
}
