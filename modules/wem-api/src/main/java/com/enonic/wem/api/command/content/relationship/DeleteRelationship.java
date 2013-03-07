package com.enonic.wem.api.command.content.relationship;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipKey;

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
}
