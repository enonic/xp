package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

@Beta
public final class ImageSelectorConfig
    implements InputTypeConfig
{
    private final RelationshipTypeName relationshipType;

    ImageSelectorConfig( final Builder builder )
    {
        this.relationshipType = builder.relationshipType != null ? builder.relationshipType : RelationshipTypeName.REFERENCE;
    }

    public RelationshipTypeName getRelationshipType()
    {
        return relationshipType;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {

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

        public ImageSelectorConfig build()
        {
            return new ImageSelectorConfig( this );
        }
    }

}
