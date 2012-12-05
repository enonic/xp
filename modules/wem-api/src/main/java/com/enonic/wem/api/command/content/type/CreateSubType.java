package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;

public final class CreateSubType
    extends Command<QualifiedSubTypeName>
{
    private SubType subType;

    public CreateSubType subType( final SubType subType )
    {
        this.subType = subType;
        return this;
    }

    public SubType getSubType()
    {
        return subType;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateSubType ) )
        {
            return false;
        }

        final CreateSubType that = (CreateSubType) o;
        return Objects.equal( this.subType, that.subType );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.subType );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.subType, "subType cannot be null" );
    }
}
