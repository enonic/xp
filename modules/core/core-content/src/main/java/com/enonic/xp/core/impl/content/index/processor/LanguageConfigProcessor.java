package com.enonic.xp.core.impl.content.index.processor;

import java.util.Locale;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

public class LanguageConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final Locale language;

    public LanguageConfigProcessor( final Locale language )
    {
        this.language = language;
    }

    @Override
    public PatternIndexConfigDocument processDocument( final PatternIndexConfigDocument config )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create( config );

        if ( this.language != null )
        {
            builder.allTextConfig( AllTextIndexConfig.create( config.getAllTextConfig() ).addLanguage( this.language ).build() );
            builder.add( ContentPropertyNames.LANGUAGE, IndexConfig.NGRAM );
            builder.add( ContentPropertyNames.DISPLAY_NAME,
                         IndexConfig.create( IndexConfig.FULLTEXT ).addLanguage( this.language ).build() );
        }

        return builder.build();
    }
}
