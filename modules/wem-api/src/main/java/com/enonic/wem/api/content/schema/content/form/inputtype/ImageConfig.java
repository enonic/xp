package com.enonic.wem.api.content.schema.content.form.inputtype;

import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class ImageConfig
    extends RelationshipConfig
{
    private ImageConfig( final Builder builder )
    {
        super( builder );
    }

    public static Builder newImageConfig()
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

        public ImageConfig build()
        {
            return new ImageConfig( this );
        }
    }

}
