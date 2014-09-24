package com.enonic.wem.core.entity;

import java.util.Set;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.entity.IndexConfigDocumentOldShit;
import com.enonic.wem.api.entity.PathIndexConfig;
import com.enonic.wem.api.entity.PatternIndexConfigDocumentOldShit;
import com.enonic.wem.core.entity.relationship.EntityIndexConfigJson;

public class EntityPatternIndexConfigJson
    extends EntityIndexConfigJson
{
    private Set<PathIndexConfigJson> configs;

    private PropertyIndexConfigJson defaultConfig;

    public EntityPatternIndexConfigJson( final PatternIndexConfigDocumentOldShit indexConfig )
    {
        super( indexConfig.getAnalyzer() );
        this.configs = translateToJson( indexConfig.getPathIndexConfigs() );
        this.defaultConfig = new PropertyIndexConfigJson( indexConfig.getDefaultConfig() );
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public EntityPatternIndexConfigJson( @JsonProperty("analyzer") final String analyzer,
                                         @JsonProperty("configs") final Set<PathIndexConfigJson> configs,  //
                                         @JsonProperty("defaultConfig") final PropertyIndexConfigJson defaultConfig )
    {
        super( analyzer );
        this.configs = configs;
        this.defaultConfig = defaultConfig;
    }

    private Set<PathIndexConfigJson> translateToJson( final SortedSet<PathIndexConfig> configs )
    {
        Set<PathIndexConfigJson> translatedSet = Sets.newHashSet();

        for ( final PathIndexConfig config : configs )
        {
            translatedSet.add( new PathIndexConfigJson( config ) );
        }

        return translatedSet;
    }

    @Override
    public IndexConfigDocumentOldShit toEntityIndexConfig()
    {
        final PatternIndexConfigDocumentOldShit.Builder builder = PatternIndexConfigDocumentOldShit.create();

        for ( final PathIndexConfigJson config : this.configs )
        {
            builder.addConfig( PathIndexConfig.
                create().
                propertyIndexConfig( config.getPropertyIndexConfigJson().toPropertyIndexConfig() ).path(
                DataPath.from( config.getDataPath() ) ).build() );
        }

        builder.defaultConfig( this.defaultConfig.toPropertyIndexConfig() ).
            analyzer( this.getAnalyzer() );

        return builder.build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<PathIndexConfigJson> getConfigs()
    {
        return configs;
    }

    @SuppressWarnings("UnusedDeclaration")
    public PropertyIndexConfigJson getDefaultConfig()
    {
        return defaultConfig;
    }
}
