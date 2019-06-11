package com.enonic.xp.admin.impl.rest.resource.commit;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.commit.CommitService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "commit")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class CommitResource
    implements JaxRsComponent
{
    private NodeService nodeService;

    private CommitService commitService;

    @GET
    @Path("getCommit")
    public GetCommitResultJson getCommit( final GetCommitJson params )
    {
        NodeCommitId nodeCommitId = params.getNodeCommitId();

        if ( nodeCommitId == null )
        {
            final GetActiveNodeVersionsResult activeNodeVersionsResult = nodeService.getActiveVersions(
                GetActiveNodeVersionsParams.create().nodeId( params.getNodeId() ).branches(
                    Branches.from( ContextAccessor.current().getBranch() ) ).build() );

            final Optional<NodeVersionMetadata> nodeVersionMetadata =
                activeNodeVersionsResult.getNodeVersions().values().stream().findFirst();

            if ( nodeVersionMetadata.isPresent() )
            {
                nodeCommitId = nodeVersionMetadata.get().getNodeCommitId();
            }
        }

        final NodeCommitEntry nodeCommitEntry = commitService.get( nodeCommitId, InternalContext.from( ContextAccessor.current() ) );

        if ( nodeCommitEntry == null )
        {
            throw new CommitNotFoundException( nodeCommitId );
        }
        return new GetCommitResultJson( nodeCommitEntry );
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setCommitService( final CommitService commitService )
    {
        this.commitService = commitService;
    }
}
