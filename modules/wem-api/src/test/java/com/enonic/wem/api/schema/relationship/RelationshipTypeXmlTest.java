package com.enonic.wem.api.schema.relationship;


import org.junit.Test;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static junit.framework.Assert.assertEquals;

public class RelationshipTypeXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "like" ).
            description( "description" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType(  ContentTypeName.from( "person" ) ).
            addAllowedFromType(  ContentTypeName.from( "animal" ) ).
            addAllowedToType(  ContentTypeName.from( "vehicle" ) ).
            build();

        final RelationshipTypeXml relationshipTypeXml = new RelationshipTypeXml();
        relationshipTypeXml.from( relationshipType );
        final String result = XmlSerializers.relationshipType().serialize( relationshipTypeXml );

        assertXml( "relationship-type.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "relationship-type.xml" );
        final RelationshipType.Builder builder = RelationshipType.newRelationshipType();

        XmlSerializers.relationshipType().parse( xml ).to( builder );

        final RelationshipType relationshipType = builder.build();

        assertEquals( null, relationshipType.getName() );
        assertEquals( "description", relationshipType.getDescription() );
        assertEquals( "likes", relationshipType.getFromSemantic() );
        assertEquals( "liked by", relationshipType.getToSemantic() );
        assertEquals( ContentTypeNames.from( "animal", "person" ), relationshipType.getAllowedFromTypes() );
        assertEquals( ContentTypeNames.from( "vehicle" ), relationshipType.getAllowedToTypes() );
    }
}
