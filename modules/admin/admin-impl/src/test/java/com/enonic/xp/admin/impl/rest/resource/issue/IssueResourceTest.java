package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.text.StrSubstitutor;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.admin.impl.json.issue.PublishRequestItemJson;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;

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
        final CreateIssueJson params =
            new CreateIssueJson( "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createPublishRequest() );

        getResourceInstance().create( params );

        Mockito.verify( issueService, Mockito.times( 1 ) ).create( params.getCreateIssueParams() );

    }

    @Test
    public void test_get_stats()
        throws Exception
    {
        createLocalSession();

        final IssueResource issueResource = getResourceInstance();
        final FindIssuesResult result = FindIssuesResult.create().hits( 2 ).totalHits( 4 ).build();
        Mockito.when( issueService.findIssues( Mockito.any( IssueQuery.class ) ) ).thenReturn( result );

        issueResource.getStats();

        Mockito.verify( issueService, Mockito.times( 4 ) ).findIssues( Mockito.any( IssueQuery.class ) );
    }

    @Test
    public void test_list_issues()
        throws Exception
    {
        createLocalSession();

        final Issue issue = createIssue();
        final List<Issue> issues = Lists.newArrayList( issue );
        final IssueResource issueResource = getResourceInstance();
        final FindIssuesResult result = FindIssuesResult.create().hits( 2 ).totalHits( 4 ).issues( issues ).build();
        Mockito.when( issueService.findIssues( Mockito.any( IssueQuery.class ) ) ).thenReturn( result );

        issueResource.listIssues( "OPEN", 0, 10 );

        Mockito.verify( issueService, Mockito.times( 1 ) ).findIssues( Mockito.any( IssueQuery.class ) );
    }

    @Test
    public void test_getIssue()
        throws Exception
    {
        final Issue issue = createIssue();

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );

        final Map params = Maps.newHashMap();
        params.put( "id", issue.getId().toString() );

        final String expected = new StrSubstitutor( params ).replace( readFromFile( "get_issue_result.json" ) );

        String jsonString = request().path( "issue/id" ).
            queryParam( "id", issue.getId().toString() ).
            get().getAsString();

        assertStringJson( expected, jsonString );
    }

    @Test
    public void test_getIssues()
        throws Exception
    {
        final Issue issue = createIssue();

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );

        final Map params = Maps.newHashMap();
        params.put( "id", issue.getId().toString() );

        final String expected = new StrSubstitutor( params ).replace( readFromFile( "get_issues_result.json" ) );

        String jsonString = request().path( "issue/getIssues" ).
            entity( "{\"ids\":[\"" + issue.getId() + "\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertStringJson( expected, jsonString );
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
        publishRequestJson.setExcludeIds( new HashSet( Arrays.asList( "exclude-id" ) ) );
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

    private void createLocalSession()
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final LocalScope localScope = ContextAccessor.current().getLocalScope();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN ).build();
        localScope.setAttribute( authInfo );
        localScope.setSession( new SimpleSession( SessionKey.generate() ) );
    }
}
