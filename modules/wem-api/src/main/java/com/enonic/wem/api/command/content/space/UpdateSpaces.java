package com.enonic.wem.api.command.content.space;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.space.SpaceName;
import com.enonic.wem.api.content.space.SpaceNames;
import com.enonic.wem.api.content.space.editor.SpaceEditor;

public final class UpdateSpaces
    extends Command<Integer>
{
    private SpaceNames spaceNames;

    private SpaceEditor editor;


    public UpdateSpaces names( final SpaceNames spaceNames )
    {
        this.spaceNames = spaceNames;
        return this;
    }

    public UpdateSpaces name( final SpaceName spaceName )
    {
        this.spaceNames = SpaceNames.from( spaceName );
        return this;
    }

    public UpdateSpaces editor( final SpaceEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public SpaceNames getSpaceNames()
    {
        return spaceNames;
    }

    public SpaceEditor getEditor()
    {
        return editor;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof UpdateSpaces ) )
        {
            return false;
        }

        final UpdateSpaces that = (UpdateSpaces) o;
        return Objects.equal( this.spaceNames, that.spaceNames ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.spaceNames, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.spaceNames, "spaceNames cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
