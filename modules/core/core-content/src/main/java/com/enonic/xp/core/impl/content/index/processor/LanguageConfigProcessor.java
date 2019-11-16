package com.enonic.xp.core.impl.content.index.processor;

import com.google.common.base.Strings;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

public class LanguageConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final String language;

    public LanguageConfigProcessor( final String language )
    {
        this.language = language;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        if ( !Strings.nullToEmpty( this.language ).isBlank() )
        {
            builder.addAllTextConfigLanguage( this.language ).build();
            builder.add( ContentPropertyNames.LANGUAGE, IndexConfig.NGRAM );
        }

        return builder;
    }
}
