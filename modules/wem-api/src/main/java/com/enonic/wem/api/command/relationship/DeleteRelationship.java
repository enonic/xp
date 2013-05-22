package com.enonic.wem.api.command.relationship;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.relationship.DeleteRelationshipResult;
import com.enonic.wem.api.relationship.RelationshipKey;

public class DeleteRelationship
    extends Command<DeleteRelationshipResult>
{
    private RelationshipKey relationshipKey;

    public DeleteRelationship relationship( final RelationshipKey value )
    {
        this.relationshipKey = value;
        return this;
    }

    public RelationshipKey getRelationshipKey()
    {
        return relationshipKey;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( relationshipKey, "relationshipKey cannot be null" );

        final boolean isManaged = relationshipKey.getManagingData() != null;
        if ( isManaged )
        {
            throw new IllegalArgumentException( "A managed Relationship [" + relationshipKey + "] cannot be deleted directly" );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final DeleteRelationship that = (DeleteRelationship) o;

        return Objects.equals( relationshipKey, that.relationshipKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( relationshipKey );
    }
}
