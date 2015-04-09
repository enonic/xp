package com.enonic.xp.content.site;


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
        ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            build();
        Site site = Site.newSite().
            addModuleConfig( moduleConfig ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();

        Site site1 = Site.newSite(site).build();

        assertEquals( site, site1 );
    }

    @Test
    public void moduleConfigs()
    {
        ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            build();
        Site site = Site.newSite().
            moduleConfigs( ModuleConfigs.from( moduleConfig ) ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            build();

        ModuleConfigs moduleConfigs = site.getModuleConfigs();
        assertNotNull( moduleConfigs );
        assertEquals( 1, moduleConfigs.getSize() );
        assertEquals( moduleConfigs.get( 0 ).getConfig(), site.getModuleConfig( ModuleKey.from( "mymodule" ) ) );
        assertNotNull( moduleConfigs.get( "mymodule" ) );
        assertTrue( ModuleConfigs.empty().getSize() == 0 );
        assertTrue( ModuleConfigs.from( moduleConfig ).getSize() == 1 );
    }

    @Test
    public void moduleConfigEquals()
    {
        ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            build();
        ModuleConfig moduleConfig1 = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            build();

        assertEquals( moduleConfig, moduleConfig );
        assertEquals( moduleConfig, moduleConfig1 );
        assertNotEquals( moduleConfig, null );
    }

    @Test
    public void createSiteParams()
    {
        CreateSiteParams params = new CreateSiteParams();
        params.parent( ContentPath.ROOT );
        params.name( ContentName.from( "siteName" ) );
        params.displayName( "displayName" );
        params.description( "description" );
        params.moduleConfigs( ModuleConfigs.empty() );
        params.requireValid();

        assertEquals( params.getParentContentPath(), ContentPath.ROOT );
        assertEquals( params.getName(), ContentName.from( "siteName" ) );
        assertEquals( params.getDisplayName(), "displayName" );
        assertEquals( params.getDescription(), "description" );
        assertEquals( params.isRequireValid(), true );
        assertNotNull( params.getModuleConfigs() );
        assertTrue( params.getModuleConfigs().getSize() == 0 );
    }
}
