package com.enonic.wem.api.command.content.relation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.RelationshipType;

public final class CreateRelationshipType
    extends Command<QualifiedRelationshipTypeName>
{
    private RelationshipType relationshipType;

    public CreateRelationshipType relationshipType( final RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
        return this;
    }

    public RelationshipType getRelationshipType()
    {
        return relationshipType;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateRelationshipType ) )
        {
            return false;
        }

        final CreateRelationshipType that = (CreateRelationshipType) o;
        return Objects.equal( this.relationshipType, that.relationshipType );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.relationshipType );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.relationshipType, "relationshipType cannot be null" );
    }
}
