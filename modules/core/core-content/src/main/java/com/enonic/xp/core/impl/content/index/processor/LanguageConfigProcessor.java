package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.google.common.base.Strings.nullToEmpty;

public class LanguageConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final String language;

    public LanguageConfigProcessor( final String language )
    {
        this.language = language;
    }

    @Override
    public PatternIndexConfigDocument processDocument( final PatternIndexConfigDocument config )
    {
        final PatternIndexConfigDocument.Builder configBuilder = PatternIndexConfigDocument.create( config );

        final AllTextIndexConfig.Builder allTextBuilder = AllTextIndexConfig.create( config.getAllTextConfig() );

        if ( !nullToEmpty( this.language ).isBlank() )
        {
            configBuilder.allTextConfig( allTextBuilder.addLanguage( this.language ).build() );
            configBuilder.add( ContentPropertyNames.LANGUAGE, IndexConfig.NGRAM );
        }

        return configBuilder.build();
    }
}
