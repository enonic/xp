package com.enonic.wem.api.command.content.relationship;

public final class RelationshipCommands
{
    public CreateRelationship create()
    {
        return new CreateRelationship();
    }

    public UpdateRelationships update()
    {
        return new UpdateRelationships();
    }

    public DeleteRelationships delete()
    {
        return new DeleteRelationships();
    }

    /*public GetRelationshipTypes get()
    {
        return new GetRelationshipTypes();
    }*/
}
