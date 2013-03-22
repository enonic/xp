package com.enonic.wem.api.command.content.binary;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;

public final class CreateBinary
    extends Command<BinaryId>
{
    private Binary binary;

    public CreateBinary binary( final Binary binary )
    {
        this.binary = binary;
        return this;
    }

    public Binary getBinary()
    {
        return binary;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateBinary ) )
        {
            return false;
        }

        final CreateBinary that = (CreateBinary) o;
        return Objects.equal( this.binary, that.binary );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.binary );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.binary, "binary cannot be null" );
    }

}
