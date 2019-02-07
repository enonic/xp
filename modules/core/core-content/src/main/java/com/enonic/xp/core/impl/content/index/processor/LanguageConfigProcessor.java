package com.enonic.xp.core.impl.content.index.processor;

import org.apache.commons.lang.StringUtils;

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
        if ( StringUtils.isNotBlank( this.language ) )
        {
            builder.addAllTextConfigLanguage( this.language ).build();
        }

        return builder;
    }
}
