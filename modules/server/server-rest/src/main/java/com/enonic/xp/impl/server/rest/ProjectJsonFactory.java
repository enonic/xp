package com.enonic.xp.impl.server.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

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
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.query.filter.ValueFilter;

/**
 * Builds the project listing (projects, their draft and master branches, and the sites on each) shared by the legacy
 * {@code /content/projects/list} resource and the {@code server:project} API.
 */
public final class ProjectJsonFactory
{
    private final ProjectService projectService;

    private final ContentService contentService;

    public ProjectJsonFactory( final ProjectService projectService, final ContentService contentService )
    {
        this.projectService = projectService;
        this.contentService = contentService;
    }

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
            .size( -1 )
            .build();

        final FindContentIdsByQueryResult result = context.callWith( () -> contentService.find( query ) );

        return context.callWith( () -> contentService.getByIds( GetContentByIdsParams.create().contentIds( result.getContentIds() ).build() )
            .stream()
            .map( ProjectJsonFactory::createSiteJson )
            .collect( Collectors.toList() ) );
    }

    private static SiteJson createSiteJson( final Content site )
    {
        final SiteJson.Builder builder = SiteJson.create().displayName( site.getDisplayName() ).path( site.getPath().toString() );
        if ( site.getLanguage() != null )
        {
            builder.language( site.getLanguage().toLanguageTag() );
        }

        return builder.build();
    }
}
