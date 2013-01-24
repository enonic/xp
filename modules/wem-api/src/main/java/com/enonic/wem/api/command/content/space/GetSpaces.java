package com.enonic.wem.api.command.content.space;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.space.SpaceName;
import com.enonic.wem.api.content.space.SpaceNames;
import com.enonic.wem.api.content.space.Spaces;

public final class GetSpaces
    extends Command<Spaces>
{
    private boolean getAllSpaces = false;

    private SpaceNames spaceNames;

    public GetSpaces name( final SpaceName spaceName )
    {
        this.spaceNames = SpaceNames.from( spaceName );
        return this;
    }

    public GetSpaces names( final SpaceNames spaceNames )
    {
        this.spaceNames = spaceNames;
        return this;
    }

    public GetSpaces all()
    {
        getAllSpaces = true;
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

        final GetSpaces that = (GetSpaces) o;
        return Objects.equal( this.spaceNames, that.spaceNames ) && ( this.getAllSpaces == that.getAllSpaces );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.spaceNames, this.getAllSpaces );
    }

    @Override
    public void validate()
    {
        if ( getAllSpaces )
        {
            Preconditions.checkArgument( this.spaceNames == null, "Cannot specify both get all and get spaceNames" );
        }
        else
        {
            Preconditions.checkNotNull( this.spaceNames, "spaceNames cannot be null" );
        }
    }

    public SpaceNames getSpaceNames()
    {
        return spaceNames;
    }

    public boolean isGetAll()
    {
        return getAllSpaces;
    }

}
