package com.enonic.wem.api.content.schema.content.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.schema.content.ContentType;

public final class ContentTypeEditors
{
    private ContentTypeEditors()
    {
    }

    public static ContentTypeEditor composite( final ContentTypeEditor... editors )
    {
        return new CompositeContentTypeEditor( editors );
    }

    public static ContentTypeEditor setContentType( final ContentType contentType )
    {
        return SetContentTypeEditor.newSetContentTypeEditor( contentType ).build();
    }

    public static ContentTypeEditor setIcon( final Icon icon )
    {
        return SetContentTypeEditor.newSetContentTypeEditor().icon( icon ).build();
    }
}
