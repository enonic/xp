package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypeDeletionResult;

public final class DeleteSubTypes
    extends Command<SubTypeDeletionResult>
{
    private QualifiedSubTypeNames qualifiedSubTypeNames;

    public QualifiedSubTypeNames getNames()
    {
        return this.qualifiedSubTypeNames;
    }

    public DeleteSubTypes names( final QualifiedSubTypeNames qualifiedSubTypeNames )
    {
        this.qualifiedSubTypeNames = qualifiedSubTypeNames;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteSubTypes ) )
        {
            return false;
        }

        final DeleteSubTypes that = (DeleteSubTypes) o;
        return Objects.equal( this.qualifiedSubTypeNames, that.qualifiedSubTypeNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedSubTypeNames );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedSubTypeNames, "qualifiedSubTypeNames cannot be null" );
    }
}
