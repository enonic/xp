package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.RootDataSet;

import static com.enonic.wem.api.content.Content.newContent;

final class SetContentDataEditor
    implements ContentEditor
{
    protected final RootDataSet source;

    SetContentDataEditor( final RootDataSet source )
    {
        this.source = source;
    }

    @Override
    public Content edit( final Content content )
        throws Exception
    {
        return edit( this.source, content );
    }

    private Content edit( final RootDataSet source, final Content target )
        throws Exception
    {
        final Content updated = newContent( target ).
            rootDataSet( source ).
            build();
        return updated;
    }

}
