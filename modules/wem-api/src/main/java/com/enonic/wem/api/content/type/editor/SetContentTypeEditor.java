package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.ContentType;

final class SetContentTypeEditor
    implements ContentTypeEditor
{
    protected final ContentType source;

    public SetContentTypeEditor( final ContentType source )
    {
        this.source = source;
    }

    @Override
    public ContentType edit( final ContentType contentType )
        throws Exception
    {
        final ContentType updated = ContentType.newContentType( contentType ).
            displayName( source.getDisplayName() )
                // TODO update other fields
            .build();
        return updated;
    }
}
