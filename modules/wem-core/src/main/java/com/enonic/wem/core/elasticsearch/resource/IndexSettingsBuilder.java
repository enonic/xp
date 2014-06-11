package com.enonic.wem.core.elasticsearch.resource;

import javax.inject.Inject;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import com.enonic.wem.core.index.Index;


public class IndexSettingsBuilder
    extends AbstractSettingsBuilder
{
    private IndexSettingsSourceProvider indexSettingsSourceProvider;

    public Settings buildIndexSettings( final Index index )
    {
        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();

        applySettingsFromFile( settings, index );

        //applyConfigSettings( settings );

        return settings.build();
    }

    private void applySettingsFromFile( final ImmutableSettings.Builder settings, final Index index )
    {
        final String settingsSource = indexSettingsSourceProvider.getSource( index );

        LOG.info( "---> Loaded settings for index " + index );

        settings.loadFromSource( settingsSource );
    }

    /*
    private void applyConfigSettings( final ImmutableSettings.Builder settings )
    {
        final Map<String, String> indexConfigPropertiesMap = getIndexConfigPropertiesMap();

        populateSettings( settings, indexConfigPropertiesMap, ELASTICSEARCH_PROPERTIES_PREFIX );
    }

    private Map<String, String> getIndexConfigPropertiesMap()
    {
        return configProperties.getSubConfig( new Predicate<String>()
        {
            @Override
            public boolean apply( final String input )
            {
                return StringUtils.startsWith( input, INDEX_PROPERTIES_PREFIX );
            }
        } );
    }
    */

    @Inject
    public void setIndexSettingsSourceProvider( final IndexSettingsSourceProvider indexSettingsSourceProvider )
    {
        this.indexSettingsSourceProvider = indexSettingsSourceProvider;
    }
}
