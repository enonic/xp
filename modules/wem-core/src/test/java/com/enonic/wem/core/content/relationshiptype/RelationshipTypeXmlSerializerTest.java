package com.enonic.wem.core.content.relationshiptype;

import static org.junit.Assert.*;

public class RelationshipTypeXmlSerializerTest
    extends AbstractRelationshipTypeSerializerTest
{
    @Override
    RelationshipTypeSerializer getSerializer()
    {
        return new RelationshipTypeXmlSerializer().prettyPrint( true );
    }

    @Override
    String getFileAsString( final String fileName )
    {
        return getXmlAsString( fileName + ".xml" );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getXmlAsString( fileNameForExpected + ".xml" ), actualSerialization );
    }
}
