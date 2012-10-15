package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;

final class SetContentDataEditor
    implements ContentEditor
{
    protected final ContentData source;

    SetContentDataEditor( final ContentData source )
    {
        this.source = source;
    }

    @Override
    public boolean edit( final Content content )
        throws Exception
    {
        return edit( this.source, content );
    }

    private static boolean edit( final ContentData source, final Content target )
        throws Exception
    {
        target.setData( source );
        return true;
    }

}
