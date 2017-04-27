package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;

import javax.annotation.security.RolesAllowed;
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
        return new IssueStatsJson( 0, 4, 4, 0 ); // mock for now
    }

    @GET
    @Path("bytype")
    public IssuesJson getIssuesByType( @QueryParam("type") final String type )
    {
        final List<Issue> issues = this.issueService.findIssues( createIssuesByTypeQuery( type ) );

        return new IssuesJson( issues );
    }

    private IssueQuery createIssuesByTypeQuery( final String type )
    {
        final IssueQuery.Builder builder = IssueQuery.create();

        if ( type.equalsIgnoreCase( "CLOSED" ) )
        {
            return builder.status( IssueStatus.Closed ).build();
        }

        if ( type.equalsIgnoreCase( "OPEN" ) )
        {
            return builder.status( IssueStatus.Open ).build();
        }

        if ( type.equalsIgnoreCase( "CREATED_BY_ME" ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            return builder.creator( authInfo.getUser().getKey() ).build();
        }

        if ( type.equalsIgnoreCase( "ASSIGNED_TO_ME" ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            return builder.approvers( PrincipalKeys.from( authInfo.getUser().getKey() ) ).build();
        }

        return builder.build();
    }

    @Reference
    public void setIssueService( final IssueService issueService )
    {
        this.issueService = issueService;
    }
}
