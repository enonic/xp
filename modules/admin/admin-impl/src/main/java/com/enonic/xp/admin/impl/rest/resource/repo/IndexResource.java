package com.enonic.xp.admin.impl.rest.resource.repo;

import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;

@Api("repo")
@Path(ResourceConstants.REST_ROOT + "repo")
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public final class IndexResource
    implements JaxRsComponent
{
    private IndexService indexService;

    @POST
    @Path("reindex")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("Reindexes a specified repository")
    public ReindexResultJson reindex( final ReindexRequestJson request )
    {
        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            setBranches( parseBranches( request.branches ) ).
            initialize( request.initialize ).
            repositoryId( parseRepositoryId( request.repository ) ).
            build() );

        return ReindexResultJson.create( result );
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    private static Branches parseBranches( final String branches )
    {
        final Iterable<String> split = Splitter.on( "," ).split( branches );
        final Iterable<Branch> parsed = Lists.newArrayList( split ).stream().map( Branch::from ).collect( Collectors.toList() );
        return Branches.from( parsed );
    }

    private static RepositoryId parseRepositoryId( final String repository )
    {
        return RepositoryId.from( repository );
    }
}
