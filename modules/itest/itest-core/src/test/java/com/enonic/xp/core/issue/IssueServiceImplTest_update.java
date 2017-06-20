package com.enonic.xp.core.issue;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.issue.IssueNameFactory;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.Assert.*;

public class IssueServiceImplTest_update
    extends AbstractIssueServiceTest
{

    @Test
    public void update()
        throws Exception
    {
        final Issue issue = this.createIssue();

        final UpdateIssueParams updateIssueParams = new UpdateIssueParams().
            id( issue.getId() ).
            editor( editMe -> {
                editMe.title = "updated title";
                editMe.description = "updated description";
                editMe.approverIds =
                    PrincipalKeys.from( PrincipalKey.from( "user:myStore:approver-1" ), PrincipalKey.from( "user:myStore:approver-2" ) );
                editMe.publishRequest = PublishRequest.create().addExcludeId( ContentId.from( "new-exclude-id" ) ).addItem(
                    PublishRequestItem.create().id( ContentId.from( "new-content-id" ) ).includeChildren( true ).build() ).build();
                editMe.issueStatus = IssueStatus.Closed;
            } );

        final Issue updatedIssue = this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "updated title", updatedIssue.getTitle() );
        assertEquals( "updated description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.Closed, updatedIssue.getStatus() );
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
    public void nothing_updated()
        throws Exception
    {
        final Issue issue = this.createIssue();

        final UpdateIssueParams updateIssueParams = new UpdateIssueParams().id( issue.getId() );

        final Issue updatedIssue = this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "title", updatedIssue.getTitle() );
        assertEquals( "description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), updatedIssue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), updatedIssue.getPublishRequest().getItems().first().getId() );
        assertEquals( IssueNameFactory.create( updatedIssue.getIndex() ), updatedIssue.getName() );
    }

    @Test
    public void test_name_does_not_get_updated()
        throws Exception
    {
        final Issue issue = this.createIssue();

        final UpdateIssueParams updateIssueParams = new UpdateIssueParams().
            id( issue.getId() ).
            editor( edit -> edit.title = "new title" );

        final Issue updatedIssue = this.issueService.update( updateIssueParams );

        assertNotNull( updatedIssue );
        assertEquals( "new title", updatedIssue.getTitle() );
        assertEquals( IssueNameFactory.create( updatedIssue.getIndex() ), updatedIssue.getName() );
    }

    private Issue createIssue()
    {
        return this.createIssue( CreateIssueParams.create().
            title( "title" ).
            description( "description" ).
            addApproverId( PrincipalKey.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ) );
    }
}
