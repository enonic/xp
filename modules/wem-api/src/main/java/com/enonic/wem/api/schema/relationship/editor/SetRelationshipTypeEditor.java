package com.enonic.wem.api.schema.relationship.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;

public final class SetRelationshipTypeEditor
    implements RelationshipTypeEditor
{
    private final String displayName;

    private final String fromSemantic;

    private final String toSemantic;

    private final ContentTypeNames allowedFromTypes;

    private final ContentTypeNames allowedToTypes;

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

        private ContentTypeNames allowedFromTypes;

        private ContentTypeNames allowedToTypes;

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

        public Builder allowedFromTypes( final ContentTypeNames value )
        {
            this.allowedFromTypes = value;
            return this;
        }

        public Builder allowedToTypes( final ContentTypeNames value )
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
    {
        final RelationshipType.Builder builder = newRelationshipType( relationshipType );
        if ( this.icon != null )
        {
            builder.icon( this.icon );
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
            for ( ContentTypeName contentTypeName : allowedFromTypes )
            {
                builder.addAllowedFromType( contentTypeName );
            }
        }
        if ( allowedToTypes != null )
        {
            for ( ContentTypeName contentTypeName : allowedToTypes )
            {
                builder.addAllowedToType( contentTypeName );
            }
        }
        return builder.build();
    }
}
