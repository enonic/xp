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
    public boolean edit( final ContentType contentType )
        throws Exception
    {
        return edit( this.source, contentType );
    }

    private static boolean edit( final ContentType source, final ContentType target )
        throws Exception
    {
        target.setDisplayName( source.getDisplayName() );
        // TODO ...
        return true;
    }
}
