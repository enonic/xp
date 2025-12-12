package com.enonic.xp.lib.node.mapper;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

import static com.enonic.xp.lib.node.NodePropertyConstants.ALL_TEXT_CONFIG;
import static com.enonic.xp.lib.node.NodePropertyConstants.ANALYZER;
import static com.enonic.xp.lib.node.NodePropertyConstants.CONFIG_ARRAY;
import static com.enonic.xp.lib.node.NodePropertyConstants.CONFIG_PATH;
import static com.enonic.xp.lib.node.NodePropertyConstants.CONFIG_SETTINGS;
import static com.enonic.xp.lib.node.NodePropertyConstants.DEFAULT_CONFIG;

class IndexConfigDocMapper
    implements MapSerializable
{
    private final IndexConfigDocument value;

    IndexConfigDocMapper( final IndexConfigDocument value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final IndexConfigDocument document )
    {
        gen.value( ANALYZER, value.getAnalyzer() );

        if ( document instanceof PatternIndexConfigDocument )
        {
            serialize( gen, (PatternIndexConfigDocument) document );
        }
    }

    private void serialize( final MapGenerator gen, final PatternIndexConfigDocument document )
    {
        gen.map( DEFAULT_CONFIG );
        serialize( gen, document.getDefaultConfig() );
        gen.end();

        gen.array( CONFIG_ARRAY );

        final SortedSet<PathIndexConfig> pathIndexConfigs = document.getPathIndexConfigs();

        for ( final PathIndexConfig pathIndexConfig : pathIndexConfigs )
        {
            gen.map();
            gen.value( CONFIG_PATH, pathIndexConfig.getPath().toString() );
            gen.map( CONFIG_SETTINGS );
            serialize( gen, pathIndexConfig.getIndexConfig() );
            gen.end();
            gen.end();
        }

        gen.end();

        gen.map( ALL_TEXT_CONFIG );
        serialize( gen, document.getAllTextConfig() );
        gen.end();
    }

    private void serialize( final MapGenerator gen, final IndexConfig indexConfig )
    {
        gen.value( "decideByType", indexConfig.isDecideByType() );
        gen.value( "enabled", indexConfig.isEnabled() );
        gen.value( "nGram", indexConfig.isnGram() );
        gen.value( "fulltext", indexConfig.isFulltext() );
        gen.value( "includeInAllText", indexConfig.isIncludeInAllText() );
        gen.value( "path", indexConfig.isPath() );

        final List<IndexValueProcessor> indexValueProcessors = indexConfig.getIndexValueProcessors();

        serializeArray( gen, "indexValueProcessors", indexValueProcessors.
            stream().
            map( IndexValueProcessor::getName ).
            collect( Collectors.toList() ) );

        final List<String> languages = indexConfig.getLanguages();

        serializeArray( gen, "languages", languages );
    }

    private void serializeArray( final MapGenerator gen, final String name, final List<String> values )
    {
        gen.array( name );
        for ( final String value : values )
        {
            gen.value( value );
        }
        gen.end();
    }

    private void serialize( final MapGenerator gen, final AllTextIndexConfig allTextConfig )
    {
        gen.value( "enabled", allTextConfig.isEnabled() );
        gen.value( "nGram", allTextConfig.isnGram() );
        gen.value( "fulltext", allTextConfig.isFulltext() );

        final List<String> languages = allTextConfig.getLanguages();
        serializeArray( gen, "languages", languages );
    }
}
