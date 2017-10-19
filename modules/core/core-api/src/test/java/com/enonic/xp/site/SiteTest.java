package com.enonic.xp.site;


import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;

import static org.junit.Assert.*;

public class SiteTest
{

    @Test
    public void description()
    {
        Site site = Site.create().
            description( "This is my site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();
        assertEquals( "This is my site", site.getDescription() );
    }

    @Test
    public void siteEquals()
    {
        Site site = Site.create().
            description( "This is my site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();
        Site site1 = Site.create().
            description( "This is my another site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();
        assertEquals( site, site );
        assertNotEquals( site, null );
        assertNotEquals( site, site1 );
    }

    @Test
    public void builder()
    {
        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( new PropertyTree() ).
            build();
        Site site = Site.create().
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            addSiteConfig( siteConfig ).
            build();

        Site site1 = Site.create( site ).build();

        assertEquals( site, site1 );
    }

    @Test
    public void siteConfigs()
    {
        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( new PropertyTree() ).
            build();
        Site site = Site.create().
            siteConfigs( SiteConfigs.from( siteConfig ) ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();

        SiteConfigs siteConfigs = site.getSiteConfigs();
        assertNotNull( siteConfigs );
        assertEquals( 1, siteConfigs.getSize() );
        assertEquals( siteConfigs.get( 0 ).getConfig(), site.getSiteConfig( ApplicationKey.from( "myapplication" ) ) );
        assertNotNull( siteConfigs.get( "myapplication" ) );
        assertTrue( SiteConfigs.empty().getSize() == 0 );
        assertTrue( SiteConfigs.from( siteConfig ).getSize() == 1 );
    }

    @Test
    public void siteConfigEquals()
    {
        SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( new PropertyTree() ).
            build();
        SiteConfig siteConfig1 = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( new PropertyTree() ).
            build();

        assertEquals( siteConfig, siteConfig );
        assertEquals( siteConfig, siteConfig1 );
        assertNotEquals( siteConfig, null );
    }

    @Test
    public void createSiteParams()
    {
        CreateSiteParams params = new CreateSiteParams();
        params.parent( ContentPath.ROOT );
        params.name( ContentName.from( "siteName" ) );
        params.displayName( "displayName" );
        params.description( "description" );
        params.siteConfigs( SiteConfigs.empty() );
        params.requireValid();

        assertEquals( params.getParentContentPath(), ContentPath.ROOT );
        assertEquals( params.getName(), ContentName.from( "siteName" ) );
        assertEquals( params.getDisplayName(), "displayName" );
        assertEquals( params.getDescription(), "description" );
        assertEquals( params.isRequireValid(), true );
        assertNotNull( params.getSiteConfigs() );
        assertTrue( params.getSiteConfigs().getSize() == 0 );
    }
}
