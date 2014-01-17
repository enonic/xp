package com.enonic.wem.api.command.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;

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

    public GetRelationshipTypes byNames( final RelationshipTypeNames names )
    {
        return new GetRelationshipTypes().names( names );
    }
}
