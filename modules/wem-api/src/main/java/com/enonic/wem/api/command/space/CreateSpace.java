package com.enonic.wem.api.command.space;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;

public final class CreateSpace
    extends Command<Space>
{
    private String displayName;

    private SpaceName spaceName;

    private Icon icon;

    public CreateSpace name( final SpaceName spaceName )
    {
        this.spaceName = spaceName;
        return this;
    }

    public CreateSpace name( final String name )
    {
        return name( SpaceName.from( name ) );
    }

    public CreateSpace displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateSpace icon( final Icon icon )
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
        return Objects.equal( this.displayName, that.displayName ) && Objects.equal( this.spaceName, that.spaceName ) &&
            Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.displayName, this.spaceName, this.icon );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.spaceName, "space name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "space displayName cannot be null" );
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public SpaceName getName()
    {
        return spaceName;
    }

    public Icon getIcon()
    {
        return icon;
    }
}
