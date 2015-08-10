package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.schema.relationship.RelationshipTypeName;

@Beta
public final class ImageSelectorTypeConfig
    implements InputTypeConfig
{
    private final RelationshipTypeName relationshipType;

    ImageSelectorTypeConfig( final Builder builder )
    {
        this.relationshipType = builder.relationshipType != null ? builder.relationshipType : RelationshipTypeName.REFERENCE;
    }

    public RelationshipTypeName getRelationshipType()
    {
        return relationshipType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RelationshipTypeName relationshipType;

        Builder()
        {
            // protection
        }

        public Builder relationshipType( final RelationshipTypeName value )
        {
            relationshipType = value;
            return this;
        }

        public ImageSelectorTypeConfig build()
        {
            return new ImageSelectorTypeConfig( this );
        }
    }
}
