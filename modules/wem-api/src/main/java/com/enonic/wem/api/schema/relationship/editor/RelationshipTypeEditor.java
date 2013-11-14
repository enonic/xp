package com.enonic.wem.api.schema.relationship.editor;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.support.Editor;

public interface RelationshipTypeEditor
    extends Editor<RelationshipType>
{
    /**
     * @param relationshipType to be edited
     * @return updated relationshipType, null if it has not been updated.
     */
    public RelationshipType edit( RelationshipType relationshipType );
}
