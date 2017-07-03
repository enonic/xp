package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.admin.impl.json.issue.IssueJson;
import com.enonic.xp.admin.impl.json.issue.IssueListJson;
import com.enonic.xp.admin.impl.json.issue.IssueStatsJson;
import com.enonic.xp.admin.impl.json.issue.IssuesJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.GetIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.ListIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.issue.FindIssuesParams;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.PrincipalKeys;
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

    @POST
    @Path("create")
    public IssueJson create( final CreateIssueJson params, @Context HttpServletRequest request )
    {
        final Issue issue = issueService.create( params.getCreateIssueParams() );
        issueNotificationsSender.notifyIssueCreated( issue, request.getHeader( HttpHeaders.REFERER ) );

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
        final Issue issue = issueService.update( params.getUpdateIssueParams() );

        if ( params.isPublish() )
        {
            issueNotificationsSender.notifyIssuePublished( issue, request.getHeader( HttpHeaders.REFERER ) );
        }
        else
        {
            issueNotificationsSender.notifyIssueUpdated( issue, request.getHeader( HttpHeaders.REFERER ) );
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

    private IssueQuery createIssueQuery( final FindIssuesParams params )
    {
        final IssueQuery.Builder builder = IssueQuery.create();

        builder.status( params.getStatus() );
        builder.from( params.getFrom() );
        builder.size( params.getSize() );

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
        final long open =
            this.issueService.findIssues( createIssueQuery( FindIssuesParams.create().status( IssueStatus.OPEN ).build() ) ).getTotalHits();

        final long openAssignedToMe = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.OPEN ).assignedToMe( true ).build() ) ).getTotalHits();

        final long openCreatedByMe = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.OPEN ).createdByMe( true ).build() ) ).getTotalHits();

        final long closed = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.CLOSED ).build() ) ).getTotalHits();

        final long closedAssignedToMe = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.CLOSED ).assignedToMe( true ).build() ) ).getTotalHits();

        final long closedCreatedByMe = this.issueService.findIssues(
            createIssueQuery( FindIssuesParams.create().status( IssueStatus.CLOSED ).createdByMe( true ).build() ) ).getTotalHits();

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
}
