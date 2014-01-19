package com.enonic.wem.core.content.site;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

public class SiteDataSerializer
    extends AbstractDataSetSerializer<Site,Site>
{
    static final String SITE_TEMPLATE = "template";

    static final String SITE_MODULE_CONFIG = "moduleConfig";

    static final String SITE_MODULE = "module";

    static final String SITE_CONFIG = "config";

    static final String SITE_MODULE_CONFIGS = "moduleConfigs";

    private final String dataSetName;

    public SiteDataSerializer( final String dataSetName )
    {
        this.dataSetName = dataSetName;
    }

    public DataSet toData( final Site site )
    {
        final DataSet siteDataSet = new DataSet( dataSetName );

        siteDataSet.addProperty( SITE_TEMPLATE, new Value.String( site.getTemplate().toString() ) );
        final DataSet moduleConfigs = new DataSet( SITE_MODULE_CONFIGS );

        for ( ModuleConfig moduleConfig : site.getModuleConfigs() )
        {
            final DataSet moduleConfigDataSet = new DataSet( SITE_MODULE_CONFIG );

            moduleConfigDataSet.setProperty( SITE_MODULE, new Value.String( moduleConfig.getModule().toString() ) );
            moduleConfigDataSet.add( moduleConfig.getConfig().toDataSet( SITE_CONFIG ) );

            moduleConfigs.add( moduleConfigDataSet );
        }

        siteDataSet.add( moduleConfigs );

        return siteDataSet;
    }

    public Site fromData( final DataSet dataSet )
    {
        final Site.Builder siteBuilder = Site.newSite().
            template( SiteTemplateKey.from( dataSet.getProperty( SITE_TEMPLATE ).getString() ) );

        final DataSet siteModuleConfigsDataSet = dataSet.getDataSet( SITE_MODULE_CONFIGS );

        final ImmutableList<DataSet> configDataSets = siteModuleConfigsDataSet.getDataSets();

        for ( final DataSet configDataSet : configDataSets )
        {
            if ( configDataSet.getName().equals( SITE_MODULE_CONFIG ) )
            {
                final ModuleConfig moduleConfig = getAsModuleConfig( configDataSet );

                siteBuilder.addModuleConfig( moduleConfig );
            }
        }

        return siteBuilder.build();
    }

    private ModuleConfig getAsModuleConfig( final DataSet configData )
    {
        return ModuleConfig.newModuleConfig().config( configData.getDataSet( SITE_CONFIG ).toRootDataSet() ).
            module( ModuleKey.from( configData.getProperty( SITE_MODULE ).getString() ) ).
            build();
    }

}
