package com.enonic.wem.core.search.elastic;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;

import com.enonic.wem.core.config.ConfigProperties;

@Component
public final class NodeSettingsBuilder
{
    private final static String PROPERTIES_PREFIX = "cms.elasticsearch";

    private final static String INDEX_PROPERTIES_PREFIX = PROPERTIES_PREFIX + ".index";

    private ConfigProperties configProperties;

    public Settings buildNodeSettings()
    {
        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();

        final Map<String, String> nodePropertyMap = getNodePropertyMap();
        populateSettings( settings, nodePropertyMap, PROPERTIES_PREFIX );

        return settings.build();
    }

    @Autowired
    public void setConfigProperties( final ConfigProperties configProperties )
    {
        this.configProperties = configProperties;
    }

    private Map<String, String> getNodePropertyMap()
    {
        return this.configProperties.getSubMap( new Predicate<String>()
        {
            @Override
            public boolean apply( final String input )
            {
                return StringUtils.startsWith( input, PROPERTIES_PREFIX ) && !StringUtils.startsWith( input, INDEX_PROPERTIES_PREFIX );
            }
        } );
    }

    private void populateSettings( final ImmutableSettings.Builder settings, final Map<String, String> propertyMap,
                                   final String propertyPrefix )
    {
        for ( final String property : propertyMap.keySet() )
        {
            final String indexPropertyValue = propertyMap.get( property ).trim();
            final String indexPropertyName = subtractPrefixFromProperty( property, propertyPrefix );
            settings.put( indexPropertyName, indexPropertyValue );
        }
    }

    private String subtractPrefixFromProperty( final String property, final String propertyPrefix )
    {
        return StringUtils.substringAfter( property, propertyPrefix + "." );
    }
}
