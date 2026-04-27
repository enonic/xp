package com.enonic.xp.content;

import com.enonic.xp.security.PrincipalKey;


public final class EditableContentMetadata
{
    public final Content source;

    public PrincipalKey owner;

    public ContentId variantOf;

    public EditableContentMetadata( final Content source )
    {
        this.source = source;
        this.owner = source.getOwner();
        this.variantOf = source.getVariantOf();
    }

    public Content build()
    {
        return Content.create( this.source ).owner( owner ).variantOf( variantOf ).build();
    }
}
