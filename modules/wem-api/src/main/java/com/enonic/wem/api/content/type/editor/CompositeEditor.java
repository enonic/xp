package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.ContentType;

final class CompositeEditor
    implements ContentTypeEditor
{
    protected final ContentTypeEditor[] editors;

    public CompositeEditor( final ContentTypeEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public boolean edit( final ContentType content )
        throws Exception
    {
        boolean modified = false;
        for ( final ContentTypeEditor editor : this.editors )
        {
            final boolean result = editor.edit( content );
            modified = modified || result;
        }

        return modified;
    }
}
