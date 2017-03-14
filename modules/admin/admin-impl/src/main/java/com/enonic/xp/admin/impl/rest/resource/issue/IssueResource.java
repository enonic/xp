package com.enonic.xp.admin.impl.rest.resource.issue;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.issue.IssueJson;
import com.enonic.xp.admin.impl.json.issue.IssuesJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.issue.json.CreateIssueJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.GetIssuesJson;
import com.enonic.xp.admin.impl.rest.resource.issue.json.UpdateIssueJson;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.jaxrs.JaxRsComponent;
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

    @Reference
    public void setIssueService( final IssueService issueService )
    {
        this.issueService = issueService;
    }
}
