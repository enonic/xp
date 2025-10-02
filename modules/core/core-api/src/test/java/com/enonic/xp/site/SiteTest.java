package com.enonic.xp.site;


import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final PropertyTree siteData = new PropertyTree();

        new SiteConfigsDataSerializer().toProperties(
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build(),
            siteData.getRoot() );

        final Site site = Site.create().
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();

        final Site site1 = Site.create( site ).build();

        assertEquals( site, site1 );
    }

    @Test
    public void siteConfigs()
    {
        final PropertyTree siteData = new PropertyTree();

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build();

        new SiteConfigsDataSerializer().toProperties( siteConfig, siteData.getRoot() );

        final Site site = Site.create().name( "my-content" ).data( siteData ).
            parentPath( ContentPath.ROOT ).
            build();

        final SiteConfigs siteConfigs = site.getSiteConfigs();
        assertNotNull( siteConfigs );
        assertEquals( 1, siteConfigs.getSize() );
        assertEquals( siteConfigs.get( 0 ).getConfig(), site.getSiteConfigs().get( ApplicationKey.from( "myapplication" ) ).getConfig() );
        assertNotNull( siteConfigs.get( ApplicationKey.from( "myapplication" ) ) );
        assertTrue( SiteConfigs.empty().isEmpty() );
        assertEquals( 1, SiteConfigs.from( siteConfig ).getSize() );
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
}
