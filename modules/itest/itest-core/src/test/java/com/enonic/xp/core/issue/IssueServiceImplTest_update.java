package com.enonic.xp.core.issue;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssuePath;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.name.NamePrettyfier;
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
                editMe.itemIds = ContentIds.from( ContentId.from( "content-id1" ), ContentId.from( "content-id2" ) );
                editMe.issueStatus = IssueStatus.Closed;
            } );

        final Issue updatedIssue = this.issueService.update( updateIssueParams );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( updatedIssue );
        assertEquals( "updated title", updatedIssue.getTitle() );
        assertEquals( "updated description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.Closed, updatedIssue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getModifier() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), updatedIssue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id1" ), updatedIssue.getItemIds().first() );
        assertEquals( issueName, updatedIssue.getName() );
        assertEquals( IssuePath.from( issueName ), updatedIssue.getPath() );
        assertNotEquals( updatedIssue.getCreatedTime(), updatedIssue.getModifiedTime() );
    }

    @Test
    public void nothing_updated()
        throws Exception
    {
        final Issue issue = this.createIssue();

        final UpdateIssueParams updateIssueParams = new UpdateIssueParams().id( issue.getId() );

        final Issue updatedIssue = this.issueService.update( updateIssueParams );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( updatedIssue );
        assertEquals( "title", updatedIssue.getTitle() );
        assertEquals( "description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), updatedIssue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), updatedIssue.getItemIds().first() );
        assertEquals( issueName, updatedIssue.getName() );
        assertEquals( IssuePath.from( issueName ), updatedIssue.getPath() );
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
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( updatedIssue );
        assertEquals( "new title", updatedIssue.getTitle() );
        assertEquals( issueName, updatedIssue.getName() );
    }

    private Issue createIssue()
    {
        return this.createIssue( CreateIssueParams.create().
            title( "title" ).
            description( "description" ).
            addApproverId( PrincipalKey.from( "user:myStore:approver-1" ) ).
            addItemId( ContentId.from( "content-id" ) ) );
    }
}
