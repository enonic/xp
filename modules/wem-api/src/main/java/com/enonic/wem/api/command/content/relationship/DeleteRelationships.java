package com.enonic.wem.api.command.content.relationship;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipKeys;

public class DeleteRelationships
    extends Command<DeleteRelationshipsResult>
{
    private RelationshipKeys relationshipKeys;

    public DeleteRelationships relationships( final RelationshipKeys value )
    {
        this.relationshipKeys = value;
        return this;
    }

    public RelationshipKeys getRelationshipKeys()
    {
        return relationshipKeys;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( relationshipKeys, "relationshipKeys cannot be null" );
        Preconditions.checkArgument( relationshipKeys.isNotEmpty(), "relationshipKeys cannot be empty" );
    }
}
