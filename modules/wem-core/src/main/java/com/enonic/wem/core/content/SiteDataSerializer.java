package com.enonic.wem.core.content;

import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.DataId;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;

class SiteDataSerializer
{
    static final String SITE_MODULE_CONFIG = "moduleConfig";

    static final String SITE_MODULE = "module";

    static final String SITE_CONFIG = "config";

    static final String SITE_MODULE_CONFIGS = "moduleConfigs";

    private final String dataSetName;

    SiteDataSerializer( final String dataSetName )
    {
        this.dataSetName = dataSetName;
    }

    DataSet toData( final Site site )
    {
        final DataSet siteDataSet = new DataSet( dataSetName );
        siteDataSet.setProperty( DataId.from( "description", 0 ), Value.newString( site.getDescription() ) );

        final DataSet moduleConfigs = new DataSet( SITE_MODULE_CONFIGS );

        for ( ModuleConfig moduleConfig : site.getModuleConfigs() )
        {
            final DataSet moduleConfigDataSet = new DataSet( SITE_MODULE_CONFIG );

            moduleConfigDataSet.setProperty( SITE_MODULE, Value.newString( moduleConfig.getModule().toString() ) );
            moduleConfigDataSet.add( moduleConfig.getConfig().toDataSet( SITE_CONFIG ) );

            moduleConfigs.add( moduleConfigDataSet );
        }

        siteDataSet.add( moduleConfigs );

        return siteDataSet;
    }
}
