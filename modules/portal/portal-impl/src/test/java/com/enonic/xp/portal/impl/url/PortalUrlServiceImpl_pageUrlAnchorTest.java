package com.enonic.xp.portal.impl.url;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.ContentOutOfScopeException;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PortalScopeParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A site with a nested site inside it: {@code /features} contains the site
 * {@code /features/subsite}, which contains {@code /features/subsite/folder}.
 * <p>
 * Configuration of a site is never inherited from a parent site, so which of the two sites a URL
 * belongs to decides both its base URL and the path that follows: the scope selected through
 * {@link PageUrlPartsParams.Builder#setScope} for the parts, and the site of the content for the page URL.
 */
class PortalUrlServiceImpl_pageUrlAnchorTest
    extends AbstractPortalUrlServiceImplTest
{
    private static final ContentPath FEATURES = ContentPath.from( "/features" );

    private static final ContentPath SUBSITE = ContentPath.from( "/features/subsite" );

    private static final ContentPath FOLDER = ContentPath.from( "/features/subsite/folder" );

    private Content folder;

    @BeforeEach
    void setupNestedSites()
    {
        PortalRequestAccessor.set( null );

        this.folder = Content.create()
            .id( ContentId.from( "folderid" ) )
            .name( "folder" )
            .displayName( "folder" )
            .parentPath( SUBSITE )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .createdTime( Instant.ofEpochSecond( 0 ) )
            .build();

        when( this.contentService.getById( eq( this.folder.getId() ) ) ).thenReturn( this.folder );
        when( this.contentService.getByPath( eq( FOLDER ) ) ).thenReturn( this.folder );
    }

    private Site mockSite( final ContentPath path, final String baseUrl )
    {
        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( path );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        final SiteConfigs.Builder siteConfigs = SiteConfigs.create();
        if ( baseUrl != null )
        {
            final PropertyTree config = new PropertyTree();
            config.addString( "baseUrl", baseUrl );
            siteConfigs.add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() );
        }

        final PropertyTree data = new PropertyTree();
        when( site.getData() ).thenReturn( data );
        SiteConfigsDataSerializer.toData( siteConfigs.build(), data.getRoot() );

        when( this.contentService.getByPath( eq( path ) ) ).thenReturn( site );

        return site;
    }

    /**
     * @param featuresBaseUrl Base URL configured on {@code /features}, or {@code null} for none
     * @param subsiteBaseUrl  Base URL configured on {@code /features/subsite}, or {@code null}
     */
    private void mockNestedSites( final String featuresBaseUrl, final String subsiteBaseUrl )
    {
        mockSite( FEATURES, featuresBaseUrl );
        final Site subsite = mockSite( SUBSITE, subsiteBaseUrl );

        when( this.contentService.getNearestSite( eq( this.folder.getId() ) ) ).thenReturn( subsite );
    }

    /**
     * @param baseKey id or path of the content naming the scope
     */
    private PageUrlParts parts( final ContentPath content, final String baseKey )
    {
        final PortalScopeParams.Builder scope = PortalScopeParams.create();
        if ( baseKey.startsWith( "/" ) )
        {
            scope.setContentPath( ContentPath.from( baseKey ) );
        }
        else
        {
            scope.setContentId( ContentId.from( baseKey ) );
        }

        return this.service.pageUrlParts(
            PageUrlPartsParams.create().setPath( content.toString() ).setScope( this.service.portalScope( scope.build() ) ).build() );
    }

    private PageUrlParts pageUrlPartsAnchoredAtFeatures()
    {
        return parts( FOLDER, FEATURES.toString() );
    }

    @Test
    void testAnchoredAtParentSiteWithBaseUrlOnBothSites()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        assertEquals( "https://features.com", this.service.baseUrl( BaseUrlParams.create().setPath( FEATURES.toString() ).build() ) );
        final PageUrlParts parts = pageUrlPartsAnchoredAtFeatures();
        assertEquals( "https://features.com", parts.baseUrl() );
        assertEquals( "/subsite/folder", parts.path() );
    }

    @Test
    void testAnchoredAtParentSiteWithBaseUrlOnNestedSiteOnly()
    {
        mockNestedSites( null, "https://subsite.com" );

        // the parts come from configuration alone, and the Base URL of the nested site belongs to the nested site
        final PageUrlParts parts = pageUrlPartsAnchoredAtFeatures();
        assertNull( parts.baseUrl() );
        assertEquals( "/subsite/folder", parts.path() );
    }

    @Test
    void testAnchoredAtParentSiteWithoutBaseUrls()
    {
        mockNestedSites( null, null );

        assertNull( pageUrlPartsAnchoredAtFeatures().baseUrl() );
        assertEquals( "/subsite/folder", pageUrlPartsAnchoredAtFeatures().path() );
    }

    @Test
    void testAnchoredAtParentSiteWithBaseUrlOnParentSiteOnly()
    {
        mockNestedSites( "https://features.com", null );

        assertEquals( "https://features.com", pageUrlPartsAnchoredAtFeatures().baseUrl() );
        assertEquals( "/subsite/folder", pageUrlPartsAnchoredAtFeatures().path() );
    }

    @Test
    void testAnchoredAtNestedSite()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        final PageUrlParts parts = parts( FOLDER, SUBSITE.toString() );

        assertEquals( "https://subsite.com", parts.baseUrl() );
        assertEquals( "/folder", parts.path() );
        assertEquals( this.service.pageUrl( new PageUrlParams().path( FOLDER.toString() ) ), parts.baseUrl() + parts.path() );
    }

    @Test
    void testPageUrlUsesSiteOfContent()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        assertEquals( "https://subsite.com/folder", this.service.pageUrl( new PageUrlParams().path( FOLDER.toString() ) ) );
    }

    @Test
    void testPageUrlUsesSiteOfContentWithoutBaseUrls()
    {
        mockNestedSites( null, null );

        assertEquals( "/site/myproject/draft/features/subsite/folder",
                      this.service.pageUrl( new PageUrlParams().path( FOLDER.toString() ) ) );
    }

    @Test
    void testAnchoredAtProjectRoot()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // the project is selected, so its configuration applies and the full content path follows
        final PageUrlParts parts = parts( FOLDER, "/" );
        assertNull( parts.baseUrl() );
        assertEquals( "/features/subsite/folder", parts.path() );
    }

    @Test
    void testAnchoredAtContentInsideSite()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // paths start at the content, as at a content a virtual host mounts, and the Base URL of its site is
        // followed by its path there
        final PageUrlParts parts = parts( FOLDER, this.folder.getId().toString() );

        assertEquals( "https://subsite.com/folder", parts.baseUrl() );
        assertEquals( "", parts.path() );

        // the URL is the one of the site: only where its path starts moves
        final PageUrlParts siteParts = parts( FOLDER, SUBSITE.toString() );
        assertEquals( siteParts.baseUrl() + siteParts.path(), parts.baseUrl() + parts.path() );
    }

    @Test
    void testContentAboveTheSelectedContent()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // the site of the selected folder lies above it, so outside the scope
        assertThatThrownBy( () -> parts( SUBSITE, this.folder.getId().toString() ) ).isInstanceOf( ContentOutOfScopeException.class );
    }

    @Test
    void testSelectedSiteBesideTheContent()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // /features/sub is a sibling of /features/subsite
        mockSite( ContentPath.from( "/features/sub" ), "https://sub.com" );

        assertThatThrownBy( () -> parts( FOLDER, "/features/sub" ) ).isInstanceOf( ContentOutOfScopeException.class )
            .hasMessageContaining( "/features/subsite/folder" )
            .hasMessageContaining( "/features/sub" );
    }

    @Test
    void testSelectedSiteIsInsideTheContent()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // the content is the parent site, above the selected one
        assertThatThrownBy( () -> parts( FEATURES, SUBSITE.toString() ) ).isInstanceOf( ContentOutOfScopeException.class );
    }

    @Test
    void testPartsFollowTheSelectedSiteUnderSiteRequest()
    {
        PortalRequestAccessor.set( this.portalRequest );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setRawPath( "/site/myproject/draft/features/subsite/folder" );
        portalRequest.setContentPath( FOLDER );

        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/features/subsite" );

        mockNestedSites( "https://features.com", "https://subsite.com" );

        // pageUrl follows the request, rewritten by the virtual host; the parts follow configuration
        assertEquals( "/source/folder", this.service.pageUrl( new PageUrlParams().id( this.folder.getId().toString() ) ) );
        final PageUrlParts parts = parts( FOLDER, FEATURES.toString() );
        assertEquals( "https://features.com", parts.baseUrl() );
        assertEquals( "/subsite/folder", parts.path() );
    }

    @Test
    void testPartsAtProjectRootReadTheProjectFromTheProjectService()
    {
        PortalRequestAccessor.set( this.portalRequest );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setProject( project( "https://stale.com" ) );

        final Project current = project( "https://current.com" );
        when( this.projectService.get( eq( ProjectName.from( "myproject" ) ) ) ).thenReturn( current );

        mockNestedSites( null, null );

        final PageUrlParts parts = parts( FOLDER, "/" );
        assertEquals( "https://current.com", parts.baseUrl() );
        assertEquals( "/features/subsite/folder", parts.path() );
    }

    private static Project project( final String baseUrl )
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", baseUrl );
        return Project.create()
            .name( ProjectName.from( "myproject" ) )
            .addSiteConfig( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() )
            .build();
    }
}
