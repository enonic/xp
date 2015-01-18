package com.enonic.wem.api.relationship.editor;

import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.support.Editor;

public interface RelationshipEditor
    extends Editor<Relationship>
{
    /**
     * @param relationship to be edited
     * @return updated relationship, null if it has not been updated.
     */
    public Relationship edit( Relationship relationship );
}
