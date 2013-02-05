package com.enonic.wem.api.command.content.relationship.editor;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;

final class CompositeRelationshipTypeEditor
    implements RelationshipTypeEditor
{
    private final RelationshipTypeEditor[] editors;

    CompositeRelationshipTypeEditor( final RelationshipTypeEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public RelationshipType edit( final RelationshipType relationshipType )
        throws Exception
    {
        boolean modified = false;
        RelationshipType relationshipTypeEdited = relationshipType;
        for ( final RelationshipTypeEditor editor : this.editors )
        {
            final RelationshipType updatedRelationshipType = editor.edit( relationshipTypeEdited );
            if ( updatedRelationshipType != null )
            {
                relationshipTypeEdited = updatedRelationshipType;
                modified = true;
            }
        }
        return modified ? relationshipTypeEdited : null;
    }
}
