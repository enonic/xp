package com.enonic.wem.core.entity;


import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;

public class EntityIndexConfigJson
{
    private final String analyzer;

    private final Map<String, PropertyIndexConfigJson> propertyIndexConfigs;

    public EntityIndexConfigJson( final EntityIndexConfig entityIndexConfig )
    {
        this.analyzer = entityIndexConfig.getAnalyzer();
        this.propertyIndexConfigs = translateMap( entityIndexConfig.getPropertyIndexConfigs() );
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public EntityIndexConfigJson( @JsonProperty("analyzer") final String analyzer,
                                  @JsonProperty("propertyIndexConfigs") final Map<String, PropertyIndexConfigJson> propertyIndexConfigs )
    {
        this.analyzer = analyzer;
        this.propertyIndexConfigs = propertyIndexConfigs;
    }

    private Map<String, PropertyIndexConfigJson> translateMap( final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs )
    {
        Map<String, PropertyIndexConfigJson> translatedMap = Maps.newHashMap();

        for ( final DataPath path : propertyIndexConfigs.keySet() )
        {
            translatedMap.put( path.toString(), new PropertyIndexConfigJson( propertyIndexConfigs.get( path ) ) );
        }

        return translatedMap;
    }

    public EntityIndexConfig toEntityIndexConfig()
    {
        final EntityIndexConfig.Builder builder = EntityIndexConfig.newEntityIndexConfig().analyzer( this.analyzer );

        for ( final String path : this.propertyIndexConfigs.keySet() )
        {
            final PropertyIndexConfigJson propertyIndexConfigJson = propertyIndexConfigs.get( path );

            builder.addPropertyIndexConfig( path, PropertyIndexConfig.newPropertyIndexConfig().
                tokenizedEnabled( propertyIndexConfigJson.getTokenizedEnabled() ).
                fulltextEnabled( propertyIndexConfigJson.getFulltextEnabled() ).
                enabled( propertyIndexConfigJson.getEnabled() ).
                build() );
        }

        return builder.build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getAnalyzer()
    {
        return analyzer;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<String, PropertyIndexConfigJson> getPropertyIndexConfigs()
    {
        return propertyIndexConfigs;
    }
}
