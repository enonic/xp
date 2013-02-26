package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;

final class CompositeEditor
    implements ContentEditor
{
    private final ContentEditor[] editors;

    CompositeEditor( final ContentEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public Content edit( final Content toBeEdited )
        throws Exception
    {
        boolean modified = false;
        Content contentToBeEdited = toBeEdited;
        for ( final ContentEditor editor : this.editors )
        {
            final Content updatedContent = editor.edit( contentToBeEdited );
            if ( updatedContent != null )
            {
                contentToBeEdited = updatedContent;
                modified = true;
            }
        }
        return modified ? contentToBeEdited : null;
    }
}
