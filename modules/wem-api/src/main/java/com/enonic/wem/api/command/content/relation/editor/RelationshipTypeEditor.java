package com.enonic.wem.api.command.content.relation.editor;

import com.enonic.wem.api.content.relation.RelationshipType;

public interface RelationshipTypeEditor
{
    /**
     * @param relationshipType to be edited
     * @return updated relationship type, null if it has not been updated.
     */
    public RelationshipType edit( RelationshipType relationshipType )
        throws Exception;
}
