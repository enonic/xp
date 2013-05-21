package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.support.serializer.ParsingException;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface RelationshipTypeSerializer
{
    public String toString( RelationshipType relationshipType )
        throws SerializingException;

    public RelationshipType toRelationshipType( String serialized )
        throws ParsingException;
}
