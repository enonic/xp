package com.enonic.wem.api.content.relationshiptype.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;

public final class SetRelationshipTypeEditor
    implements RelationshipTypeEditor
{
    private final String displayName;

    private final String fromSemantic;

    private final String toSemantic;

    private final QualifiedContentTypeNames allowedFromTypes;

    private final QualifiedContentTypeNames allowedToTypes;

    private final Icon icon;

    private SetRelationshipTypeEditor( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.fromSemantic = builder.fromSemantic;
        this.toSemantic = builder.toSemantic;
        this.allowedFromTypes = builder.allowedFromTypes;
        this.allowedToTypes = builder.allowedToTypes;
        this.icon = builder.icon;
    }

    public static Builder newSetRelationshipTypeEditor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String displayName;

        private String fromSemantic;

        private String toSemantic;

        private QualifiedContentTypeNames allowedFromTypes;

        private QualifiedContentTypeNames allowedToTypes;

        private Icon icon;

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder fromSemantic( final String value )
        {
            this.fromSemantic = value;
            return this;
        }

        public Builder toSemantic( final String value )
        {
            this.toSemantic = value;
            return this;
        }

        public Builder allowedFromTypes( final QualifiedContentTypeNames value )
        {
            this.allowedFromTypes = value;
            return this;
        }

        public Builder allowedToTypes( final QualifiedContentTypeNames value )
        {
            this.allowedToTypes = value;
            return this;
        }

        public Builder icon( final Icon value )
        {
            this.icon = value;
            return this;
        }

        public SetRelationshipTypeEditor build()
        {
            return new SetRelationshipTypeEditor( this );
        }
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
