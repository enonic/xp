package com.enonic.xp.content.site;


import org.junit.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.site.ModuleConfig;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.content.site.Site;
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
    }

}
