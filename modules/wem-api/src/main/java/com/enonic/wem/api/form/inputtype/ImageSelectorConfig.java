package com.enonic.wem.api.form.inputtype;

import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

public class ImageSelectorConfig
    extends RelationshipConfig
{
    private ImageSelectorConfig( final Builder builder )
    {
        super( builder );
    }

    public static Builder newImageSelectorConfig()
    {
        return new Builder();
    }

    public static class Builder
        extends RelationshipConfig.Builder
    {

        Builder()
        {
            // protection
        }

        public Builder relationshipType( final QualifiedRelationshipTypeName value )
        {
            super.relationshipType( value );
            return this;
        }

        public ImageSelectorConfig build()
        {
            return new ImageSelectorConfig( this );
        }
    }

}
