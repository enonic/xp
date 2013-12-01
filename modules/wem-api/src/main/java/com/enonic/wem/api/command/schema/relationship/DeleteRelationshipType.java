package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class DeleteRelationshipType
    extends Command<DeleteRelationshipTypeResult>
{
    private RelationshipTypeName name;

    public RelationshipTypeName getName()
    {
        return this.name;
    }

    public DeleteRelationshipType name( final RelationshipTypeName name )
    {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteRelationshipType ) )
        {
            return false;
        }

        final DeleteRelationshipType that = (DeleteRelationshipType) o;
        return Objects.equal( this.name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "Relationship type name cannot be null" );
    }
}
