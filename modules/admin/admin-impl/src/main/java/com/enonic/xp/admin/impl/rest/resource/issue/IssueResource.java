package com.enonic.xp.admin.impl.rest.resource.issue;

import java.time.Instant;

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
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;

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
        final IssuesJson result = new IssuesJson();
        if ( type.equals( "CLOSED" ) || type.endsWith( "ASSIGNED_TO_ME" ) )
        {
            return result;
        }

        result.addIssue( createMockIssue( "New messaging on the front page", PrincipalKey.ofAnonymous(), Instant.now() ) );
        result.addIssue( createMockIssue( "Removing the old product", PrincipalKey.ofAnonymous(), Instant.now() ) );
        result.addIssue( createMockIssue( "More pictures", PrincipalKey.ofAnonymous(), Instant.now() ) );
        result.addIssue( createMockIssue( "Adding header and footer to the site", PrincipalKey.ofAnonymous(), Instant.now() ) );

        return result;
    }

    private Issue createMockIssue( final String title, final PrincipalKey creator, final Instant modifiedTime )
    {
        return Issue.create().id( IssueId.create() ).title( title ).creator( creator ).modifiedTime( modifiedTime ).build();
    }

    @Reference
    public void setIssueService( final IssueService issueService )
    {
        this.issueService = issueService;
    }
}
