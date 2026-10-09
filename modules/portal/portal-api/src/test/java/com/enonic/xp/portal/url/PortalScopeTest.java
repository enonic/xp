package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortalScopeTest
{
    @Test
    void testProjectByDefault()
    {
        final PortalScope scope = PortalScope.create().projectName( ProjectName.from( "myproject" ) ).branch( Branch.from( "master" ) ).build();

        assertEquals( ContentPath.ROOT, scope.getPath() );
        assertEquals( ContentPath.ROOT, scope.getSitePath() );
        assertNull( scope.getBaseUrl() );
        assertTrue( scope.getApplications().isEmpty() );
    }

    @Test
    void testBaseUrlOfSite()
    {
        assertEquals( "https://example.com", scope( "/my-site", "/my-site", portalConfig( "https://example.com/" ) ).getBaseUrl() );
    }

    @Test
    void testBaseUrlOfContentInsideSite()
    {
        // the Base URL of the site is followed by the URL-escaped path of the content below it
        assertEquals( "https://example.com/bl%C3%A5b%C3%A6r/posts",
                      scope( "/my-site/blåbær/posts", "/my-site", portalConfig( "https://example.com" ) ).getBaseUrl() );
    }

    @Test
    void testBaseUrlOfContentOutsideAnySite()
    {
        assertEquals( "https://example.com/libraries", scope( "/libraries", "/", portalConfig( "https://example.com" ) ).getBaseUrl() );
    }

    @Test
    void testBaseUrlNotConfigured()
    {
        assertNull( scope( "/my-site/posts", "/my-site", SiteConfigs.empty() ).getBaseUrl() );
    }

    private static PortalScope scope( final String path, final String sitePath, final SiteConfigs siteConfigs )
    {
        return PortalScope.create()
            .projectName( ProjectName.from( "myproject" ) )
            .branch( Branch.from( "master" ) )
            .path( ContentPath.from( path ) )
            .sitePath( ContentPath.from( sitePath ) )
            .siteConfigs( siteConfigs )
            .build();
    }

    private static SiteConfigs portalConfig( final String baseUrl )
    {
        final PropertyTree config = new PropertyTree();
        config.setString( "baseUrl", baseUrl );
        return SiteConfigs.from( SiteConfig.create().application( ApplicationKey.PORTAL ).config( config ).build() );
    }
}
