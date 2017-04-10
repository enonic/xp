package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.json.issue.PublishRequestItemJson;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;

public class IssueResourceTest
    extends AdminResourceTestSupport
{
    private IssueService issueService;

    @Override
    protected IssueResource getResourceInstance()
    {
        final IssueResource resource = new IssueResource();

        issueService = Mockito.mock( IssueService.class );

        resource.setIssueService( issueService );

        return resource;
    }

    @Test
    public void test_create()
        throws Exception
    {
        final CreateIssueJson params = new CreateIssueJson( "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createPublishRequest() );

        getResourceInstance().create( params );

        Mockito.verify( issueService, Mockito.times( 1 ) ).create( params.getCreateIssueParams() );

    }

    @Test
    public void test_getIssues()
        throws Exception
    {
        final Issue issue = createIssue();

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );

        String jsonString = request().path( "issue/getIssues" ).
            entity( "{\"ids\":[\"" + issue.getId() + "\",\"" + UUID.randomUUID() + "\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "get_issues_result.json", jsonString );
    }

    @Test
    public void test_update()
    {
        final Issue issue = createIssue();

        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Status", Arrays.asList( "approver-1" ),
                                 createPublishRequest() );

        getResourceInstance().update( params );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( params.getUpdateIssueParams() );
    }


    private PublishRequestJson createPublishRequest()
    {
        final PublishRequestItemJson publishRequestItemJson = new PublishRequestItemJson( PublishRequestItem.create().
            includeChildren( true ).
            id( ContentId.from( "content-id" ) ).build() );

        final PublishRequestJson publishRequestJson = new PublishRequestJson();

        publishRequestJson.setItems( new HashSet( Arrays.asList( publishRequestItemJson ) ) );
        publishRequestJson.setExcludeIds( new HashSet( Arrays.asList( "exclude-id"  ) ) );
        return publishRequestJson;
    }

    private Issue createIssue()
    {
        return Issue.create().addApproverId( PrincipalKey.from( "user:system:anonymous" ) ).
            title( "title" ).
            description( "desc" ).creator( User.ANONYMOUS.getKey() ).modifier( User.ANONYMOUS.getKey() ).
            setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).build();

    }
}
