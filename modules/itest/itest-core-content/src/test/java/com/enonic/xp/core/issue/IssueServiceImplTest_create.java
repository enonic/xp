package com.enonic.xp.core.issue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.issue.IssueNameFactory;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.CreatePublishRequestIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestIssue;
import com.enonic.xp.issue.PublishRequestIssueSchedule;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class IssueServiceImplTest_create
    extends AbstractIssueServiceTest
{
    @Test
    void create_issue()
    {
        final CreateIssueParams params = CreateIssueParams.create().
            title( "title" ).
            description( "description" ).
            setApproverIds( PrincipalKeys.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).
            build();

        final Issue issue = this.issueService.create( params );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), issue.getPublishRequest().getItems().first().getId() );
        assertEquals( ContentId.from( "exclude-id" ), issue.getPublishRequest().getExcludeIds().first() );
        assertEquals( IssueNameFactory.create( issue.getIndex() ), issue.getName() );
    }

    @Test
    void create_publish_request_issue()
    {
        Instant from = Instant.ofEpochSecond( 1561965725L );
        Instant to = Instant.ofEpochSecond( 1575184925L );

        final CreatePublishRequestIssueParams params = CreatePublishRequestIssueParams.create().
            title( "title" ).
            description( "description" ).
            setApproverIds( PrincipalKeys.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).
            schedule( PublishRequestIssueSchedule.create().to( to ).from( from ).build() ).
            build();

        final PublishRequestIssue issue = (PublishRequestIssue) this.issueService.create( params );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), issue.getPublishRequest().getItems().first().getId() );
        assertEquals( ContentId.from( "exclude-id" ), issue.getPublishRequest().getExcludeIds().first() );
        assertEquals( IssueNameFactory.create( issue.getIndex() ), issue.getName() );
        assertEquals( from, issue.getSchedule().getFrom() );
        assertEquals( to, issue.getSchedule().getTo() );
    }

    @Test
    void create_publish_request_issue_without_schedule()
    {
        final CreatePublishRequestIssueParams params = CreatePublishRequestIssueParams.create().
            title( "title" ).
            description( "description" ).
            setApproverIds( PrincipalKeys.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).
            build();

        final PublishRequestIssue issue = (PublishRequestIssue) this.issueService.create( params );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), issue.getPublishRequest().getItems().first().getId() );
        assertEquals( ContentId.from( "exclude-id" ), issue.getPublishRequest().getExcludeIds().first() );
        assertEquals( IssueNameFactory.create( issue.getIndex() ), issue.getName() );
        assertNull( issue.getSchedule() );
    }
}
