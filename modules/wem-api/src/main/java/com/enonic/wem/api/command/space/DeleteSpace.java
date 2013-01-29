package com.enonic.wem.api.command.space;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.space.SpaceName;

public final class DeleteSpace
    extends Command<Boolean>
{
    private SpaceName spaceName;

    public SpaceName getName()
    {
        return this.spaceName;
    }

    public DeleteSpace name( final SpaceName spaceName )
    {
        this.spaceName = spaceName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteSpace ) )
        {
            return false;
        }

        final DeleteSpace that = (DeleteSpace) o;
        return Objects.equal( this.spaceName, that.spaceName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.spaceName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.spaceName, "spaceName cannot be null" );
    }
}
