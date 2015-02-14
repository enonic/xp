package com.enonic.xp.core.relationship.editor;

import com.enonic.xp.core.relationship.Relationship;
import com.enonic.xp.core.support.Editor;

public interface RelationshipEditor
    extends Editor<Relationship>
{
    /**
     * @param relationship to be edited
     * @return updated relationship, null if it has not been updated.
     */
    public Relationship edit( Relationship relationship );
}
