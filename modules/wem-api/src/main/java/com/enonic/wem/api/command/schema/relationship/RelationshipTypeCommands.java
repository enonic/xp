package com.enonic.wem.api.command.schema.relationship;

public final class RelationshipTypeCommands
{
    public RelationshipTypesExists exists()
    {
        return new RelationshipTypesExists();
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

    public RelationshipTypeGetCommands get()
    {
        return new RelationshipTypeGetCommands();
    }
}
