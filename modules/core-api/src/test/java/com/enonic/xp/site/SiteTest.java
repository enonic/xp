package com.enonic.xp.site;


import org.junit.Test;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;

import static org.junit.Assert.*;

public class SiteTest
{

    @Test
    public void description()
    {
        Site site = Site.newSite().
            description( "This is my site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();
        assertEquals( "This is my site", site.getDescription() );
    }

    @Test
    public void siteEquals()
    {
        Site site = Site.newSite().
            description( "This is my site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();
        Site site1 = Site.newSite().
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
        SiteConfig siteConfig = SiteConfig.newSiteConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            build();
        Site site = Site.newSite().
            addSiteConfig( siteConfig ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();

        Site site1 = Site.newSite( site ).build();

        assertEquals( site, site1 );
    }

    @Test
    public void siteConfigs()
    {
        SiteConfig siteConfig = SiteConfig.newSiteConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            build();
        Site site = Site.newSite().
            siteConfigs( SiteConfigs.from( siteConfig ) ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();

        SiteConfigs siteConfigs = site.getSiteConfigs();
        assertNotNull( siteConfigs );
        assertEquals( 1, siteConfigs.getSize() );
        assertEquals( siteConfigs.get( 0 ).getConfig(), site.getSiteConfig( ModuleKey.from( "mymodule" ) ) );
        assertNotNull( siteConfigs.get( "mymodule" ) );
        assertTrue( SiteConfigs.empty().getSize() == 0 );
        assertTrue( SiteConfigs.from( siteConfig ).getSize() == 1 );
    }

    @Test
    public void siteConfigEquals()
    {
        SiteConfig siteConfig = SiteConfig.newSiteConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            build();
        SiteConfig siteConfig1 = SiteConfig.newSiteConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
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
