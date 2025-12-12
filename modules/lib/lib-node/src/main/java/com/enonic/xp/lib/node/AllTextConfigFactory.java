package com.enonic.xp.lib.node;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.index.AllTextIndexConfig;

import static com.enonic.xp.lib.node.NodePropertyConstants.ALL_TEXT_CONFIG;

public class AllTextConfigFactory
{
    private static final boolean DEFAULT_ENABLED = true;

    private static final boolean DEFAULT_NGRAM = true;

    private static final boolean DEFAULT_FULLTEXT = false;

    private final PropertySet propertySet;

    public AllTextConfigFactory( final PropertySet propertySet )
    {
        this.propertySet = propertySet;
    }

    public AllTextIndexConfig create()
    {
        if ( this.propertySet == null )
        {
            return createDefault();
        }

        final PropertySet allTextConfig = this.propertySet.getSet( ALL_TEXT_CONFIG );

        if ( allTextConfig == null )
        {
            return createDefault();
        }

        final AllTextIndexConfig.Builder builder = AllTextIndexConfig.create();

        final Boolean enabled = allTextConfig.getBoolean( "enabled" );
        final Boolean nGram = allTextConfig.getBoolean( "nGram" );
        final Boolean fulltext = allTextConfig.getBoolean( "fulltext" );
        final Iterable<String> languages = allTextConfig.getStrings( "languages" );

        if ( enabled != null )
        {
            builder.enabled( enabled );
        }
        else
        {
            builder.enabled( DEFAULT_ENABLED );
        }

        if ( nGram != null )
        {
            builder.nGram( nGram );
        }
        else
        {
            builder.nGram( DEFAULT_NGRAM );
        }

        if ( fulltext != null )
        {
            builder.fulltext( fulltext );
        }
        else
        {
            builder.fulltext( DEFAULT_FULLTEXT );
        }

        for ( final String language : languages )
        {
            builder.addLanguage( language );
        }

        return builder.build();
    }

    private AllTextIndexConfig createDefault()
    {
        return AllTextIndexConfig.create()
            .enabled( DEFAULT_ENABLED )
            .nGram( DEFAULT_NGRAM )
            .fulltext( DEFAULT_FULLTEXT )
            .build();
    }
}
