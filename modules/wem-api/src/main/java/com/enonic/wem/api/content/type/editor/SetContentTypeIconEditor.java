package com.enonic.wem.api.content.type.editor;

import java.util.Arrays;

import com.enonic.wem.api.content.type.ContentType;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

final class SetContentTypeIconEditor
    implements ContentTypeEditor
{
    protected final byte[] icon;

    public SetContentTypeIconEditor( final byte[] icon )
    {
        this.icon = icon;
    }

    @Override
    public ContentType edit( final ContentType contentType )
        throws Exception
    {
        final byte[] iconToSet = ( this.icon == null ) ? null : Arrays.copyOf( this.icon, this.icon.length );
        final ContentType updated = newContentType( contentType ).
            icon( iconToSet ).
            build();
        return updated;
    }
}
