package com.enonic.xp.core.issue;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssuePath;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class IssueServiceImplTest_getIssue
    extends AbstractIssueServiceTest
{

    @Test
    public void get_issue()
        throws Exception
    {
        final IssueId issueId = this.createIssue( CreateIssueParams.create().
            title( "title" ).
            description( "description" ).
            creator( PrincipalKey.from( "user:myStore:me" ) ).
            addApproverId( PrincipalKey.from( "user:myStore:approver-1" ) ).
            addItemId( ContentId.from( "content-id" ) ) ).getId();

        final Issue issue = this.issueService.getIssue( issueId );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:myStore:me" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), issue.getItemIds().first() );
        assertEquals( issueName, issue.getName() );
        assertEquals( IssuePath.from( issueName ), issue.getPath() );
    }
}
