package com.enonic.wem.api.content.relationship.editor;

import com.enonic.wem.api.content.relationship.Relationship;

final class CompositeRelationshipEditor
    implements RelationshipEditor
{
    private final RelationshipEditor[] editors;

    CompositeRelationshipEditor( final RelationshipEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public Relationship edit( final Relationship relationship )
        throws Exception
    {
        boolean modified = false;
        Relationship edited = relationship;
        for ( final RelationshipEditor editor : editors )
        {
            final Relationship updated = editor.edit( edited );
            if ( updated != null )
            {
                edited = updated;
                modified = true;
            }
        }
        return modified ? edited : null;
    }
}
