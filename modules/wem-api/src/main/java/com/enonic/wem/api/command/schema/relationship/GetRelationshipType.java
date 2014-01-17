package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class GetRelationshipType
    extends Command<RelationshipType>
{
    private RelationshipTypeName name;

    private boolean notFoundAsException = false;

    public GetRelationshipType name( final RelationshipTypeName value )
    {
        this.name = value;
        return this;
    }

    public GetRelationshipType notFoundAsException()
    {
        notFoundAsException = true;
        return this;
    }

    public GetRelationshipType notFoundAsNull()
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

        if ( !( o instanceof GetRelationshipType ) )
        {
            return false;
        }

        final GetRelationshipType that = (GetRelationshipType) o;
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
