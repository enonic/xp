package com.enonic.xp.content;

import java.util.Locale;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class EditableContentMetadata
{
    public final Content source;

    public PrincipalKey owner;

    public Locale language;

    public ContentId variantOf;

    public EditableContentMetadata( final Content source )
    {
        this.source = source;
        this.owner = source.getOwner();
        this.language = source.getLanguage();
        this.variantOf = source.getVariantOf();
    }

    public Content build()
    {
        return Content.create( this.source )
            .owner( owner )
            .language( language )
            .variantOf( variantOf )
            .build();
    }
}
