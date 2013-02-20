package com.enonic.wem.core.content.schema.relationshiptype;

import static org.junit.Assert.*;

public class RelationshipTypeJsonSerializerTest
    extends AbstractRelationshipTypeSerializerTest
{
    @Override
    RelationshipTypeSerializer getSerializer()
    {
        final RelationshipTypeJsonSerializer serializer = new RelationshipTypeJsonSerializer();
        serializer.prettyPrint();
        return serializer;
    }

    @Override
    String getFileAsString( final String fileName )
    {
        return getJsonAsString( fileName + ".json" );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getJsonAsString( fileNameForExpected + ".json" ), actualSerialization );
    }
}
