package com.enonic.wem.api.command.content.relationship;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipIds;

public class DeleteRelationships
    extends Command<DeleteRelationshipsResult>
{
    private RelationshipIds relationshipIds;

    public DeleteRelationships relationshipIds( final RelationshipIds value )
    {
        this.relationshipIds = value;
        return this;
    }

    public RelationshipIds getRelationshipIds()
    {
        return relationshipIds;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( relationshipIds, "relationshipIds cannot be null" );
        Preconditions.checkArgument( relationshipIds.isNotEmpty(), "relationshipIds cannot be empty" );
    }
}
