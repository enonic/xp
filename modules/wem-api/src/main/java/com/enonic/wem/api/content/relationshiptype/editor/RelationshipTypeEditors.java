package com.enonic.wem.api.content.relationshiptype.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

public abstract class RelationshipTypeEditors
{
    public static RelationshipTypeEditor composite( final RelationshipTypeEditor... editors )
    {
        return new CompositeRelationshipTypeEditor( editors );
    }

    public static RelationshipTypeEditor setRelationshipType( final String displayName, final String fromSemantic, final String toSemantic,
                                                              final QualifiedContentTypeNames allowedFromTypes,
                                                              final QualifiedContentTypeNames allowedToTypes, final Icon icon )
    {
        return new SetRelationshipTypeEditor( displayName, fromSemantic, toSemantic, allowedFromTypes, allowedToTypes, icon );
    }
}
