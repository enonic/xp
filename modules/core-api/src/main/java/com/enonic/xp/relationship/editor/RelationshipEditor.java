package com.enonic.xp.relationship.editor;

import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.support.Editor;

public interface RelationshipEditor
    extends Editor<Relationship>
{
    /**
     * @param relationship to be edited
     * @return updated relationship, null if it has not been updated.
     */
    public Relationship edit( Relationship relationship );
}
