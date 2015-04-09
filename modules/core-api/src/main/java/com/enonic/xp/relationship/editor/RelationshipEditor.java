package com.enonic.xp.relationship.editor;

import com.google.common.annotations.Beta;

import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.support.Editor;

@Beta
public interface RelationshipEditor
    extends Editor<Relationship>
{
    /**
     * @param relationship to be edited
     * @return updated relationship, null if it has not been updated.
     */
    @Override
    Relationship edit( Relationship relationship );
}
