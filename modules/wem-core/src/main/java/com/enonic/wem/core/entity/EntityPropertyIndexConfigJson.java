package com.enonic.wem.core.entity;


import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.entity.IndexConfigDocumentOldShit;
import com.enonic.wem.api.entity.PorpertyIndexConfigDocumentOldShit;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.entity.relationship.EntityIndexConfigJson;

public class EntityPropertyIndexConfigJson
    extends EntityIndexConfigJson
{
    private final Map<String, PropertyIndexConfigJson> propertyIndexConfigs;

    public EntityPropertyIndexConfigJson( final PorpertyIndexConfigDocumentOldShit indexConfig )
    {
        super( indexConfig.getAnalyzer() );
        this.propertyIndexConfigs = translateMap( indexConfig.getPropertyIndexConfigs() );
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public EntityPropertyIndexConfigJson( @JsonProperty("analyzer") final String analyzer, @JsonProperty(
        "propertyIndexConfigs") final Map<String, PropertyIndexConfigJson> propertyIndexConfigs )
    {
        super( analyzer );
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

    public IndexConfigDocumentOldShit toEntityIndexConfig()
    {
        final PorpertyIndexConfigDocumentOldShit.Builder builder = PorpertyIndexConfigDocumentOldShit.create().
            analyzer( this.getAnalyzer() );

        for ( final String path : this.propertyIndexConfigs.keySet() )
        {
            final PropertyIndexConfigJson propertyIndexConfigJson = propertyIndexConfigs.get( path );

            builder.addPropertyIndexConfig( path, propertyIndexConfigJson.toPropertyIndexConfig() );
        }

        return builder.build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<String, PropertyIndexConfigJson> getPropertyIndexConfigs()
    {
        return propertyIndexConfigs;
    }
}
