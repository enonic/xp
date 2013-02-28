package com.enonic.wem.api.content.schema.content.editor;

import com.enonic.wem.api.Icon;

public final class ContentTypeEditors
{
    private ContentTypeEditors()
    {
    }

    public static ContentTypeEditor composite( final ContentTypeEditor... editors )
    {
        return new CompositeContentTypeEditor( editors );
    }

    public static ContentTypeEditor setIcon( final Icon icon )
    {
        return SetContentTypeEditor.newSetContentTypeEditor().icon( icon ).build();
    }
}
