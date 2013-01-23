package com.enonic.wem.core.content.relationship;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.AbstractSerializerTest;

import static org.junit.Assert.*;

public class RelationshipTypeJsonSerializerTest
    extends AbstractSerializerTest
{
    private RelationshipTypeJsonSerializer serializer;

    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    @Test
    public void testSerializeParse()
        throws Exception
    {
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            name( "contains" ).
            displayName( "Contains" ).
            module( ModuleName.from( "myModule" ) ).
            toSemantic( "contains" ).
            fromSemantic( "contained by" ).
            addAllowedFromType( QualifiedContentTypeName.folder() ).
            addAllowedFromType( QualifiedContentTypeName.file() ).
            addAllowedToType( QualifiedContentTypeName.unstructured() ).
            addAllowedToType( QualifiedContentTypeName.space() ).build();

        final String actualSerialization = serializer.toString( relationshipType );

        // exercise
        final RelationshipType actualRelationshipType = serializer.toRelationshipType( actualSerialization );

        // verify
        assertSerializedResult( "relationshipType", actualSerialization );
        assertEquals( relationshipType, actualRelationshipType );
    }

    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getJsonAsString( fileNameForExpected + ".json" ), actualSerialization );
    }

    private RelationshipTypeJsonSerializer getSerializer()
    {
        final RelationshipTypeJsonSerializer relationshipTypeJsonSerializer = new RelationshipTypeJsonSerializer();
        relationshipTypeJsonSerializer.prettyPrint();
        return relationshipTypeJsonSerializer;
    }
}
