package com.enonic.wem.api.content.relationshiptype.editor;

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
        RelationshipType edited = relationshipType;
        for ( final RelationshipTypeEditor editor : this.editors )
        {
            final RelationshipType updated = editor.edit( edited );
            if ( updated != null )
            {
                edited = updated;
                modified = true;
            }
        }
        return modified ? edited : null;
    }
}
