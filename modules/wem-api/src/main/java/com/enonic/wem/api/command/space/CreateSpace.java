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

    private byte[] icon;

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

    public CreateSpace icon( final byte[] icon )
    {
        this.icon = icon;
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
        return Objects.equal( this.displayName, that.displayName ) && Objects.equal( this.name, that.name ) &&
            Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.displayName, this.name, this.icon );
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

    public byte[] getIcon()
    {
        return icon;
    }
}
