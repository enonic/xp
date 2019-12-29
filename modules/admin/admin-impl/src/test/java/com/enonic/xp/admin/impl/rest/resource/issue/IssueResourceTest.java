package com.enonic.xp.admin.impl.rest.resource.issue;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.text.StrSubstitutor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.enonic.xp.admin.impl.json.issue.DeleteIssueCommentResultJson;
import com.enonic.xp.admin.impl.json.issue.IssueCommentJson;
import com.enonic.xp.admin.impl.json.issue.IssueStatsJson;
import com.enonic.xp.admin.impl.json.issue.PublishRequestItemJson;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestScheduleJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CountStatsJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueCommentJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.DeleteIssueCommentJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.ListIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueCommentJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.CreatePublishRequestIssueParams;
import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.issue.FindIssueCommentsResult;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueCommentQuery;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueNotFoundException;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.IssueType;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestIssue;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IssueResourceTest
    extends AdminResourceTestSupport
{
    private IssueService issueService;

    private IssueNotificationsSender issueNotificationsSender;

    private SecurityService securityService;

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    @Override
    protected IssueResource getResourceInstance()
    {
        final IssueResource resource = new IssueResource();

        issueService = Mockito.mock( IssueService.class );
        contentService = Mockito.mock( ContentService.class );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn(
            Contents.from( this.createContent() ) );
        contentTypeService = Mockito.mock( ContentTypeService.class );
        issueNotificationsSender = Mockito.mock( IssueNotificationsSender.class );
        securityService = Mockito.mock( SecurityService.class );
        Mockito.when( securityService.getAllMemberships( Mockito.isA( PrincipalKey.class ) ) ).thenReturn(
            PrincipalKeys.from( "role:system:one" ) );
        Mockito.when( securityService.getPrincipals( Mockito.isA( PrincipalKeys.class ) ) ).thenReturn(
            Principals.from( Role.create().key( RoleKeys.ADMIN ).displayName( "Admin" ).build() ) );
        Mockito.when( securityService.getUser( User.ANONYMOUS.getKey() ) ).thenReturn( Optional.of( User.ANONYMOUS ) );

        resource.setIssueService( issueService );
        resource.setIssueNotificationsSender( issueNotificationsSender );
        resource.setSecurityService( securityService );
        resource.setContentService( contentService );
        resource.setContentTypeService( contentTypeService );

        return resource;
    }

    private Content createContent()
    {
        return Content.create().
            id( ContentId.from( "content-id" ) ).
            type( ContentTypeName.folder() ).
            displayName( "Content display name" ).
            name( "content-name" ).
            path( "/path/to/content" ).
            build();
    }

    private Answer<IssueNotificationParams> returnNotificationParams()
    {
        return invocation -> IssueNotificationParams.create().build();
    }

    @Test
    public void test_create()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( null, "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createPublishRequest(), null );

        final Issue issue = this.createIssue();
        final IssueComment comment = IssueComment.create().text( issue.getDescription() ).creator( issue.getCreator() ).build();
        final Role role = Role.create().key( RoleKeys.CONTENT_MANAGER_APP ).displayName( "dname" ).build();
        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final ArgumentCaptor<CreateIssueCommentParams> commentCaptor = ArgumentCaptor.forClass( CreateIssueCommentParams.class );

        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        Mockito.when( issueService.create( Mockito.any( CreateIssueParams.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.createComment( Mockito.any( CreateIssueCommentParams.class ) ) ).thenReturn( comment );

        issueResource.create( params, request );

        Mockito.verify( issueService ).createComment( commentCaptor.capture() );
        Mockito.verify( issueService, Mockito.times( 1 ) ).create( Mockito.any( CreateIssueParams.class ) );
        Mockito.verify( issueService, Mockito.times( 1 ) ).createComment( Mockito.any( CreateIssueCommentParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueCreated( Mockito.isA( IssueNotificationParams.class ) );

        assertEquals( "desc", commentCaptor.getValue().getText() );
        assertEquals( issue.getId(), commentCaptor.getValue().getIssue() );
        assertEquals( issue.getCreator(), commentCaptor.getValue().getCreator() );
    }

    @Test
    public void test_create_with_publish_schedule()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( IssueType.PUBLISH_REQUEST.toString(), "title", "desc", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createPublishRequest(),
                                 createPublishRequestSchedule() );

        final Issue issue = this.createPublishRequestIssue();
        final IssueComment comment = IssueComment.create().text( issue.getDescription() ).creator( issue.getCreator() ).build();
        final Role role = Role.create().key( RoleKeys.CONTENT_MANAGER_APP ).displayName( "dname" ).build();
        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final ArgumentCaptor<CreateIssueCommentParams> commentCaptor = ArgumentCaptor.forClass( CreateIssueCommentParams.class );
        final ArgumentCaptor<CreateIssueParams> paramCaptor = ArgumentCaptor.forClass( CreateIssueParams.class );

        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        Mockito.when( issueService.create( Mockito.any( CreateIssueParams.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.createComment( Mockito.any( CreateIssueCommentParams.class ) ) ).thenReturn( comment );

        issueResource.create( params, request );

        Mockito.verify( issueService ).createComment( commentCaptor.capture() );
        Mockito.verify( issueService, Mockito.times( 1 ) ).create( paramCaptor.capture() );
        Mockito.verify( issueService, Mockito.times( 1 ) ).createComment( Mockito.any( CreateIssueCommentParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueCreated( Mockito.isA( IssueNotificationParams.class ) );

        assertEquals( "desc", commentCaptor.getValue().getText() );
        assertEquals( issue.getId(), commentCaptor.getValue().getIssue() );
        assertEquals( issue.getCreator(), commentCaptor.getValue().getCreator() );
        assertTrue( paramCaptor.getValue() instanceof CreatePublishRequestIssueParams );
        assertEquals( params.schedule.getFrom(), ( (CreatePublishRequestIssueParams) paramCaptor.getValue() ).getSchedule().getFrom() );
        assertEquals( params.schedule.getTo(), ( (CreatePublishRequestIssueParams) paramCaptor.getValue() ).getSchedule().getTo() );
    }

    @Test
    public void test_createNoDescription()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( null, "title", null, Arrays.asList( User.ANONYMOUS.getKey().toString() ), createPublishRequest(), null );

        final Issue issue = this.createIssue();

        final Role role = Role.create().key( RoleKeys.CONTENT_MANAGER_APP ).displayName( "dname" ).build();
        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();

        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        Mockito.when( issueService.create( Mockito.any( CreateIssueParams.class ) ) ).thenReturn( issue );

        issueResource.create( params, request );

        Mockito.verify( issueService, Mockito.times( 1 ) ).create( Mockito.any( CreateIssueParams.class ) );
        Mockito.verify( issueService, Mockito.never() ).createComment( Mockito.any( CreateIssueCommentParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueCreated( Mockito.isA( IssueNotificationParams.class ) );
    }

    @Test
    public void verifyValidAssigneeNotFiltered()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( null, "title", "", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createPublishRequest(), null );

        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final Role role = Role.create().key( RoleKeys.CONTENT_MANAGER_EXPERT ).displayName( "dname" ).build();
        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        Mockito.when( issueService.create( Mockito.isA( CreateIssueParams.class ) ) ).thenReturn( this.createIssue() );

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
            new CreateIssueJson( null, "title", "", Arrays.asList( User.ANONYMOUS.getKey().toString() ), createPublishRequest(), null );

        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final Role role = Role.create().key( RoleKeys.EVERYONE ).displayName( "dname" ).build();
        Mockito.when( securityService.getPrincipals( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( Principals.from( role ) );
        ArgumentCaptor<CreateIssueParams> issueParamsArgumentCaptor = ArgumentCaptor.forClass( CreateIssueParams.class );
        Mockito.when( issueService.create( Mockito.isA( CreateIssueParams.class ) ) ).thenReturn( this.createIssue() );

        issueResource.create( params, request );

        Mockito.verify( issueService ).create( issueParamsArgumentCaptor.capture() );

        assertTrue( issueParamsArgumentCaptor.getValue().getApproverIds().isEmpty() );
    }

    @Test
    public void verifyOnlyInvalidAssigneeFiltered()
        throws Exception
    {
        final CreateIssueJson params =
            new CreateIssueJson( null, "title", "", Arrays.asList( User.ANONYMOUS.getKey().toString(), User.ANONYMOUS.getKey().toString() ),
                                 createPublishRequest(), null );

        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final IssueResource issueResource = getResourceInstance();
        final Role role1 = Role.create().key( RoleKeys.EVERYONE ).displayName( "dname" ).build();
        final Role role2 = Role.create().key( RoleKeys.CONTENT_MANAGER_ADMIN ).displayName( "dname" ).build();
        Mockito.when( securityService.getPrincipals( Mockito.any() ) ).thenReturn( Principals.from( role1 ), Principals.from( role2 ) );
        ArgumentCaptor<CreateIssueParams> issueParamsArgumentCaptor = ArgumentCaptor.forClass( CreateIssueParams.class );
        Mockito.when( issueService.create( Mockito.isA( CreateIssueParams.class ) ) ).thenReturn( this.createIssue() );

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
    public void test_getStatsByType()
        throws Exception
    {
        createLocalSession();
        final FindIssuesResult findIssuesResult = FindIssuesResult.create().hits( 2 ).totalHits( 4 ).build();
        final IssueResource issueResource = getResourceInstance();
        Mockito.when( issueService.findIssues( Mockito.any( IssueQuery.class ) ) ).thenReturn( findIssuesResult );
        final IssueStatsJson result = issueResource.getStatsByType( new CountStatsJson( "STANDARD" ) );

        assertNotNull( result );
        Mockito.verify( issueService, Mockito.times( 6 ) ).findIssues( Mockito.any( IssueQuery.class ) );
    }

    @Test
    public void test_list_issues()
        throws Exception
    {
        createLocalSession();

        final Issue issue = createIssue();
        final List<Issue> issues = List.of( issue );
        final IssueResource issueResource = getResourceInstance();
        final FindIssuesResult result = FindIssuesResult.create().hits( 2 ).totalHits( 4 ).issues( issues ).build();
        Mockito.when( issueService.findIssues( Mockito.any( IssueQuery.class ) ) ).thenReturn( result );
        Mockito.when( securityService.getUser( Mockito.any( PrincipalKey.class ) ) ).thenReturn( Optional.empty() );

        issueResource.listIssues( new ListIssuesJson( "OPEN", true, true, true, 0, 10 ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).findIssues( Mockito.any( IssueQuery.class ) );
    }

    @Test
    public void test_find_issues()
        throws Exception
    {
        createLocalSession();

        final FindIssuesResult result = FindIssuesResult.create().hits( 2 ).totalHits( 4 ).issues( List.of( createIssue() ) ).build();

        Mockito.when( issueService.findIssues( Mockito.any( IssueQuery.class ) ) ).thenReturn( result );
        Mockito.when( securityService.getUser( Mockito.any( PrincipalKey.class ) ) ).thenReturn( Optional.empty() );

        request().path( "issue/findIssues" ).entity( "{}", MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        Mockito.verify( issueService, Mockito.times( 1 ) ).findIssues( Mockito.any( IssueQuery.class ) );
    }

    @Test
    public void test_getIssue()
        throws Exception
    {
        final Instant createdTime = Instant.now();
        final Issue issue = createIssue();

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );
        List<IssueComment> comments = List.of( this.createIssueComment( createdTime ) );
        FindIssueCommentsResult result = FindIssueCommentsResult.create().hits( 1 ).totalHits( 3 ).comments( comments ).build();
        Mockito.when( this.issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn( result );

        final Map params = new HashMap<>();
        params.put( "id", issue.getId().toString() );
        params.put( "createdTime", createdTime );

        final String expected = new StrSubstitutor( params ).replace( readFromFile( "get_issue_result.json" ) );

        String jsonString = request().path( "issue/id" ).
            queryParam( "id", issue.getId().toString() ).
            get().getAsString();

        assertStringJson( expected, jsonString );
    }

    @Test
    public void test_get_publish_schedule_issue()
        throws Exception
    {
        final Instant createdTime = Instant.now();
        final Issue issue = createPublishRequestIssue();

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );
        List<IssueComment> comments = List.of( this.createIssueComment( createdTime ) );
        FindIssueCommentsResult result = FindIssueCommentsResult.create().hits( 1 ).totalHits( 3 ).comments( comments ).build();
        Mockito.when( this.issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn( result );

        final Map params = new HashMap<>();
        params.put( "id", issue.getId().toString() );
        params.put( "createdTime", createdTime );

        final String expected = new StrSubstitutor( params ).replace( readFromFile( "get_issue_scheduled_result.json" ) );

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
        final Issue issue = createIssue();

        Mockito.when( this.issueService.getIssue( issue.getId() ) ).thenReturn( issue );
        Mockito.when( this.issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn(
            FindIssueCommentsResult.create().build() );

        final Map params = new HashMap<>();
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
        final Issue issue = createIssue();
        final User admin = User.create().login( "admin" ).displayName( "Admin" ).email( "admin@email.com" ).build();
        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Open", false, false, Arrays.asList( "user:system:admin" ),
                                 createPublishRequest(), null );

        IssueResource resource = getResourceInstance();
        Mockito.when( issueService.getIssue( Mockito.isA( IssueId.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.update( Mockito.any( UpdateIssueParams.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn(
            FindIssueCommentsResult.create().build() );
        Mockito.when( securityService.getUser( PrincipalKey.from( "user:system:admin" ) ) ).thenReturn( Optional.of( admin ) );

        resource.update( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( Mockito.any( UpdateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueUpdated(
            Mockito.isA( IssueUpdatedNotificationParams.class ) );
    }

    @Test
    public void test_update_with_publish_schedule()
    {
        final Issue issue = createPublishRequestIssue();
        final User admin = User.create().login( "admin" ).displayName( "Admin" ).email( "admin@email.com" ).build();
        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Open", false, false, Arrays.asList( "user:system:admin" ),
                                 createPublishRequest(), createPublishRequestSchedule() );

        IssueResource resource = getResourceInstance();
        Mockito.when( issueService.getIssue( Mockito.isA( IssueId.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.update( Mockito.any( UpdateIssueParams.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn(
            FindIssueCommentsResult.create().build() );
        Mockito.when( securityService.getUser( PrincipalKey.from( "user:system:admin" ) ) ).thenReturn( Optional.of( admin ) );

        resource.update( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( Mockito.any( UpdateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueUpdated(
            Mockito.isA( IssueUpdatedNotificationParams.class ) );
    }

    @Test
    public void test_update_is_publish()
    {
        final Issue issue = createIssue();

        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Closed", true, false, null, createPublishRequest(), null );

        IssueResource resource = getResourceInstance();
        Mockito.when( issueService.getIssue( Mockito.isA( IssueId.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.update( Mockito.any( UpdateIssueParams.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn(
            FindIssueCommentsResult.create().build() );

        resource.update( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( Mockito.any( UpdateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssuePublished(
            Mockito.isA( IssuePublishedNotificationParams.class ) );
    }

    @Test
    public void test_update_is_autoSave()
    {
        final Issue issue = createIssue();
        final User admin = User.create().login( "admin" ).displayName( "Admin" ).email( "admin@email.com" ).build();
        final UpdateIssueJson params =
            new UpdateIssueJson( issue.getId().toString(), "title", "desc", "Closed", true, true, Arrays.asList( "user:system:admin" ),
                                 createPublishRequest(), null );

        IssueResource resource = getResourceInstance();
        Mockito.when( issueService.getIssue( Mockito.isA( IssueId.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.update( Mockito.any( UpdateIssueParams.class ) ) ).thenReturn( issue );
        Mockito.when( issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn(
            FindIssueCommentsResult.create().build() );
        Mockito.when( securityService.getUser( PrincipalKey.from( "user:system:admin" ) ) ).thenReturn( Optional.of( admin ) );

        resource.update( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).update( Mockito.any( UpdateIssueParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 0 ) ).notifyIssueUpdated(
            Mockito.isA( IssueUpdatedNotificationParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 0 ) ).notifyIssuePublished(
            Mockito.isA( IssuePublishedNotificationParams.class ) );
    }

    @Test
    public void test_comment()
    {
        final Issue issue = createIssue();
        final IssueComment comment = createIssueComment( Instant.now() );
        final User creator = User.ANONYMOUS;

        final CreateIssueCommentJson params =
            new CreateIssueCommentJson( issue.getId().toString(), comment.getText(), comment.getCreator().toString(), false );

        IssueResource resource = getResourceInstance();
        Mockito.when( securityService.getUser( params.creator ) ).thenReturn( Optional.ofNullable( creator ) );
        Mockito.when( issueService.getIssue( params.issueId ) ).thenReturn( issue );
        Mockito.when( issueService.createComment( Mockito.any( CreateIssueCommentParams.class ) ) ).thenReturn( comment );
        Mockito.when( issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn(
            FindIssueCommentsResult.create().build() );

        resource.comment( params, Mockito.mock( HttpServletRequest.class ) );

        Mockito.verify( issueService, Mockito.times( 1 ) ).createComment( Mockito.any( CreateIssueCommentParams.class ) );
        Mockito.verify( issueNotificationsSender, Mockito.times( 1 ) ).notifyIssueCommented(
            Mockito.isA( IssueCommentedNotificationParams.class ) );
    }

    @Test
    public void test_commentNoUser()
    {
        final Issue issue = createIssue();
        final IssueComment comment = createIssueComment( Instant.now() );

        final CreateIssueCommentJson params =
            new CreateIssueCommentJson( issue.getId().toString(), comment.getText(), comment.getCreator().toString(), false );

        IssueResource resource = getResourceInstance();
        Mockito.when( issueService.getIssue( params.issueId ) ).thenReturn( issue );
        Mockito.when( securityService.getUser( params.creator ) ).thenReturn( Optional.empty() );

        assertThrows(PrincipalNotFoundException.class, () -> resource.comment( params, Mockito.mock( HttpServletRequest.class ) ) );
    }

    @Test
    public void test_commentNoIssue()
    {
        final Issue issue = createIssue();
        final IssueComment comment = createIssueComment( Instant.now() );

        final CreateIssueCommentJson params =
            new CreateIssueCommentJson( issue.getId().toString(), comment.getText(), comment.getCreator().toString(), false );

        IssueResource resource = getResourceInstance();
        Mockito.when( issueService.getIssue( params.issueId ) ).thenThrow( new IssueNotFoundException( issue.getId() ) );
        Mockito.when( securityService.getUser( params.creator ) ).thenReturn( Optional.of( User.ANONYMOUS ) );

        assertThrows(IssueNotFoundException.class, () -> resource.comment( params, Mockito.mock( HttpServletRequest.class ) ) );
    }

    @Test
    public void test_getComments()
        throws Exception
    {
        final Issue issue = createIssue();
        final IssueComment comment = createIssueComment( Instant.now() );

        FindIssueCommentsResult result = FindIssueCommentsResult.create().comments( List.of( comment ) ).hits( 1 ).totalHits( 10 ).build();

        Mockito.when( this.issueService.findComments( Mockito.any( IssueCommentQuery.class ) ) ).thenReturn( result );

        final Map params = new HashMap<>();
        params.put( "createdTime", comment.getCreated() );
        params.put( "id", comment.getId() );

        final String expected = new StrSubstitutor( params ).replace( readFromFile( "get_issue_comments_result.json" ) );

        String jsonString = request().path( "issue/comment/list" ).
            entity( "{\"issue\":\"" + issue.getName() + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertStringJson( expected, jsonString );
    }

    @Test
    public void test_updateComment()
    {
        final IssueComment comment = createIssueComment( Instant.now() );

        final UpdateIssueCommentJson params = new UpdateIssueCommentJson( comment.getId().toString(), comment.getText() );

        IssueResource resource = getResourceInstance();
        Mockito.when( issueService.updateComment( Mockito.any( UpdateIssueCommentParams.class ) ) ).thenReturn( comment );

        IssueCommentJson json = resource.updateComment( params );

        assertEquals( json.getText(), comment.getText() );
        Mockito.verify( issueService, Mockito.times( 1 ) ).updateComment( Mockito.any( UpdateIssueCommentParams.class ) );
    }

    @Test
    public void test_deleteComment()
        throws Exception
    {
        final IssueComment comment = createIssueComment( Instant.now() );
        IssueResource resource = getResourceInstance();

        DeleteIssueCommentResult result = new DeleteIssueCommentResult( NodeIds.from( comment.getId() ) );
        Mockito.when( this.issueService.deleteComment( Mockito.any( DeleteIssueCommentParams.class ) ) ).thenReturn( result );

        DeleteIssueCommentJson params = new DeleteIssueCommentJson( comment.getId().toString() );
        DeleteIssueCommentResultJson resultJson = resource.deleteComment( params );

        assertEquals( 1, resultJson.getIds().size() );
        assertEquals( comment.getId().toString(), resultJson.getIds().get( 0 ) );
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

    private PublishRequestScheduleJson createPublishRequestSchedule()
    {
        return new PublishRequestScheduleJson( Instant.ofEpochSecond( 1549016358L ).toString(),
                                               Instant.ofEpochSecond( 1570000758L ).toString() );
    }

    private IssueComment createIssueComment( Instant createdTime )
    {
        return IssueComment.create().
            id( NodeId.from( UUID.randomUUID() ) ).
            text( "Comment text one" ).
            creator( User.ANONYMOUS.getKey() ).
            creatorDisplayName( "Anonymous" ).
            created( createdTime ).
            build();

    }

    private Issue createIssue()
    {
        return Issue.create().
            addApproverId( PrincipalKey.from( "user:system:anonymous" ) ).
            title( "title" ).
            description( "desc" ).
            creator( User.ANONYMOUS.getKey() ).
            modifier( User.ANONYMOUS.getKey() ).
            setPublishRequest( PublishRequest.create().
                addExcludeId( ContentId.from( "exclude-id" ) ).
                addItem( PublishRequestItem.create().
                    id( ContentId.from( "content-id" ) ).
                    includeChildren( true ).
                    build() ).
                build() ).
            build();

    }

    private Issue createPublishRequestIssue()
    {
        return PublishRequestIssue.create().
            addApproverId( PrincipalKey.from( "user:system:anonymous" ) ).
            title( "title" ).
            description( "desc" ).
            creator( User.ANONYMOUS.getKey() ).
            modifier( User.ANONYMOUS.getKey() ).
            setPublishRequest( PublishRequest.create().
                addExcludeId( ContentId.from( "exclude-id" ) ).
                addItem( PublishRequestItem.create().
                    id( ContentId.from( "content-id" ) ).
                    includeChildren( true ).
                    build() ).
                build() ).
            schedule( createPublishRequestSchedule().toSchedule() ).
            build();

    }

    private void createLocalSession()
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
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
