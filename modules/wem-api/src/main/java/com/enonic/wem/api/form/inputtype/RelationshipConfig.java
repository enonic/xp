package com.enonic.wem.api.form.inputtype;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.InvalidValueException;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public class RelationshipConfig
    implements InputTypeConfig
{
    private final RelationshipTypeName relationshipType;

    RelationshipConfig( final Builder builder )
    {
        Preconditions.checkNotNull( builder.relationshipType, "relationshipType cannot be null" );
        this.relationshipType = builder.relationshipType;
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

    public static Builder newRelationshipConfig()
    {
        return new Builder();
    }

    public static class Builder
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

        public RelationshipConfig build()
        {
            return new RelationshipConfig( this );
        }
    }

}
