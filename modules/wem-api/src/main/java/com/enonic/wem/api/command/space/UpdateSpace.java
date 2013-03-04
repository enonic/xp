package com.enonic.wem.api.command.space;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.editor.SpaceEditor;

public final class UpdateSpace
    extends Command<Boolean>
{
    private SpaceName spaceName;

    private SpaceEditor editor;


    public UpdateSpace name( final SpaceName spaceName )
    {
        this.spaceName = spaceName;
        return this;
    }

    public UpdateSpace editor( final SpaceEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public SpaceName getSpaceName()
    {
        return spaceName;
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

        if ( !( o instanceof UpdateSpace ) )
        {
            return false;
        }

        final UpdateSpace that = (UpdateSpace) o;
        return Objects.equal( this.spaceName, that.spaceName ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.spaceName, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.spaceName, "spaceName cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
