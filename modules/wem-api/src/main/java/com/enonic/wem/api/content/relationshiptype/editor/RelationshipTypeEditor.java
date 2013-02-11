package com.enonic.wem.api.content.relationshiptype.editor;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;

public interface RelationshipTypeEditor
{
    /**
     * @param relationshipType to be edited
     * @return updated relationshipType, null if it has not been updated.
     */
    public RelationshipType edit( RelationshipType relationshipType )
        throws Exception;
}
