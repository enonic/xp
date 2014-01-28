package com.enonic.wem.api.command.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class RelationshipTypeGetCommands
{
    public GetAllRelationshipTypes all()
    {
        return new GetAllRelationshipTypes();
    }

    public GetRelationshipType byName( final RelationshipTypeName name )
    {
        return new GetRelationshipType().name( name );
    }
}
