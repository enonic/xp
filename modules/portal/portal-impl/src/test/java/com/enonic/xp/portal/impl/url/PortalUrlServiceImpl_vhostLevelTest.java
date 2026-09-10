package com.enonic.xp.portal.impl.url;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A virtual host mapping states which part of the content tree its address stands for, so it
 * decides the level URLs following the request belong to - the same way selecting a site does.
 */
class PortalUrlServiceImpl_vhostLevelTest
    extends AbstractPortalUrlServiceImplTest
{
    private Content folder;

    @BeforeEach
    void setupSiteRequest()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setRawPath( "/site/myproject/draft/features/subsite/folder" );
        portalRequest.setContentPath( ContentPath.from( "/features/subsite/folder" ) );

        this.folder = Content.create()
            .id( ContentId.from( "folderid" ) )
            .name( "folder" )
            .displayName( "folder" )
            .parentPath( ContentPath.from( "/features/subsite" ) )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .createdTime( Instant.ofEpochSecond( 0 ) )
            .build();

        when( contentService.getById( eq( folder.getId() ) ) ).thenReturn( folder );

        final Site subsite = mockSite( "/features/subsite" );
        mockSite( "/features" );
        when( contentService.getNearestSite( eq( folder.getId() ) ) ).thenReturn( subsite );
    }

    private Site mockSite( final String path )
    {
        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( path ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        final PropertyTree data = new PropertyTree();
        when( site.getData() ).thenReturn( data );
        SiteConfigsDataSerializer.toData( SiteConfigs.empty(), data.getRoot() );

        when( contentService.getByPath( eq( ContentPath.from( path ) ) ) ).thenReturn( site );

        return site;
    }

    private void mountVhost( final String source, final String target )
    {
        when( virtualHost.getSource() ).thenReturn( source );
        when( virtualHost.getTarget() ).thenReturn( target );
    }

    private String url()
    {
        return this.service.pageUrl( new PageUrlParams().id( "folderid" ) );
    }

    private String path()
    {
        return this.service.pageUrlParts( new PageUrlParams().id( "folderid" ) ).path();
    }

    private String baseUrl()
    {
        return this.service.baseUrl( BaseUrlParams.create().setId( "folderid" ).build() );
    }

    @Test
    void testMountedOnTheSiteOfTheContent()
    {
        mountVhost( "/source", "/site/myproject/draft/features/subsite" );

        assertEquals( "/source/folder", url() );
        assertEquals( "/folder", path() );
        assertEquals( "/source", baseUrl() );
    }

    @Test
    void testMountedOnAnOuterSite()
    {
        mountVhost( "/source", "/site/myproject/draft/features" );

        // the host mounts /features, so that is the level - the nested site stays in the path
        assertEquals( "/source/subsite/folder", url() );
        assertEquals( "/subsite/folder", path() );
        assertEquals( "/source", baseUrl() );
    }

    @Test
    void testMountedOnTheProject()
    {
        mountVhost( "/source", "/site/myproject/draft" );

        assertEquals( "/source/features/subsite/folder", url() );
        assertEquals( "/features/subsite/folder", path() );
        assertEquals( "/source", baseUrl() );
    }

    @Test
    void testMountedAtTheRootOfTheHost()
    {
        mountVhost( "/", "/site/myproject/draft/features" );

        assertEquals( "/subsite/folder", url() );
        assertEquals( "/subsite/folder", path() );
        // the level is the whole host, so its base is the host itself
        assertEquals( "", baseUrl() );
    }

    @Test
    void testWithoutMatchingVhost()
    {
        // nothing narrows the request, so the site engine serves the whole project: that is the
        // level, and the full content path follows it
        assertEquals( "/site/myproject/draft/features/subsite/folder", url() );
        assertEquals( "/features/subsite/folder", path() );
        assertEquals( "/site/myproject/draft", baseUrl() );
    }

    @Test
    void testSelectedLevelOutranksTheVhost()
    {
        mountVhost( "/source", "/site/myproject/draft/features/subsite" );

        final PageUrlParams params =
            new PageUrlParams().id( "folderid" ).base( BaseUrlParams.create().setPath( "/features" ).build() );

        assertEquals( "/site/myproject/draft/features/subsite/folder", this.service.pageUrl( params ) );
        assertEquals( "/subsite/folder", this.service.pageUrlParts( params ).path() );
    }
}
