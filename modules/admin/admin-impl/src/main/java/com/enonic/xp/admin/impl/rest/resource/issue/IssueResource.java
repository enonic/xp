package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.issue.IssueJson;
import com.enonic.xp.admin.impl.json.issue.IssueStatsJson;
import com.enonic.xp.admin.impl.json.issue.IssuesJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.GetIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "issue")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class IssueResource
    implements JaxRsComponent
{
    private static final String DEFAULT_FROM_PARAM = "0";

    private static final String DEFAULT_SIZE_PARAM = "10";

    private IssueService issueService;

    @POST
    @Path("create")
    public IssueJson create( final CreateIssueJson params )
    {
        final Issue issue = issueService.create( params.getCreateIssueParams() );
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
    @Path("update")
    public IssueJson update( final UpdateIssueJson params )
    {
        return new IssueJson( issueService.update( params.getUpdateIssueParams() ) );
    }

    @GET
    @Path("stats")
    public IssueStatsJson getStats()
    {
        final long assignedToMe = this.issueService.countIssues( createIssuesByTypeQuery( "ASSIGNED_TO_ME" ).build() );
        final long createdByMe = this.issueService.countIssues( createIssuesByTypeQuery( "CREATED_BY_ME" ).build() );
        final long open = this.issueService.countIssues( createIssuesByTypeQuery( "OPEN" ).build() );
        final long closed = this.issueService.countIssues( createIssuesByTypeQuery( "CLOSED" ).build() );

        return IssueStatsJson.create().assignedToMe( assignedToMe ).createdByMe( createdByMe ).open( open ).closed( closed ).build();
    }

    @GET
    @Path("list")
    public IssuesJson listIssues( @QueryParam("type") final String type,
                                  @QueryParam("from") @DefaultValue(DEFAULT_FROM_PARAM) final Integer fromParam,
                                  @QueryParam("size") @DefaultValue(DEFAULT_SIZE_PARAM) final Integer sizeParam )
    {
        final IssueQuery issueQuery = createIssuesByTypeQuery( type ).from( fromParam ).size( sizeParam ).build();
        final List<Issue> issues = this.issueService.findIssues( issueQuery );

        return new IssuesJson( issues );
    }

    private IssueQuery.Builder createIssuesByTypeQuery( final String type )
    {
        final IssueQuery.Builder builder = IssueQuery.create();

        if ( type == null )
        {
            return builder;
        }

        if ( type.equalsIgnoreCase( "CLOSED" ) )
        {
            return builder.status( IssueStatus.Closed );
        }

        if ( type.equalsIgnoreCase( "OPEN" ) )
        {
            return builder.status( IssueStatus.Open );
        }

        if ( type.equalsIgnoreCase( "CREATED_BY_ME" ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            return builder.creator( authInfo.getUser().getKey() );
        }

        if ( type.equalsIgnoreCase( "ASSIGNED_TO_ME" ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            return builder.approvers( PrincipalKeys.from( authInfo.getUser().getKey() ) );
        }

        return builder;
    }

    @Reference
    public void setIssueService( final IssueService issueService )
    {
        this.issueService = issueService;
    }
}
