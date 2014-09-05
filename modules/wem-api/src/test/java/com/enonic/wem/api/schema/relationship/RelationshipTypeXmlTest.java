package com.enonic.wem.api.schema.relationship;


import org.junit.Test;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

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
            name( "mymodule-1.0.0:like" ).
            description( "description" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType(  ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedFromType(  ContentTypeName.from( "mymodule-1.0.0:animal" ) ).
            addAllowedToType(  ContentTypeName.from( "mymodule-1.0.0:vehicle" ) ).
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

        builder.name( "mymodule-1.0.0:myreltype" );
        final RelationshipType relationshipType = builder.build();

        assertEquals( "mymodule-1.0.0:myreltype", relationshipType.getName().toString() );
        assertEquals( "description", relationshipType.getDescription() );
        assertEquals( "likes", relationshipType.getFromSemantic() );
        assertEquals( "liked by", relationshipType.getToSemantic() );
        assertEquals( ContentTypeNames.from( "mymodule-1.0.0:animal", "mymodule-1.0.0:person" ), relationshipType.getAllowedFromTypes() );
        assertEquals( ContentTypeNames.from( "mymodule-1.0.0:vehicle" ), relationshipType.getAllowedToTypes() );
    }
}
