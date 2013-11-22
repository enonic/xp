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

    private final String collection;

    private final Map<String, PropertyIndexConfigJson> propertyIndexConfigs;

    public EntityIndexConfigJson( final EntityIndexConfig entityIndexConfig )
    {
        this.analyzer = entityIndexConfig.getAnalyzer();
        this.propertyIndexConfigs = translateMap( entityIndexConfig.getPropertyIndexConfigs() );
        this.collection = entityIndexConfig.getCollection();
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public EntityIndexConfigJson( @JsonProperty("analyzer") final String analyzer, @JsonProperty("collection") final String collection,
                                  @JsonProperty("propertyIndexConfigs") final Map<String, PropertyIndexConfigJson> propertyIndexConfigs )
    {
        this.analyzer = analyzer;
        this.propertyIndexConfigs = propertyIndexConfigs;
        this.collection = collection;
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
        final EntityIndexConfig.Builder builder = EntityIndexConfig.newEntityIndexConfig().
            analyzer( this.analyzer ).
            collection( this.collection );

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
    public String getCollection()
    {
        return collection;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<String, PropertyIndexConfigJson> getPropertyIndexConfigs()
    {
        return propertyIndexConfigs;
    }
}
