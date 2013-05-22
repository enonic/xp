package com.enonic.wem.api.relationship.editor;

import com.enonic.wem.api.relationship.Relationship;

public interface RelationshipEditor
{
    /**
     * @param relationship to be edited
     * @return updated relationship, null if it has not been updated.
     */
    public Relationship edit( Relationship relationship )
        throws Exception;
}
