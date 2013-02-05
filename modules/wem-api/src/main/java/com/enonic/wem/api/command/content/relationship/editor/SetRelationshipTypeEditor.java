package com.enonic.wem.api.command.content.relationship.editor;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;

final class SetRelationshipTypeEditor
    implements RelationshipTypeEditor
{
    private final RelationshipType source;

    SetRelationshipTypeEditor( final RelationshipType source )
    {
        this.source = source;
    }

    @Override
    public RelationshipType edit( final RelationshipType relationshipType )
        throws Exception
    {
        final RelationshipType.Builder builder = newRelationshipType( relationshipType );
        builder.displayName( source.getDisplayName() );
        builder.module( source.getModuleName() );
        builder.fromSemantic( source.getFromSemantic() );
        builder.toSemantic( source.getToSemantic() );

        for ( QualifiedContentTypeName contentTypeName : source.getAllowedFromTypes() )
        {
            builder.addAllowedFromType( contentTypeName );
        }
        for ( QualifiedContentTypeName contentTypeName : source.getAllowedToTypes() )
        {
            builder.addAllowedToType( contentTypeName );
        }
        return builder.build();
    }
}
