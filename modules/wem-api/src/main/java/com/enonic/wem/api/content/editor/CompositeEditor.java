package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;

final class CompositeEditor
    implements ContentEditor
{
    protected final ContentEditor[] editors;

    public CompositeEditor( final ContentEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public boolean edit( final Content content )
        throws Exception
    {
        boolean flag = false;
        for ( final ContentEditor editor : this.editors )
        {
            final boolean result = editor.edit( content );
            flag = flag || result;
        }

        return flag;
    }
}
