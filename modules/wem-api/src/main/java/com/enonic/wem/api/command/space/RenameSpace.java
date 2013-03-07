package com.enonic.wem.api.command.space;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.space.SpaceName;

public final class RenameSpace
    extends Command<Boolean>
{
    private SpaceName targetSpace;

    private String newName;

    public RenameSpace()
    {
    }

    public RenameSpace space( final SpaceName spaceName )
    {
        this.targetSpace = spaceName;
        return this;
    }

    public RenameSpace newName( final String newName )
    {
        this.newName = newName;
        return this;
    }

    public SpaceName getSpace()
    {
        return targetSpace;
    }

    public String getNewName()
    {
        return newName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof RenameSpace ) )
        {
            return false;
        }

        final RenameSpace that = (RenameSpace) o;
        return Objects.equal( this.targetSpace, that.targetSpace ) && Objects.equal( this.newName, that.newName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.targetSpace, this.newName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.targetSpace, "spaceName cannot be null" );
        Preconditions.checkNotNull( this.newName, "newName cannot be null" );
    }
}
