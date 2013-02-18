package com.enonic.wem.api.command.content.relationshiptype;

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

    public CreateRelationshipType create()
    {
        return new CreateRelationshipType();
    }

    public UpdateRelationshipType update()
    {
        return new UpdateRelationshipType();
    }

    public DeleteRelationshipTypes delete()
    {
        return new DeleteRelationshipTypes();
    }
}
