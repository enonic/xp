package com.enonic.wem.api.content.schema.content.form.inputtype;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class RelationshipConfig
    implements InputTypeConfig
{
    private final QualifiedRelationshipTypeName relationshipType;

    RelationshipConfig( final Builder builder )
    {
        Preconditions.checkNotNull( builder.relationshipType, "relationshipType cannot be null" );
        this.relationshipType = builder.relationshipType;
    }

    public QualifiedRelationshipTypeName getRelationshipType()
    {
        return relationshipType;
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

        public RelationshipConfig build()
        {
            return new RelationshipConfig( this );
        }
    }

}
