package com.enonic.wem.api.command.content.relationship;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.editor.RelationshipEditor;

public class UpdateRelationships
    extends Command<UpdateRelationshipsResult>
{
    private RelationshipIds relationshipIds;

    private RelationshipEditor editor;

    public UpdateRelationships relationshipIds( final RelationshipIds value )
    {
        this.relationshipIds = value;
        return this;
    }

    public RelationshipIds getRelationshipIds()
    {
        return relationshipIds;
    }

    public void editor( final RelationshipEditor relationshipEditor )
    {
        this.editor = relationshipEditor;
    }

    public RelationshipEditor getEditor()
    {
        return editor;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( relationshipIds, "relationshipIds cannot be null" );
        Preconditions.checkArgument( relationshipIds.isNotEmpty(), "relationshipIds cannot be empty" );
    }
}
