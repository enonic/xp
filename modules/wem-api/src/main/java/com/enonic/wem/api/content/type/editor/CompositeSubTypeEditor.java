package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.SubType;

final class CompositeSubTypeEditor
    implements SubTypeEditor
{
    protected final SubTypeEditor[] editors;

    public CompositeSubTypeEditor( final SubTypeEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public SubType edit( final SubType subType )
        throws Exception
    {
        boolean modified = false;
        SubType subTypeEditet = subType;
        for ( final SubTypeEditor editor : this.editors )
        {
            final SubType updatedContent = editor.edit( subTypeEditet );
            if ( updatedContent != null )
            {
                subTypeEditet = updatedContent;
                modified = true;
            }
        }
        return modified ? subTypeEditet : null;
    }
}
