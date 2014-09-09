package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.xml.mapper.XmlRelationshipTypeMapper;
import com.enonic.wem.api.xml.model.XmlRelationshipType;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static junit.framework.Assert.assertEquals;

public class XmlRelationshipTypeSerializerTest
    extends BaseXmlSerializer2Test
{
    @Test
    public void test_to_xml()
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

        XmlRelationshipType xmlObject = XmlRelationshipTypeMapper.toXml( relationshipType );
        String result = XmlSerializers2.relationshipType().serialize( xmlObject );

        assertXml( "relationship-type.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "relationship-type.xml" );
        final RelationshipType.Builder builder = RelationshipType.newRelationshipType();

        XmlRelationshipType xmlObject = XmlSerializers2.relationshipType().parse( xml );
        XmlRelationshipTypeMapper.fromXml( xmlObject, builder );
        builder.name( "mymodule-1.0.0:myreltype" );
        RelationshipType relationshipType = builder.build();

        assertEquals( "mymodule-1.0.0:myreltype", relationshipType.getName().toString() );
        assertEquals( "description", relationshipType.getDescription() );
        assertEquals( "likes", relationshipType.getFromSemantic() );
        assertEquals( "liked by", relationshipType.getToSemantic() );
        assertEquals( ContentTypeNames.from( "mymodule-1.0.0:animal", "mymodule-1.0.0:person" ), relationshipType.getAllowedFromTypes() );
        assertEquals( ContentTypeNames.from( "mymodule-1.0.0:vehicle" ), relationshipType.getAllowedToTypes() );
    }

}
