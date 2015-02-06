package com.enonic.wem.api.form.inputtype;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.InvalidValueException;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class ContentSelectorConfig
    implements InputTypeConfig
{
    private final RelationshipTypeName relationshipType;

    private final ContentTypeNames allowedContentTypes;

    ContentSelectorConfig( final Builder builder )
    {
        this.relationshipType = builder.relationshipType != null ? builder.relationshipType : RelationshipTypeName.REFERENCE;
        this.allowedContentTypes = builder.allowedContentTypes.build();
    }

    public RelationshipTypeName getRelationshipType()
    {
        return relationshipType;
    }

    public ContentTypeNames getAllowedContentTypes()
    {
        return allowedContentTypes;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {

    }

    public static Builder newRelationshipConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RelationshipTypeName relationshipType;

        private final ContentTypeNames.Builder allowedContentTypes;

        Builder()
        {
            allowedContentTypes = ContentTypeNames.newContentTypeNames();
        }

        public Builder relationshipType( final RelationshipTypeName value )
        {
            relationshipType = value;
            return this;
        }

        public Builder addAllowedContentType( final ContentTypeName contentType )
        {
            this.allowedContentTypes.add( contentType );
            return this;
        }

        public ContentSelectorConfig build()
        {
            return new ContentSelectorConfig( this );
        }
    }

}
