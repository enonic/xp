package com.enonic.xp.relationship;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.relationship.editor.RelationshipEditor;

@Beta
public final class UpdateRelationshipParams
{
    private RelationshipKey relationshipKey;

    private RelationshipEditor editor;

    public UpdateRelationshipParams relationshipKey( final RelationshipKey value )
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

    public void validate()
    {
        Preconditions.checkNotNull( relationshipKey, "relationshipKey cannot be null" );
        Preconditions.checkArgument( relationshipKey.getManagingData() != null, "Managed Relationship's cannot be updated directly" );
    }
}
