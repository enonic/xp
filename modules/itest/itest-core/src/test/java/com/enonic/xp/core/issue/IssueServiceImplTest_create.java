package com.enonic.xp.core.issue;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.issue.IssueNameFactory;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
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
            setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).
            build();

        final Issue issue = this.issueService.create( params );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:system:test-user" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id" ), issue.getPublishRequest().getItems().first().getId() );
        assertEquals( ContentId.from( "exclude-id" ), issue.getPublishRequest().getExcludeIds().first() );
        assertEquals( IssueNameFactory.create( issue.getIndex() ), issue.getName() );
    }
}
