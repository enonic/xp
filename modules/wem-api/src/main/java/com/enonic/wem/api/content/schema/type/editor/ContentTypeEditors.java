package com.enonic.wem.api.content.schema.type.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.schema.type.ContentType;

public abstract class ContentTypeEditors
{
    public static ContentTypeEditor composite( final ContentTypeEditor... editors )
    {
        return new CompositeContentTypeEditor( editors );
    }

    public static ContentTypeEditor setContentType( final ContentType contentType )
    {
        return new SetContentTypeEditor( contentType );
    }

    public static ContentTypeEditor setIcon( final Icon icon )
    {
        return new SetContentTypeIconEditor( icon );
    }

    public static ContentTypeEditor removeIcon()
    {
        return new SetContentTypeIconEditor( null );
    }
}
