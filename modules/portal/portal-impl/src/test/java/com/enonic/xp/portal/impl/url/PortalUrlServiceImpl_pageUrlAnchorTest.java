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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A site with a nested site inside it: {@code /features} contains the site
 * {@code /features/subsite}, which contains {@code /features/subsite/folder}.
 * <p>
 * Configuration of a site is never inherited from a parent site, so which of the two sites a URL
 * belongs to decides both its base URL and the path that follows: the site selected through
 * {@link com.enonic.xp.portal.url.PageUrlParams#base}, and the site of the content otherwise.
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

    private static BaseUrlParams siteBase( final String contentKey )
    {
        final BaseUrlParams.Builder builder = BaseUrlParams.create();
        if ( contentKey.startsWith( "/" ) )
        {
            builder.setPath( contentKey );
        }
        else
        {
            builder.setId( contentKey );
        }
        return builder.build();
    }

    private String pageUrlAnchoredAtFeatures()
    {
        return this.service.pageUrl( new PageUrlParams().path( FOLDER.toString() ).base( siteBase( FEATURES.toString() ) ) );
    }

    private PageUrlParts pageUrlPartsAnchoredAtFeatures()
    {
        return this.service.pageUrlParts( new PageUrlParams().path( FOLDER.toString() ).base( siteBase( FEATURES.toString() ) ) );
    }

    @Test
    void testAnchoredAtParentSiteWithBaseUrlOnBothSites()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // the very same params drive baseUrl() and the page URL, so the parts reassemble
        final BaseUrlParams base = siteBase( FEATURES.toString() );

        assertEquals( "https://features.com", this.service.baseUrl( base ) );
        assertEquals( "https://features.com/subsite/folder", this.service.pageUrl(
            new PageUrlParams().path( FOLDER.toString() ).base( base ) ) );
        assertEquals( "/subsite/folder", this.service.pageUrlParts(
            new PageUrlParams().path( FOLDER.toString() ).base( base ) ).path() );
    }

    @Test
    void testAnchoredAtParentSiteWithBaseUrlOnNestedSiteOnly()
    {
        mockNestedSites( null, "https://subsite.com" );

        // the Base URL of the nested site does not apply to a URL that belongs to its parent
        assertEquals( "/site/myproject/draft/features", this.service.baseUrl( siteBase( FEATURES.toString() ) ) );
        assertEquals( "/site/myproject/draft/features/subsite/folder", pageUrlAnchoredAtFeatures() );
        assertEquals( "/subsite/folder", pageUrlPartsAnchoredAtFeatures().path() );
    }

    @Test
    void testAnchoredAtParentSiteWithoutBaseUrls()
    {
        mockNestedSites( null, null );

        assertEquals( "/site/myproject/draft/features/subsite/folder", pageUrlAnchoredAtFeatures() );
        assertEquals( "/subsite/folder", pageUrlPartsAnchoredAtFeatures().path() );
    }

    @Test
    void testAnchoredAtParentSiteWithBaseUrlOnParentSiteOnly()
    {
        mockNestedSites( "https://features.com", null );

        assertEquals( "https://features.com/subsite/folder", pageUrlAnchoredAtFeatures() );
        assertEquals( "/subsite/folder", pageUrlPartsAnchoredAtFeatures().path() );
    }

    @Test
    void testAnchoredAtNestedSite()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        final PageUrlParams params = new PageUrlParams().path( FOLDER.toString() ).base( siteBase( SUBSITE.toString() ) );

        assertEquals( "https://subsite.com/folder", this.service.pageUrl( params ) );
        assertEquals( "/folder", this.service.pageUrlParts( params ).path() );
    }

    @Test
    void testWithoutAnchorUsesSiteOfContent()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        final PageUrlParams params = new PageUrlParams().path( FOLDER.toString() );

        assertEquals( "https://subsite.com/folder", this.service.pageUrl( params ) );
        assertEquals( "/folder", this.service.pageUrlParts( params ).path() );
    }

    @Test
    void testWithoutAnchorUsesSiteOfContentWithoutBaseUrls()
    {
        mockNestedSites( null, null );

        final PageUrlParams params = new PageUrlParams().path( FOLDER.toString() );

        assertEquals( "/site/myproject/draft/features/subsite/folder", this.service.pageUrl( params ) );
        assertEquals( "/folder", this.service.pageUrlParts( params ).path() );
    }

    @Test
    void testAnchoredAtProjectRoot()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        final PageUrlParams params = new PageUrlParams().path( FOLDER.toString() ).base( siteBase( "/" ) );

        // no site is selected, so the project decides and the full content path follows
        assertEquals( "/site/myproject/draft/features/subsite/folder", this.service.pageUrl( params ) );
        assertEquals( "/features/subsite/folder", this.service.pageUrlParts( params ).path() );
    }

    @Test
    void testAnchoredAtContentInsideSite()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // selecting a content that is not itself a site selects the site it belongs to
        final PageUrlParams params = new PageUrlParams().path( FOLDER.toString() ).base( siteBase( this.folder.getId().toString() ) );

        assertEquals( "https://subsite.com/folder", this.service.pageUrl( params ) );
    }

    @Test
    void testSelectedSiteDoesNotContainTheContent()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // /features/sub is a sibling of /features/subsite, not an ancestor of the content
        mockSite( ContentPath.from( "/features/sub" ), "https://sub.com" );

        final PageUrlParams params = new PageUrlParams().path( FOLDER.toString() ).base( siteBase( "/features/sub" ) );

        // the Base URL of that site does not lead to the content, so there is no URL to give
        assertThatThrownBy( () -> this.service.pageUrl( params ) ).isInstanceOf( ContentOutOfScopeException.class )
            .hasMessageContaining( "/features/subsite/folder" )
            .hasMessageContaining( "/features/sub" );
        assertThatThrownBy( () -> this.service.pageUrlParts( params ) ).isInstanceOf( ContentOutOfScopeException.class );
    }

    @Test
    void testSelectedSiteIsInsideTheContent()
    {
        mockNestedSites( "https://features.com", "https://subsite.com" );

        // the content is the parent site, above the selected one - equally unreachable from it
        final PageUrlParams params = new PageUrlParams().path( FEATURES.toString() ).base( siteBase( SUBSITE.toString() ) );

        assertThatThrownBy( () -> this.service.pageUrl( params ) ).isInstanceOf( ContentOutOfScopeException.class );
        assertThatThrownBy( () -> this.service.pageUrlParts( params ) ).isInstanceOf( ContentOutOfScopeException.class );
    }

    @Test
    void testAnchorOutranksSiteRequest()
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

        // the request is followed - and rewritten by the virtual host - only without a selected site
        assertEquals( "/source/folder", this.service.pageUrl( new PageUrlParams().id( this.folder.getId().toString() ) ) );
        assertEquals( "https://features.com/subsite/folder",
                      this.service.pageUrl( new PageUrlParams().id( this.folder.getId().toString() ).base( siteBase( FEATURES.toString() ) ) ) );
    }
}
