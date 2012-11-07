package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.ContentType;

public abstract class ContentTypeEditors
{
    public static ContentTypeEditor composite( final ContentTypeEditor... editors )
    {
        return new CompositeEditor( editors );
    }

    public static ContentTypeEditor setContentType( final ContentType contentType )
    {
        return new SetContentTypeEditor( contentType );
    }

}
