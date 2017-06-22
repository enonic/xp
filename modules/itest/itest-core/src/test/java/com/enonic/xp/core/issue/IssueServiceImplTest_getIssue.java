package com.enonic.xp.core.issue;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.issue.IssueNameFactory;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
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
            addApproverId( PrincipalKey.from( "user:myStore:approver-1" ) ).
            setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ) ).getId();

        final Issue issue = this.issueService.getIssue( issueId );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), issue.getPublishRequest().getItems().first().getId() );
        assertEquals( IssueNameFactory.create( issue.getIndex() ), issue.getName() );
    }
}
