package com.enonic.wem.api.command.content.binary;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.binary.BinaryId;

public final class DeleteBinary
    extends Command<DeleteBinaryResult>
{
    private BinaryId binaryId;


    public BinaryId getBinaryId()
    {
        return binaryId;
    }

    public DeleteBinary binaryId( final BinaryId binaryId )
    {
        this.binaryId = binaryId;
        return this;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteBinary ) )
        {
            return false;
        }

        final DeleteBinary that = (DeleteBinary) o;
        return Objects.equal( this.binaryId, that.binaryId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.binaryId );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.binaryId, "binary id cannot be null" );
    }
}
