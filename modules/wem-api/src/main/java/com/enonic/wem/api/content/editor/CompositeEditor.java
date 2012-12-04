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
    public Content edit( final Content content )
        throws Exception
    {
        boolean modified = false;
        Content contentEdited = content;
        for ( final ContentEditor editor : this.editors )
        {
            final Content updatedContent = editor.edit( contentEdited );
            if ( updatedContent != null )
            {
                contentEdited = updatedContent;
                modified = true;
            }
        }
        return modified ? contentEdited : null;
    }
}
