package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.xml.mapper.XmlRelationshipTypeMapper;
import com.enonic.wem.api.xml.model.XmlRelationshipType;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static junit.framework.Assert.assertEquals;

public class XmlRelationshipTypeSerializerTest
    extends BaseXmlSerializerTest
{
    private final static ModuleKey CURRENT_MODULE = ModuleKey.from( "mymodule" );

    @Test
    public void test_to_xml()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "mymodule:like" ).
            description( "description" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule:person" ) ).
            addAllowedFromType( ContentTypeName.from( "mymodule:animal" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule:vehicle" ) ).
            build();

        XmlRelationshipType xmlObject = new XmlRelationshipTypeMapper( CURRENT_MODULE ).toXml( relationshipType );
        String result = XmlSerializers.relationshipType().serialize( xmlObject );

        assertXml( "relationship-type-to.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "relationship-type-from.xml" );
        final RelationshipType.Builder builder = RelationshipType.newRelationshipType();

        XmlRelationshipType xmlObject = XmlSerializers.relationshipType().parse( xml );
        new XmlRelationshipTypeMapper( CURRENT_MODULE ).fromXml( xmlObject, builder );
        builder.name( "mymodule:myreltype" );
        RelationshipType relationshipType = builder.build();

        assertEquals( "mymodule:myreltype", relationshipType.getName().toString() );
        assertEquals( "description", relationshipType.getDescription() );
        assertEquals( "likes", relationshipType.getFromSemantic() );
        assertEquals( "liked by", relationshipType.getToSemantic() );
        assertEquals( ContentTypeNames.from( "mymodule:animal", "mymodule:person" ), relationshipType.getAllowedFromTypes() );
        assertEquals( ContentTypeNames.from( "mymodule:vehicle" ), relationshipType.getAllowedToTypes() );
    }
}
