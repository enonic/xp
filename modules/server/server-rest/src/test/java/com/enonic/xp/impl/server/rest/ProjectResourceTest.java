package com.enonic.xp.impl.server.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.assertj.core.api.recursive.comparison.ComparingProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.server.rest.model.BranchJson;
import com.enonic.xp.impl.server.rest.model.ProjectJson;
import com.enonic.xp.impl.server.rest.model.SiteJson;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectResourceTest
{
    private ProjectResource resource;

    @Mock
    private ContentService contentService;

    @Mock
    private ProjectService projectService;

    @BeforeEach
    void setUp()
    {
        resource = new ProjectResource( projectService, contentService );
    }

    @Test
    void testListProjects()
    {
        final Projects projects = Projects.create()
            .addAll( List.of( mockProject( ProjectName.from( "project1" ) ), mockProject( ProjectName.from( "project2" ) ) ) )
            .build();

        final Site site = createSite();

        final List<ProjectJson> expected = new ArrayList<>();

        for ( final Project project : projects )
        {
            expected.add( createProjectJson( site, project ) );
        }

        when( projectService.list() ).thenReturn( projects );

        when( contentService.find( isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().contents( ContentIds.from( site.getId() ) ).totalHits( 1L ).build() );

        when( contentService.getByIds( isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( site ) );

        final List<ProjectJson> result = resource.list();

        assertThat( result ).usingRecursiveComparison().withIntrospectionStrategy( new ComparingProperties() ).isEqualTo( expected );
    }

    @Test
    void testListEmpty()
    {
        when( projectService.list() ).thenReturn( Projects.empty() );

        final List<ProjectJson> result = resource.list();

        Assertions.assertEquals( Collections.emptyList(), result );
    }

    private ProjectJson createProjectJson( final Site site, final Project project )
    {
        return ProjectJson.create()
            .project( project )
            .addBranches( List.of( createBranchJson( "draft", List.of( site ) ), createBranchJson( "master", List.of( site ) ) ) )
            .build();
    }

    private BranchJson createBranchJson( final String name, final Collection<Site> sites )
    {
        return BranchJson.create()
            .name( name )
            .addSites( sites.stream().map( this::createSiteJson ).collect( Collectors.toList() ) )
            .build();
    }

    private SiteJson createSiteJson( final Site site )
    {
        return SiteJson.create().displayName( site.getDisplayName() ).language( "en" ).path( site.getPath().toString() ).build();
    }

    private Project mockProject( final ProjectName name )
    {
        final PropertyTree siteConfigConfig = new PropertyTree();
        siteConfigConfig.setLong( "long", 2L );
        siteConfigConfig.addBoolean( "boolean", true );
        siteConfigConfig.setString( "string", "str" );

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( siteConfigConfig ).build();

        final Project.Builder project = Project.create();

        project.name( name );
        project.displayName( "My Default Project" );
        project.description( "Project Description" );

        project.addSiteConfig( siteConfig );

        return project.build();
    }

    private Site createSite()
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().template( PageTemplateKey.from( "my-page" ) ).config( rootDataSet ).build();

        return Site.create()
            .id( ContentId.from( "123" ) )
            .path( ContentPath.from( "/my-site" ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.from( "portal:site" ) )
            .displayName( "My Content" )
            .language( Locale.ENGLISH )
            .page( page )
            .build();
    }

}
