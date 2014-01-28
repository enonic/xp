package com.enonic.wem.api.schema.content.editor;

import com.enonic.wem.api.schema.SchemaIcon;

public final class ContentTypeEditors
{
    private ContentTypeEditors()
    {
    }

    public static ContentTypeEditor composite( final ContentTypeEditor... editors )
    {
        return new CompositeContentTypeEditor( editors );
    }

    public static ContentTypeEditor setIcon( final SchemaIcon icon )
    {
        return SetContentTypeEditor.newSetContentTypeEditor().icon( icon ).build();
    }
}
