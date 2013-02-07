package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.type.ContentType;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

final class SetContentTypeIconEditor
    implements ContentTypeEditor
{
    private final Icon icon;

    SetContentTypeIconEditor( final Icon icon )
    {
        this.icon = icon;
    }

    @Override
    public ContentType edit( final ContentType contentType )
        throws Exception
    {
        final Icon iconToSet = ( this.icon == null ) ? null : Icon.copyOf( this.icon );
        final ContentType updated = newContentType( contentType ).
            icon( iconToSet ).
            build();
        return updated;
    }
}
