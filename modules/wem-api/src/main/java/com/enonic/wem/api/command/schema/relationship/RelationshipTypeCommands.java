package com.enonic.wem.api.command.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class RelationshipTypeCommands
{
    public RelationshipTypesExists exists()
    {
        return new RelationshipTypesExists();
    }

    public GetRelationshipTypes get()
    {
        return new GetRelationshipTypes();
    }

    public GetRelationshipType byName( final RelationshipTypeName name )
    {
        return new GetRelationshipType().name( name );
    }

    public CreateRelationshipType create()
    {
        return new CreateRelationshipType();
    }

    public UpdateRelationshipType update()
    {
        return new UpdateRelationshipType();
    }

    public DeleteRelationshipType delete()
    {
        return new DeleteRelationshipType();
    }
}
