package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.admin.impl.json.issue.DeleteIssueCommentResultJson;
import com.enonic.xp.admin.impl.json.issue.IssueCommentJson;
import com.enonic.xp.admin.impl.json.issue.IssueCommentListJson;
import com.enonic.xp.admin.impl.json.issue.IssueJson;
import com.enonic.xp.admin.impl.json.issue.IssueListJson;
import com.enonic.xp.admin.impl.json.issue.IssueStatsJson;
import com.enonic.xp.admin.impl.json.issue.IssuesJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueCommentJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.DeleteIssueCommentJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.FindIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.GetIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.ListIssueCommentsJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.ListIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueCommentJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.issue.FindIssueCommentsResult;
import com.enonic.xp.issue.FindIssuesParams;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueCommentQuery;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "issue")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class IssueResource
    implements JaxRsComponent
{
    private IssueService issueService;

    private IssueNotificationsSender issueNotificationsSender;

    private SecurityService securityService;

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    @POST
    @Path("create")
    public IssueJson create( final CreateIssueJson json, @Context HttpServletRequest request )
    {
        final Issue issue = issueService.create( generateCreateIssueParams( json ) );
        final List<IssueComment> comments = Lists.newArrayList();

        if ( !Strings.isNullOrEmpty( json.description ) )
        {
            Optional<User> creator = securityService.getUser( issue.getCreator() );
            if ( creator.isPresent() )
            {
                CreateIssueCommentParams params = CreateIssueCommentParams.create().
                    issue( issue.getId() ).
                    creator( issue.getCreator() ).
                    creatorDisplayName( creator.get().getDisplayName() ).
                    text( json.description ).
                    build();

                comments.add( issueService.createComment( params ) );
            }
        }

        final IssueNotificationParams createdParams = IssueNotificationParamsFactory.create().
            securityService( securityService ).
            contentService( contentService ).
            contentTypeService( contentTypeService ).
            issue( issue ).
            comments( comments ).
            url( request.getHeader( HttpHeaders.REFERER ) ).
            build().
            createdParams();

        issueNotificationsSender.notifyIssueCreated( createdParams );

        return new IssueJson( issue );
    }

    @POST
    @Path("getIssues")
    public IssuesJson getByIds( final GetIssuesJson params )
    {
        final IssuesJson result = new IssuesJson();
        for ( IssueId id : params.getIssueIds() )
        {
            result.addIssue( issueService.getIssue( id ) );
        }

        return result;
    }

    @POST
    @Path("findIssues")
    public IssuesJson find( final FindIssuesJson params )
    {
        final IssueQuery query = createIssueQuery( params.getFindIssuesParams() );
        final FindIssuesResult findResult = issueService.findIssues( query );

        final IssuesJson result = new IssuesJson();
        result.addIssues( findResult.getIssues() );

        return result;
    }

    @GET
    @Path("id")
    public IssueJson getById( @QueryParam("id") final String id )
    {
        final Issue issue = issueService.getIssue( IssueId.from( id ) );
        return new IssueJson( issue );
    }

    @POST
    @Path("update")
    public IssueJson update( final UpdateIssueJson params, @Context HttpServletRequest request )
    {
        final Issue issueToEdit = issueService.getIssue( params.issueId );
        final PrincipalKeys validAssignees =
            params.approverIds != null ? filterInvalidAssignees( params.approverIds ) : PrincipalKeys.empty();

        final PrincipalKeys addedAssignees = filterKeys( issueToEdit.getApproverIds(), validAssignees, false );
        final PrincipalKeys existingAssignees = filterKeys( issueToEdit.getApproverIds(), validAssignees, true );

        final Issue issue = issueService.update( generateUpdateIssueParams( params ) );

        IssueCommentQuery query = IssueCommentQuery.create().issue( issue.getId() ).build();
        final List<IssueComment> comments = issueService.findComments( query ).getIssueComments();
        final String referer = request.getHeader( HttpHeaders.REFERER );

        if ( addedAssignees.getSize() > 0 )
        {
            final IssueNotificationParams createdParams = IssueNotificationParamsFactory.create().
                securityService( securityService ).
                contentService( contentService ).
                contentTypeService( contentTypeService ).
                issue( issue ).
                comments( comments ).
                url( request.getHeader( HttpHeaders.REFERER ) ).
                recipients( addedAssignees ).
                build().
                createdParams();

            issueNotificationsSender.notifyIssueCreated( createdParams );
        }

        if ( !params.autoSave )
        {
            final IssueNotificationParamsFactory.Builder paramsBuilder = IssueNotificationParamsFactory.create().
                securityService( securityService ).
                contentService( contentService ).
                contentTypeService( contentTypeService ).
                issue( issue ).
                comments( comments ).
                url( request.getHeader( HttpHeaders.REFERER ) );

            if ( params.isPublish )
            {
                issueNotificationsSender.notifyIssuePublished( paramsBuilder.build().publishedParams() );
            }
            else
            {
                issueNotificationsSender.notifyIssueUpdated( paramsBuilder.recipients( existingAssignees ).build().updatedParams() );
            }
        }

        return new IssueJson( issue );
    }

    @GET
    @Path("stats")
    public IssueStatsJson getStats()
    {
        return countIssues();
    }

    @POST
    @Path("comment")
    public IssueCommentJson comment( final CreateIssueCommentJson json, @Context HttpServletRequest request )
    {
        final Issue issue = issueService.getIssue( json.issueId );
        final Optional<User> creator = securityService.getUser( json.creator );

        if ( !creator.isPresent() )
        {
            throw new PrincipalNotFoundException( json.creator );
        }

        CreateIssueCommentParams params = CreateIssueCommentParams.create().
            issue( issue.getId() ).
            text( json.text ).
            creator( creator.get().getKey() ).
            creatorDisplayName( creator.get().getDisplayName() ).
            build();

        final IssueComment comment = issueService.createComment( params );

        final IssueCommentQuery commentsQuery = IssueCommentQuery.create().issue( issue.getId() ).build();
        final FindIssueCommentsResult results = issueService.findComments( commentsQuery );

        IssueCommentedNotificationParams notificationParams = IssueNotificationParamsFactory.create().
            securityService( securityService ).
            contentService( contentService ).
            contentTypeService( contentTypeService ).
            issue( issue ).
            comments( results.getIssueComments() ).
            url( request.getHeader( HttpHeaders.REFERER ) ).
            build().
            commentedParams();

        issueNotificationsSender.notifyIssueCommented( notificationParams );

        return new IssueCommentJson( comment );
    }

    @POST
    @Path("comment/list")
    public IssueCommentListJson listComments( final ListIssueCommentsJson params )
    {
        final IssueCommentQuery issueQuery = createIssueCommentQuery( params );
        final FindIssueCommentsResult result = this.issueService.findComments( issueQuery );
        final IssueListMetaData metaData = IssueListMetaData.create().hits( result.getHits() ).totalHits( result.getTotalHits() ).build();

        return new IssueCommentListJson( result.getIssueComments(), metaData );
    }

    @POST
    @Path("comment/delete")
    public DeleteIssueCommentResultJson deleteComment( final DeleteIssueCommentJson json )
    {
        DeleteIssueCommentParams params = DeleteIssueCommentParams.create().
            comment( json.getComment() ).
            build();

        final DeleteIssueCommentResult result = this.issueService.deleteComment( params );

        return new DeleteIssueCommentResultJson( result );
    }

    @POST
    @Path("comment/update")
    public IssueCommentJson updateComment( final UpdateIssueCommentJson json )
    {
        UpdateIssueCommentParams params = UpdateIssueCommentParams.create().
            comment( json.getComment() ).
            text( json.getText() ).
            build();

        final IssueComment result = this.issueService.updateComment( params );

        return new IssueCommentJson( result );
    }

    @POST
    @Path("list")
    public IssueListJson listIssues( final ListIssuesJson params )
    {
        final IssueQuery issueQuery = createIssueQuery( params.getFindIssuesParams() );
        final FindIssuesResult result = this.issueService.findIssues( issueQuery );
        final IssueListMetaData metaData = IssueListMetaData.create().hits( result.getHits() ).totalHits( result.getTotalHits() ).build();

        if ( params.isResolveAssignees() )
        {
            return new IssueListJson( fetchAssigneesForIssues( result.getIssues() ), metaData );
        }
        else
        {
            return new IssueListJson( result.getIssues(), metaData );
        }
    }

    private IssueCommentQuery createIssueCommentQuery( final ListIssueCommentsJson params )
    {
        return IssueCommentQuery.create().
            issue( params.getIssue() ).
            creator( params.getCreator() ).
            from( params.getFrom() ).
            size( params.getSize() ).
            count( params.isCount() ).
            build();
    }

    private IssueQuery createIssueQuery( final FindIssuesParams params )
    {
        final IssueQuery.Builder builder = IssueQuery.create();

        builder.status( params.getStatus() );
        builder.from( params.getFrom() );
        builder.size( params.getSize() );
        builder.items( params.getItems() );

        if ( params.isCreatedByMe() )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            builder.creator( authInfo.getUser().getKey() );
        }

        if ( params.isAssignedToMe() )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            builder.approvers( PrincipalKeys.from( authInfo.getUser().getKey() ) );
        }

        return builder.build();
    }

    private IssueStatsJson countIssues()
    {
        final long open = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.OPEN ).size( 0 ).build() ) ).getTotalHits();

        final long openAssignedToMe = this.issueService.findIssues( createIssueQuery(
            FindIssuesParams.create().status( IssueStatus.OPEN ).assignedToMe( true ).size( 0 ).build() ) ).getTotalHits();

        final long openCreatedByMe = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.OPEN ).createdByMe( true ).size( 0 ).build() ) ).getTotalHits();

        final long closed = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.CLOSED ).size( 0 ).build() ) ).getTotalHits();

        final long closedAssignedToMe = this.issueService.findIssues( createIssueQuery(
            FindIssuesParams.create().status( IssueStatus.CLOSED ).size( 0 ).assignedToMe( true ).build() ) ).getTotalHits();

        final long closedCreatedByMe = this.issueService.findIssues( createIssueQuery(
            FindIssuesParams.create().status( IssueStatus.CLOSED ).size( 0 ).createdByMe( true ).build() ) ).getTotalHits();

        return IssueStatsJson.create().open( open ).openAssignedToMe( openAssignedToMe ).openCreatedByMe( openCreatedByMe ).closed(
            closed ).closedAssignedToMe( closedAssignedToMe ).closedCreatedByMe( closedCreatedByMe ).build();
    }


    private Map<Issue, List<User>> fetchAssigneesForIssues( final List<Issue> issues )
    {
        final Map<Issue, List<User>> issuesWithAssignees = Maps.newHashMap();

        issues.stream().forEach( issue -> issuesWithAssignees.put( issue, doFetchAssignees( issue ) ) );

        return issuesWithAssignees;
    }

    private List<User> doFetchAssignees( final Issue issue )
    {
        return issue.getApproverIds().stream().map( key -> securityService.getUser( key ).orElse( null ) ).filter(
            Objects::nonNull ).collect( Collectors.toList() );
    }

    private CreateIssueParams generateCreateIssueParams( final CreateIssueJson json )
    {
        final CreateIssueParams.Builder builder = CreateIssueParams.create();

        builder.title( json.title );
        builder.description( json.description );
        builder.setPublishRequest( json.publishRequest );
        builder.setApproverIds( filterInvalidAssignees( json.assignees ) );

        return builder.build();
    }

    private UpdateIssueParams generateUpdateIssueParams( final UpdateIssueJson json )
    {
        return UpdateIssueParams.create().
            id( json.issueId ).
            editor( editMe -> {
                if ( json.title != null )
                {
                    editMe.title = json.title;
                }
                if ( json.description != null )
                {
                    editMe.description = json.description;
                }
                if ( json.issueStatus != null )
                {
                    editMe.issueStatus = json.issueStatus;
                }
                if ( json.approverIds != null )
                {
                    editMe.approverIds = filterInvalidAssignees( json.approverIds );
                }
                if ( json.publishRequest != null )
                {
                    editMe.publishRequest = json.publishRequest;
                }
            } ).
            build();
    }

    private PrincipalKeys filterInvalidAssignees( final List<PrincipalKey> assignees )
    {
        return PrincipalKeys.from( assignees.stream().filter( this::isValidAssignee ).collect( Collectors.toList() ) );
    }

    private boolean isValidAssignee( final PrincipalKey principalKey )
    {
        final PrincipalKeys membershipKeys = securityService.getAllMemberships( principalKey );
        if ( membershipKeys.getSize() > 0 )
        {
            final Principals memberships = securityService.getPrincipals( membershipKeys );
            return memberships.stream().anyMatch( this::hasIssuePermissions );
        }
        else
        {
            return false;
        }
    }

    private PrincipalKeys filterKeys( final PrincipalKeys oldKeys, final PrincipalKeys newKeys, final boolean existing )
    {
        if ( newKeys.getSize() == 0 )
        {
            return existing ? PrincipalKeys.from( oldKeys ) : PrincipalKeys.empty();
        }
        else
        {
            return PrincipalKeys.from(
                newKeys.stream().filter( key -> existing == oldKeys.contains( key ) ).collect( Collectors.toList() ) );
        }
    }

    private boolean hasIssuePermissions( final Principal principal )
    {
        if ( principal.getKey().equals( RoleKeys.ADMIN ) )
        {
            return true;
        }

        if ( principal.getKey().equals( RoleKeys.CONTENT_MANAGER_ADMIN ) )
        {
            return true;
        }

        if ( principal.getKey().equals( RoleKeys.CONTENT_MANAGER_EXPERT ) )
        {
            return true;
        }

        if ( principal.getKey().equals( RoleKeys.CONTENT_MANAGER_APP ) )
        {
            return true;
        }

        return false;
    }

    @Reference
    public void setIssueService( final IssueService issueService )
    {
        this.issueService = issueService;
    }

    @Reference
    public void setIssueNotificationsSender( final IssueNotificationsSender issueNotificationsSender )
    {
        this.issueNotificationsSender = issueNotificationsSender;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
