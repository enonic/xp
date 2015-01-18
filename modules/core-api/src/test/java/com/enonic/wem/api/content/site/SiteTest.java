package com.enonic.wem.api.content.site;


import org.junit.Test;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.module.ModuleKey;

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
