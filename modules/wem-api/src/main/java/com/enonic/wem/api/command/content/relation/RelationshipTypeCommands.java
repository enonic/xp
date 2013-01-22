package com.enonic.wem.api.command.content.relation;

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

    public UpdateRelationshipTypes update()
    {
        return new UpdateRelationshipTypes();
    }

    public DeleteRelationshipTypes delete()
    {
        return new DeleteRelationshipTypes();
    }
}
