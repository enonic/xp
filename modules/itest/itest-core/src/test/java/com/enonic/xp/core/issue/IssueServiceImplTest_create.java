package com.enonic.xp.core.issue;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssuePath;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class IssueServiceImplTest_create
    extends AbstractIssueServiceTest
{

    @Test
    public void create_issue()
        throws Exception
    {
        final CreateIssueParams params = CreateIssueParams.create().
            title( "title" ).
            description( "description" ).
            addApproverId( PrincipalKey.from( "user:myStore:approver-1" ) ).
            addItemId( ContentId.from( "content-id" ) ).
            build();

        final Issue issue = this.issueService.create( params );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), issue.getItemIds().first() );
        assertEquals( issueName, issue.getName() );
        assertEquals( IssuePath.from( issueName ), issue.getPath() );
    }
}
