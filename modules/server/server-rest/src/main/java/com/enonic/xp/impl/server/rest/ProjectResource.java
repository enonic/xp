package com.enonic.xp.impl.server.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.impl.server.rest.model.BranchJson;
import com.enonic.xp.impl.server.rest.model.ProjectJson;
import com.enonic.xp.impl.server.rest.model.SiteJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.RoleKeys;

@Path("/content/projects")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ProjectResource
    implements JaxRsComponent
{
    private final ProjectService projectService;

    private final ContentService contentService;


    @Activate
    public ProjectResource( @Reference final ProjectService projectService, @Reference final ContentService contentService )
    {
        this.projectService = projectService;
        this.contentService = contentService;
    }

    @GET
    @Path("list")
    public List<ProjectJson> list()
    {
        return projectService.list()
            .stream()
            .map( project -> ProjectJson.create().project( project ).addBranches( getBranchesFromProject( project ) ).build() )
            .collect( Collectors.toList() );
    }

    private List<BranchJson> getBranchesFromProject( final Project project )
    {
        final ImmutableList.Builder<BranchJson> branchJsons = ImmutableList.builder();

        for ( Branch branch : Arrays.asList( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
        {
            branchJsons.add( BranchJson.create().name( branch.getValue() ).addSites( fetchSites( project.getName(), branch ) ).build() );
        }

        return branchJsons.build();
    }

    private List<SiteJson> fetchSites( final ProjectName projectName, final Branch branch )
    {
        final Context context =
            ContextBuilder.from( ContextAccessor.current() ).repositoryId( projectName.getRepoId() ).branch( branch ).build();

        final ContentQuery query = ContentQuery.create()
            .queryFilter( ValueFilter.create().fieldName( "type" ).addValue( ValueFactory.newString( "portal:site" ) ).build() )
            .build();

        final FindContentIdsByQueryResult result = context.callWith( () -> contentService.find( query ) );

        return context.callWith( () -> contentService.getByIds( new GetContentByIdsParams( result.getContentIds() ) )
            .stream()
            .map( this::createSiteJson )
            .collect( Collectors.toList() ) );
    }

    private SiteJson createSiteJson( final Content site )
    {
        final SiteJson.Builder builder = SiteJson.create().displayName( site.getDisplayName() ).path( site.getPath().toString() );
        if ( site.getLanguage() != null )
        {
            builder.language( site.getLanguage().getLanguage() );
        }

        return builder.build();
    }
}
