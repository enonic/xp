package com.enonic.wem.core.content.schema.relationship;

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
        return loadJsonAsString( fileName + ".json" );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( loadJsonAsString( fileNameForExpected + ".json" ), actualSerialization );
    }
}
