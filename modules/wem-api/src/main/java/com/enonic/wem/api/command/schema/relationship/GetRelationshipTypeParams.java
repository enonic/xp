package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class GetRelationshipTypeParams
{
    private RelationshipTypeName name;

    private boolean notFoundAsException = false;

    public GetRelationshipTypeParams name( final RelationshipTypeName value )
    {
        this.name = value;
        return this;
    }

    public GetRelationshipTypeParams notFoundAsException()
    {
        notFoundAsException = true;
        return this;
    }

    public GetRelationshipTypeParams notFoundAsNull()
    {
        notFoundAsException = false;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetRelationshipTypeParams ) )
        {
            return false;
        }

        final GetRelationshipTypeParams that = (GetRelationshipTypeParams) o;
        return Objects.equal( this.name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
    }

    public RelationshipTypeName getName()
    {
        return this.name;
    }

    public boolean isNotFoundAsException()
    {
        return notFoundAsException;
    }
}
