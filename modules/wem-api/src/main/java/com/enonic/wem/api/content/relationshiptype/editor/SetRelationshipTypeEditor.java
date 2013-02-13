package com.enonic.wem.api.content.relationshiptype.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;

final class SetRelationshipTypeEditor
    implements RelationshipTypeEditor
{
    private final String displayName;

    private final String fromSemantic;

    private final String toSemantic;

    private final QualifiedContentTypeNames allowedFromTypes;

    private final QualifiedContentTypeNames allowedToTypes;

    private final Icon icon;

    SetRelationshipTypeEditor( final String displayName, final String fromSemantic, final String toSemantic,
                               final QualifiedContentTypeNames allowedFromTypes, final QualifiedContentTypeNames allowedToTypes,
                               final Icon icon )
    {
        this.displayName = displayName;
        this.fromSemantic = fromSemantic;
        this.toSemantic = toSemantic;
        this.allowedFromTypes = allowedFromTypes;
        this.allowedToTypes = allowedToTypes;
        this.icon = icon;
    }

    @Override
    public RelationshipType edit( final RelationshipType relationshipType )
        throws Exception
    {
        final RelationshipType.Builder builder = newRelationshipType( relationshipType );
        if ( this.icon != null )
        {
            builder.icon( Icon.copyOf( this.icon ) );
        }
        if ( displayName != null )
        {
            builder.displayName( displayName );
        }
        if ( fromSemantic != null )
        {
            builder.fromSemantic( fromSemantic );
        }
        if ( toSemantic != null )
        {
            builder.toSemantic( toSemantic );
        }
        if ( allowedFromTypes != null )
        {
            for ( QualifiedContentTypeName contentTypeName : allowedFromTypes )
            {
                builder.addAllowedFromType( contentTypeName );
            }
        }
        if ( allowedToTypes != null )
        {
            for ( QualifiedContentTypeName contentTypeName : allowedToTypes )
            {
                builder.addAllowedToType( contentTypeName );
            }
        }
        return builder.build();
    }
}
