package com.enonic.wem.api.content.schema.content.form.inputtype;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames.newQualifiedContentTypeNames;

public class RelationshipConfig
    implements InputTypeConfig
{
    private final QualifiedContentTypeNames allowedContentTypes;

    private final QualifiedRelationshipTypeName relationshipType;

    private RelationshipConfig( final Builder builder )
    {
        this.allowedContentTypes = builder.allowedContentTypes.build();
        this.relationshipType = builder.relationshipType;
    }

    public QualifiedContentTypeNames getAllowedContentTypes()
    {
        return allowedContentTypes;
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


    public static Builder newRelationshipConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        private QualifiedContentTypeNames.Builder allowedContentTypes = newQualifiedContentTypeNames();

        private QualifiedRelationshipTypeName relationshipType;

        Builder()
        {
            // protection
        }

        public Builder allowedContentType( final QualifiedContentTypeName value )
        {
            allowedContentTypes.add( value );
            return this;
        }

        public Builder relationshipType( final QualifiedRelationshipTypeName value )
        {
            relationshipType = value;
            return this;
        }

        public RelationshipConfig build()
        {
            return new RelationshipConfig( this );
        }
    }

}
