package com.enonic.xp.core.issue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.issue.IssueNameFactory;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.CreatePublishRequestIssueParams;
import com.enonic.xp.issue.EditablePublishRequestIssue;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestIssue;
import com.enonic.xp.issue.PublishRequestIssueSchedule;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class IssueServiceImplTest_update
    extends AbstractIssueServiceTest
{

    @Test
    void update()
    {
        final Instant createdTime = Instant.now();
        final Issue issue = this.createIssue( createdTime );

        final PrincipalKey commentatorKey = PrincipalKey.from( "user:myStore:commentator-1" );
        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().
            id( issue.getId() ).
            editor( editMe -> {
                editMe.title = "updated title";
                editMe.description = "updated description";
                editMe.approverIds =
                    PrincipalKeys.from( PrincipalKey.from( "user:myStore:approver-1" ), PrincipalKey.from( "user:myStore:approver-2" ) );
                editMe.publishRequest = PublishRequest.create().addExcludeId( ContentId.from( "new-exclude-id" ) ).addItem(
                    PublishRequestItem.create().id( ContentId.from( "new-content-id" ) ).includeChildren( true ).build() ).build();
                editMe.issueStatus = IssueStatus.CLOSED;
            } ).build();

        final Issue updatedIssue = this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "updated title", updatedIssue.getTitle() );
        assertEquals( "updated description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.CLOSED, updatedIssue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getModifier() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), updatedIssue.getApproverIds().first() );
        assertEquals( ContentId.from( "new-exclude-id" ), updatedIssue.getPublishRequest().getExcludeIds().first() );
        assertEquals( ContentId.from( "new-content-id" ), updatedIssue.getPublishRequest().getItems().first().getId() );
        assertEquals( true, updatedIssue.getPublishRequest().getItems().first().getIncludeChildren() );
        assertEquals( IssueNameFactory.create( updatedIssue.getIndex() ), updatedIssue.getName() );
        assertNotEquals( updatedIssue.getCreatedTime(), updatedIssue.getModifiedTime() );
    }

    @Test
    void update_publish_request_issue()
    {
        Instant from = Instant.ofEpochSecond( 1561965725L );
        Instant to = Instant.ofEpochSecond( 1575184925L );

        final PublishRequestIssue issue = (PublishRequestIssue) this.issueService.create( CreatePublishRequestIssueParams.create().
            title( "title" ).
            description( "description" ).
            setApproverIds( PrincipalKeys.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).
            schedule( PublishRequestIssueSchedule.create().from( from ).to( to ).build() ).
            build() );

        Instant newFrom = from.plus( 4, ChronoUnit.DAYS );
        Instant newTo = to.plus( 8, ChronoUnit.DAYS );

        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().
            id( issue.getId() ).
            editor( editMe -> {
                EditablePublishRequestIssue edit = (EditablePublishRequestIssue) editMe;
                edit.title = "updated title";
                edit.description = "updated description";
                edit.approverIds =
                    PrincipalKeys.from( PrincipalKey.from( "user:myStore:approver-1" ), PrincipalKey.from( "user:myStore:approver-2" ) );
                edit.publishRequest = PublishRequest.create().addExcludeId( ContentId.from( "new-exclude-id" ) ).addItem(
                    PublishRequestItem.create().id( ContentId.from( "new-content-id" ) ).includeChildren( true ).build() ).build();
                edit.issueStatus = IssueStatus.CLOSED;
                edit.schedule = PublishRequestIssueSchedule.create().
                    from( newFrom ).
                    to( newTo ).
                    build();
            } ).build();

        final PublishRequestIssue updatedIssue = (PublishRequestIssue) this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "updated title", updatedIssue.getTitle() );
        assertEquals( "updated description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.CLOSED, updatedIssue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getModifier() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), updatedIssue.getApproverIds().first() );
        assertEquals( ContentId.from( "new-exclude-id" ), updatedIssue.getPublishRequest().getExcludeIds().first() );
        assertEquals( ContentId.from( "new-content-id" ), updatedIssue.getPublishRequest().getItems().first().getId() );
        assertEquals( true, updatedIssue.getPublishRequest().getItems().first().getIncludeChildren() );
        assertEquals( IssueNameFactory.create( updatedIssue.getIndex() ), updatedIssue.getName() );
        assertNotEquals( updatedIssue.getCreatedTime(), updatedIssue.getModifiedTime() );
        assertEquals( newFrom, updatedIssue.getSchedule().getFrom() );
        assertEquals( newTo, updatedIssue.getSchedule().getTo() );
    }

    @Test
    void update_publish_request_issue_without_schedule()
    {

        final PublishRequestIssue issue = (PublishRequestIssue) this.issueService.create( CreatePublishRequestIssueParams.create().
            title( "title" ).
            description( "description" ).
            setApproverIds( PrincipalKeys.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).
            build() );

        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().
            id( issue.getId() ).
            editor( editMe -> {
                EditablePublishRequestIssue edit = (EditablePublishRequestIssue) editMe;
                edit.title = "updated title";
                edit.description = "updated description";
                edit.approverIds =
                    PrincipalKeys.from( PrincipalKey.from( "user:myStore:approver-1" ), PrincipalKey.from( "user:myStore:approver-2" ) );
                edit.publishRequest = PublishRequest.create().addExcludeId( ContentId.from( "new-exclude-id" ) ).addItem(
                    PublishRequestItem.create().id( ContentId.from( "new-content-id" ) ).includeChildren( true ).build() ).build();
                edit.issueStatus = IssueStatus.CLOSED;
            } ).build();

        final PublishRequestIssue updatedIssue = (PublishRequestIssue) this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "updated title", updatedIssue.getTitle() );
        assertEquals( "updated description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.CLOSED, updatedIssue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getModifier() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), updatedIssue.getApproverIds().first() );
        assertEquals( ContentId.from( "new-exclude-id" ), updatedIssue.getPublishRequest().getExcludeIds().first() );
        assertEquals( ContentId.from( "new-content-id" ), updatedIssue.getPublishRequest().getItems().first().getId() );
        assertEquals( true, updatedIssue.getPublishRequest().getItems().first().getIncludeChildren() );
        assertEquals( IssueNameFactory.create( updatedIssue.getIndex() ), updatedIssue.getName() );
        assertNotEquals( updatedIssue.getCreatedTime(), updatedIssue.getModifiedTime() );
        assertNull( updatedIssue.getSchedule() );
    }

    @Test
    void nothing_updated()
    {
        final Instant createdTime = Instant.now();
        final Issue issue = this.createIssue( createdTime );

        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().id( issue.getId() ).build();

        final Issue updatedIssue = this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "title", updatedIssue.getTitle() );
        assertEquals( "description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), updatedIssue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), updatedIssue.getPublishRequest().getItems().first().getId() );
        assertEquals( IssueNameFactory.create( updatedIssue.getIndex() ), updatedIssue.getName() );
    }

    @Test
    void test_name_does_not_get_updated()
    {
        final Instant createdTime = Instant.now();
        final Issue issue = this.createIssue( createdTime );

        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().
            id( issue.getId() ).
            editor( edit -> edit.title = "new title" ).
            build();

        final Issue updatedIssue = this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "new title", updatedIssue.getTitle() );
        assertEquals( IssueNameFactory.create( updatedIssue.getIndex() ), updatedIssue.getName() );
    }

    private Issue createIssue( Instant createdTime )
    {
        return this.createIssue( CreateIssueParams.create().
            title( "title" ).
            description( "description" ).
            setApproverIds( PrincipalKeys.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ) );
    }
}
