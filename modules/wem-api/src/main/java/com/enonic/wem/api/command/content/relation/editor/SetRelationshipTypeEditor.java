package com.enonic.wem.api.command.content.relation.editor;

import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

import static com.enonic.wem.api.content.relation.RelationshipType.newRelationType;

final class SetRelationshipTypeEditor
    implements RelationshipTypeEditor
{
    protected final RelationshipType source;

    public SetRelationshipTypeEditor( final RelationshipType source )
    {
        this.source = source;
    }

    @Override
    public RelationshipType edit( final RelationshipType relationshipType )
        throws Exception
    {
        final RelationshipType.Builder updated = newRelationType().
            name( relationshipType.getName() ).
            module( relationshipType.getModuleName() ).
            fromSemantic( source.getFromSemantic() ).
            toSemantic( source.getToSemantic() );
        for ( QualifiedContentTypeName contentTypeName : source.getAllowedFromTypes() )
        {
            updated.addAllowedFromType( contentTypeName );
        }
        for ( QualifiedContentTypeName contentTypeName : source.getAllowedToTypes() )
        {
            updated.addAllowedToType( contentTypeName );
        }
        return updated.build();
    }
}
