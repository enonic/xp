package com.enonic.wem.core.entity;


import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.entity.relationship.EntityIndexConfigJson;

public class EntityPropertyIndexConfigJson
    extends EntityIndexConfigJson
{

    private final Map<String, PropertyIndexConfigJson> propertyIndexConfigs;

    public EntityPropertyIndexConfigJson( final EntityPropertyIndexConfig indexConfig )
    {
        super( indexConfig.getAnalyzer(), indexConfig.getCollection(), indexConfig.isDecideFulltextByValueType(), indexConfig.skip() );
        this.propertyIndexConfigs = translateMap( indexConfig.getPropertyIndexConfigs() );
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public EntityPropertyIndexConfigJson( @JsonProperty("analyzer") final String analyzer,
                                          @JsonProperty("collection") final String collection, //
                                          @JsonProperty("decideFulltextByValueType") final boolean decideFulltextByValueType, //
                                          @JsonProperty("skip") final boolean skip, //
                                          @JsonProperty(
                                              "propertyIndexConfigs") final Map<String, PropertyIndexConfigJson> propertyIndexConfigs )
    {
        super( analyzer, collection, decideFulltextByValueType, skip );
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
        final EntityPropertyIndexConfig.Builder builder = EntityPropertyIndexConfig.newEntityIndexConfig().
            analyzer( this.getAnalyzer() ).
            collection( this.getCollection() );

        for ( final String path : this.propertyIndexConfigs.keySet() )
        {
            final PropertyIndexConfigJson propertyIndexConfigJson = propertyIndexConfigs.get( path );

            builder.addPropertyIndexConfig( path, propertyIndexConfigJson.toPropertyIndexConfig() );
        }

        builder.collection( this.getCollection() );

        builder.analyzer( this.getAnalyzer() );

        builder.decideFulltextByValueType( this.isDecideFulltextByValueType() );

        builder.skip( this.isSkip() );

        return builder.build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<String, PropertyIndexConfigJson> getPropertyIndexConfigs()
    {
        return propertyIndexConfigs;
    }
}
