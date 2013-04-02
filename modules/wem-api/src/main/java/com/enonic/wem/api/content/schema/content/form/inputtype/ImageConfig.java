package com.enonic.wem.api.content.schema.content.form.inputtype;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class ImageConfig
    implements InputTypeConfig
{
    private final QualifiedRelationshipTypeName relationshipType;

    private ImageConfig( final Builder builder )
    {
        Preconditions.checkNotNull( builder.relationshipType, "relationshipType cannot be null" );
        this.relationshipType = builder.relationshipType;
    }

    public QualifiedRelationshipTypeName getRelationshipType()
    {
        return relationshipType;
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueException
    {

    }

    public static Builder newImageConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        private QualifiedRelationshipTypeName relationshipType;

        Builder()
        {
            // protection
        }

        public Builder relationshipType( final QualifiedRelationshipTypeName value )
        {
            relationshipType = value;
            return this;
        }

        public ImageConfig build()
        {
            return new ImageConfig( this );
        }
    }

}
