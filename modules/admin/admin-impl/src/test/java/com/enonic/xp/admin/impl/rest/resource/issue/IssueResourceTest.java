package com.enonic.xp.admin.impl.rest.resource.issue;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.text.StrSubstitutor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.admin.impl.json.issue.IssueStatsJson;
import com.enonic.xp.admin.impl.json.issue.PublishRequestItemJson;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CommentJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.ListIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.issue.Comment;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;

import static org.junit.Assert.*;

public class IssueResourceTest
    extends AdminResourceTestSupport
{
    private IssueService issueService;

    private IssueNotificationsSender issueNotificationsSender;

    private SecurityService securityService;

    @Override
    protected IssueResource getResourceInstance()
    {
        final IssueResource resource = new IssueResource();

        issueService = Mockito.mock( IssueService.class );
        issueNotificationsSender = Mockito.mock( IssueNotificationsSender.class );
        securityService = Mockito.mock( SecurityService.class );

        resource.setIssueService( issueService );
        resource.setIssueNotificationsSender( issueNotificationsSender );
        resource.setSecurityService( securityService );

        return resource;
    }

    @Test
    public void test_create()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createComments(),
                                 createPublishRequest() );

        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final Role role = Role.create().key( RoleKeys.CONTENT_MANAGER_APP ).displayName( "dname" ).build();
        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        issueResource.create( params, request );

        Mockito.verify( issueService, Mockito.times( 1 ) ).create( Mockito.any( CreateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueCreated( Mockito.any( Issue.class ),
                                                                                           Mockito.anyString() );
    }

    @Test
    public void verifyValidAssigneeNotFiltered()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createComments(),
                                 createPublishRequest() );

        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final Role role = Role.create().key( RoleKeys.CONTENT_MANAGER_EXPERT ).displayName( "dname" ).build();
        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        ArgumentCaptor<CreateIssueParams> issueParamsArgumentCaptor = ArgumentCaptor.forClass( CreateIssueParams.class );
        issueResource.create( params, request );
        Mockito.verify( issueService ).create( issueParamsArgumentCaptor.capture() );

        assertTrue( issueParamsArgumentCaptor.getValue().getApproverIds().isNotEmpty() );
    }

    @Test
    public void verifyInvalidAssigneeFiltered()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createComments(),
                                 createPublishRequest() );

        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final Role role = Role.create().key( RoleKeys.EVERYONE ).displayName( "dname" ).build();
        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        ArgumentCaptor<CreateIssueParams> issueParamsArgumentCaptor = ArgumentCaptor.forClass( CreateIssueParams.class );
        issueResource.create( params, request );
        Mockito.verify( issueService ).create( issueParamsArgumentCaptor.capture() );

        assertTrue( issueParamsArgumentCaptor.getValue().getApproverIds().isEmpty() );
    }

    @Test
    public void verifyOnlyInvalidAssigneeFiltered()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString(), User.ANONYMOUS.getKey().toString() ),
                                 createComments(), createPublishRequest() );

        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final Role role1 = Role.create().key( RoleKeys.EVERYONE ).displayName( "dname" ).build();
        final Role role2 = Role.create().key( RoleKeys.CONTENT_MANAGER_ADMIN ).displayName( "dname" ).build();
        Mockito.when( securityService.getPrincipals( Mockito.any() ) ).thenReturn( Principals.from( role1 ), Principals.from( role2 ) );
        ArgumentCaptor<CreateIssueParams> issueParamsArgumentCaptor = ArgumentCaptor.forClass( CreateIssueParams.class );
        issueResource.create( params, request );
        Mockito.verify( issueService ).create( issueParamsArgumentCaptor.capture() );

        assertTrue( issueParamsArgumentCaptor.getValue().getApproverIds().getSize() == 1 );
    }

    @Test
    public void test_getStats()
        throws Exception
    {
        createLocalSession();
        final FindIssuesResult findIssuesResult = FindIssuesResult.create().hits( 2 ).totalHits( 4 ).build();
        final IssueResource issueResource = getResourceInstance();
        Mockito.when( issueService.findIssues( Mockito.any( IssueQuery.class ) ) ).thenReturn( findIssuesResult );
        final IssueStatsJson result = issueResource.getStats();

        assertNotNull( result );
        Mockito.verify( issueService, Mockito.times( 6 ) ).findIssues( Mockito.any( IssueQuery.class ) );
    }

    @Test
    public void test_list_issues()
        throws Exception
    {
        createLocalSession();

        final Instant createdTime = Instant.now();
        final Issue issue = createIssue( createdTime );
        ;
        final List<Issue> issues = Lists.newArrayList( issue );
        final IssueResource issueResource = getResourceInstance();
        final FindIssuesResult result = FindIssuesResult.create().hits( 2 ).totalHits( 4 ).issues( issues ).build();
        Mockito.when( issueService.findIssues( Mockito.any( IssueQuery.class ) ) ).thenReturn( result );
        Mockito.when( securityService.getUser( Mockito.any( PrincipalKey.class ) ) ).thenReturn( Optional.empty() );

        issueResource.listIssues( new ListIssuesJson( "OPEN", true, true, true, 0, 10 ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).findIssues( Mockito.any( IssueQuery.class ) );
    }

    @Test
    public void test_getIssue()
        throws Exception
    {
        final Instant createdTime = Instant.now();
        final Issue issue = createIssue( createdTime );

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );

        final Map params = Maps.newHashMap();
        params.put( "id", issue.getId().toString() );
        params.put( "createdTime", createdTime );

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
        final Instant createdTime = Instant.now();
        final Issue issue = createIssue( createdTime );

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );

        final Map params = Maps.newHashMap();
        params.put( "id", issue.getId().toString() );
        params.put( "createdTime", createdTime );

        final String expected = new StrSubstitutor( params ).replace( readFromFile( "get_issues_result.json" ) );

        String jsonString = request().path( "issue/getIssues" ).
            entity( "{\"ids\":[\"" + issue.getId() + "\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertStringJson( expected, jsonString );
    }

    @Test
    public void test_update()
    {
        final Instant createdTime = Instant.now();
        final Issue issue = createIssue( createdTime );

        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Open", false, false, Arrays.asList( "user:system:admin" ),
                                 createComments(), createPublishRequest() );

        getResourceInstance().update( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( Mockito.any( UpdateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueUpdated( Mockito.any( Issue.class ),
                                                                                           Mockito.anyString() );
    }

    @Test
    public void test_update_is_publish()
    {
        final Instant createdTime = Instant.now();
        final Issue issue = createIssue( createdTime );

        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Closed", true, false, Arrays.asList( "user:system:admin" ),
                                 createComments(), createPublishRequest() );

        getResourceInstance().update( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( Mockito.any( UpdateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssuePublished( Mockito.any( Issue.class ),
                                                                                             Mockito.anyString() );
    }

    @Test
    public void test_update_is_autoSave()
    {
        final Instant createdTime = Instant.now();
        final Issue issue = createIssue( createdTime );

        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Closed", true, true, Arrays.asList( "user:system:admin" ),
                                 createComments(), createPublishRequest() );

        getResourceInstance().update( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( Mockito.any( UpdateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 0 ) ).notifyIssueUpdated( Mockito.any( Issue.class ),
                                                                                           Mockito.anyString() );
        Mockito.verify( issueNotificationsSender, Mockito.times( 0 ) ).notifyIssuePublished( Mockito.any( Issue.class ),
                                                                                             Mockito.anyString() );
    }

    private List<CommentJson> createComments()
    {
        return Lists.newArrayList( new CommentJson( "user:system:anonymous", "Comment text one", Instant.now().toString() ) );
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

    private Issue createIssue( final Instant createdTime )
    {
        Set<Comment> comments = Sets.newHashSet( new Comment( User.ANONYMOUS.getKey(), "Comment text one", createdTime ) );
        return Issue.create().
            addApproverId( PrincipalKey.from( "user:system:anonymous" ) ).
            addComments( comments ).
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
