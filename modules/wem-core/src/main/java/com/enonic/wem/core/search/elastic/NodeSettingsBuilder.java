package com.enonic.wem.core.search.elastic;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

@Component
final class NodeSettingsBuilder
    extends AbstractSettingsBuilder
{
    private final static Logger LOG = LoggerFactory.getLogger( NodeSettingsBuilder.class );

    public Settings buildNodeSettings()
    {
        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();

        final Map<String, String> nodePropertyMap = getNodePropertyMap();

        populateSettings( settings, nodePropertyMap, ELASTICSEARCH_PROPERTIES_PREFIX );

        checkClusterSettings( settings );

        return settings.build();
    }

    private void checkClusterSettings( final ImmutableSettings.Builder settings )
    {
        final Boolean local = getAsBoolean( settings.get( "node.local" ), null );
        final Boolean clusterEnabled = getAsBoolean( configProperties.getProperty( "cms.cluster.enabled" ), false );

        if ( local == null )
        {
            settings.put( "node.local", !clusterEnabled );
        }
        else
        {
            if ( local != ( !clusterEnabled ) )
            {
                LOG.warn( "Elasticsearch cluster enabled setting: '" + !local + "' differ from cms.cluster.enabled - property: '" +
                              clusterEnabled + "' which may cause unexpected behaviour" );
            }
        }
    }

    private Boolean getAsBoolean( String value, Boolean defaultValue )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return defaultValue;
        }

        return Boolean.valueOf( StringUtils.trimToNull( value ) );
    }

    private Map<String, String> getNodePropertyMap()
    {
        return configProperties.getSubMap( new Predicate<String>()
        {
            @Override
            public boolean apply( final String input )
            {
                return StringUtils.startsWith( input, ELASTICSEARCH_PROPERTIES_PREFIX ) &&
                    !StringUtils.startsWith( input, INDEX_PROPERTIES_PREFIX );
            }
        } );
    }
}
