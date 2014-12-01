package com.enonic.wem.core.content;

import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data2.PropertySet;

class SiteDataSerializer
{
    private static final String MODULE_CONFIG = "moduleConfig";

    private static final String MODULE = "module";

    private static final String CONFIG = "config";

    private final String dataSetName;

    SiteDataSerializer( final String dataSetName )
    {
        this.dataSetName = dataSetName;
    }

    void toData( final Site site, final PropertySet parent )
    {
        final PropertySet siteAsSet = parent.addSet( dataSetName );
        siteAsSet.setString( "description", site.getDescription() );

        for ( ModuleConfig moduleConfig : site.getModuleConfigs() )
        {
            final PropertySet moduleConfigDataSet = siteAsSet.addSet( MODULE_CONFIG );
            moduleConfigDataSet.setString( MODULE, moduleConfig.getModule().toString() );
            moduleConfigDataSet.addSet( CONFIG, moduleConfig.getConfig().getRoot().copy( moduleConfigDataSet.getTree() ) );
        }
    }
}
