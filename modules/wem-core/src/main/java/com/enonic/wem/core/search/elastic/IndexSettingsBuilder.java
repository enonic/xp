package com.enonic.wem.core.search.elastic;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;

@Component
public class IndexSettingsBuilder
    extends AbstractSettingsBuilder
{
    private IndexSettingsSourceProvider indexSettingsSourceProvider;

    public Settings buildIndexSettings()
    {
        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();

        applySettingsFromFiles( settings );

        applyConfigSettings( settings );

        return settings.build();
    }

    private void applySettingsFromFiles( final ImmutableSettings.Builder settings )
    {
        final List<String> sources = indexSettingsSourceProvider.getSources();

        for ( String source : sources )
        {
            settings.loadFromSource( source );
        }
    }

    private void applyConfigSettings( final ImmutableSettings.Builder settings )
    {
        final Map<String, String> indexConfigPropertiesMap = getIndexConfigPropertiesMap();

        populateSettings( settings, indexConfigPropertiesMap, ELASTICSEARCH_PROPERTIES_PREFIX );
    }

    private Map<String, String> getIndexConfigPropertiesMap()
    {
        return configProperties.getSubMap( new Predicate<String>()
        {
            @Override
            public boolean apply( final String input )
            {
                return StringUtils.startsWith( input, INDEX_PROPERTIES_PREFIX );
            }
        } );
    }

    @Autowired
    public void setIndexSettingsSourceProvider( final IndexSettingsSourceProvider indexSettingsSourceProvider )
    {
        this.indexSettingsSourceProvider = indexSettingsSourceProvider;
    }
}
