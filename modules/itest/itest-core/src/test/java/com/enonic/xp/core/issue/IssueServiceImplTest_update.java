package com.enonic.xp.core.issue;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssuePath;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class IssueServiceImplTest_update
    extends AbstractIssueServiceTest
{

    @Test
    public void update()
        throws Exception
    {
        final Issue issue = this.createIssue();

        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().
            id( issue.getId() ).
            title( "updated title" ).
            description( "updated description" ).
            modifier( PrincipalKey.from( "user:myStore:modifier" ) ).
            addApproverId( PrincipalKey.from( "user:myStore:approver-1" ) ).
            addApproverId( PrincipalKey.from( "user:myStore:approver-2" ) ).
            addItemId( ContentId.from( "content-id1" ) ).
            addItemId( ContentId.from( "content-id2" ) ).
            status( IssueStatus.Closed ).
            build();

        final Issue updatedIssue = this.issueService.update( updateIssueParams );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( updatedIssue );
        assertEquals( "updated title", updatedIssue.getTitle() );
        assertEquals( "updated description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.Closed, updatedIssue.getStatus() );
        assertEquals( PrincipalKey.from( "user:myStore:me" ), updatedIssue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:modifier" ), updatedIssue.getModifier() );
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

        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().
            id( issue.getId() ).
            modifier( PrincipalKey.from( "user:myStore:modifier" ) ).
            build();

        final Issue updatedIssue = this.issueService.update( updateIssueParams );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( updatedIssue );
        assertEquals( "title", updatedIssue.getTitle() );
        assertEquals( "description", updatedIssue.getDescription() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:myStore:me" ), updatedIssue.getCreator() );
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

        final UpdateIssueParams updateIssueParams = UpdateIssueParams.create().
            id( issue.getId() ).
            title( "new title" ).
            modifier( PrincipalKey.from( "user:myStore:modifier" ) ).
            build();

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
            creator( PrincipalKey.from( "user:myStore:me" ) ).
            addApproverId( PrincipalKey.from( "user:myStore:approver-1" ) ).
            addItemId( ContentId.from( "content-id" ) ) );
    }
}
