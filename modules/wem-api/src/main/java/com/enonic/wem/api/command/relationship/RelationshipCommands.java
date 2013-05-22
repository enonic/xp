package com.enonic.wem.api.command.relationship;

public final class RelationshipCommands
{
    public CreateRelationship create()
    {
        return new CreateRelationship();
    }

    public UpdateRelationship update()
    {
        return new UpdateRelationship();
    }

    public DeleteRelationship delete()
    {
        return new DeleteRelationship();
    }

    public GetRelationships get()
    {
        return new GetRelationships();
    }

}
