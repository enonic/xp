package com.enonic.wem.core.index.elastic;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.core.config.ConfigProperties;

public class AbstractSettingsBuilder
{

    final static String ELASTICSEARCH_PROPERTIES_PREFIX = "cms.elasticsearch";

    final static String INDEX_PROPERTIES_PREFIX = ELASTICSEARCH_PROPERTIES_PREFIX + ".index";

    ConfigProperties configProperties;

    private final Logger LOG = LoggerFactory.getLogger( AbstractSettingsBuilder.class );

    String subtractPrefixFromProperty( final String property, final String propertyPrefix )
    {
        return StringUtils.substringAfter( property, propertyPrefix + "." );
    }

    void populateSettings( final ImmutableSettings.Builder settings, final Map<String, String> propertyMap, final String propertyPrefix )
    {
        for ( final String property : propertyMap.keySet() )
        {
            String indexPropertyValue = propertyMap.get( property );
            indexPropertyValue = cleanUpPropertyValue( indexPropertyValue );
            final String indexPropertyName = subtractPrefixFromProperty( property, propertyPrefix );

            LOG.info( "Apply elasticsearch setting: " + indexPropertyName + " = " + indexPropertyValue );

            settings.put( indexPropertyName, indexPropertyValue );
        }
    }

    private String cleanUpPropertyValue( String indexPropertyValue )
    {
        indexPropertyValue = Strings.trimLeadingWhitespace( indexPropertyValue );
        indexPropertyValue = Strings.trimTrailingWhitespace( indexPropertyValue );
        return indexPropertyValue;
    }

    @Inject
    public void setConfigProperties( final ConfigProperties configProperties )
    {
        this.configProperties = configProperties;
    }

}
