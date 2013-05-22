package com.enonic.wem.api.command.relationship;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.editor.RelationshipEditor;

public class UpdateRelationship
    extends Command
{
    private RelationshipKey relationshipKey;

    private RelationshipEditor editor;

    public UpdateRelationship relationshipKey( final RelationshipKey value )
    {
        this.relationshipKey = value;
        return this;
    }

    public RelationshipKey getRelationshipKey()
    {
        return relationshipKey;
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
        Preconditions.checkNotNull( relationshipKey, "relationshipKey cannot be null" );
        Preconditions.checkArgument( relationshipKey.getManagingData() != null, "Managed Relationship's cannot be updated directly" );
    }
}
