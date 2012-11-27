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
    public ContentType edit( final ContentType content )
        throws Exception
    {
        boolean modified = false;
        ContentType contentEdited = content;
        for ( final ContentTypeEditor editor : this.editors )
        {
            final ContentType updatedContent = editor.edit( contentEdited );
            if ( updatedContent != null )
            {
                contentEdited = updatedContent;
                modified = true;
            }
        }
        return modified ? contentEdited : null;
    }
}
