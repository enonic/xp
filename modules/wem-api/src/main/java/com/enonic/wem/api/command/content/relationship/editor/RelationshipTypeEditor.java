package com.enonic.wem.api.command.content.relationship.editor;

import com.enonic.wem.api.content.relationship.RelationshipType;

public interface RelationshipTypeEditor
{
    /**
     * @param relationshipType to be edited
     * @return updated relationship type, null if it has not been updated.
     */
    public RelationshipType edit( RelationshipType relationshipType )
        throws Exception;
}
