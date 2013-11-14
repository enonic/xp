package com.enonic.wem.api.space.editor;

import com.enonic.wem.api.space.Space;

final class CompositeSpaceEditor
    implements SpaceEditor
{
    private final SpaceEditor[] editors;

    CompositeSpaceEditor( final SpaceEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public Space edit( final Space space )
    {
        boolean modified = false;
        Space spaceEdited = space;
        for ( final SpaceEditor editor : this.editors )
        {
            final Space updatedSpace = editor.edit( spaceEdited );
            if ( updatedSpace != null )
            {
                spaceEdited = updatedSpace;
                modified = true;
            }
        }
        return modified ? spaceEdited : null;
    }
}
