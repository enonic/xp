package com.enonic.wem.api.command.space;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.space.Space;

public final class CreateSpace
    extends Command<Space>
{
    private String displayName;

    private String name;

    public CreateSpace name( final String name )
    {
        this.name = name;
        return this;
    }

    public CreateSpace displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetSpaces ) )
        {
            return false;
        }

        final CreateSpace that = (CreateSpace) o;
        return Objects.equal( this.displayName, that.displayName ) && ( this.name == that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.displayName, this.name );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "space name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "space displayName cannot be null" );
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getName()
    {
        return name;
    }
}
