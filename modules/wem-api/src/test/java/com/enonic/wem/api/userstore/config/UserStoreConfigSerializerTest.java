package com.enonic.wem.api.userstore.config;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserStoreConfigSerializerTest
{

    private UserStoreConfigSerializer serializer;

    private SAXBuilder builder;

    @Before
    public void setUp()
    {
        serializer = new UserStoreConfigSerializer();
        builder = new SAXBuilder();
    }

    @Test
    public void testUserStoreConfigSerialization()
        throws Exception
    {
        UserStoreConfig config = new UserStoreConfig();
        config.addField( new UserStoreFieldConfig( "prefix" ) );
        config.addField( new UserStoreFieldConfig( "first-name" ) );
        config.addField( new UserStoreFieldConfig( "middle-name" ) );
        config.addField( new UserStoreFieldConfig( "last-name" ) );
        config.addField( new UserStoreFieldConfig( "suffix" ) );
        config.addField( new UserStoreFieldConfig( "initials" ) );
        config.addField( new UserStoreFieldConfig( "nick-name" ) );
        config.addField( new UserStoreFieldConfig( "photo" ) );
        config.addField( new UserStoreFieldConfig( "personal-id" ) );
        config.addField( new UserStoreFieldConfig( "member-id" ) );
        config.addField( new UserStoreFieldConfig( "organization" ) );
        config.addField( new UserStoreFieldConfig( "birthday" ) );
        config.addField( new UserStoreFieldConfig( "gender" ) );
        config.addField( new UserStoreFieldConfig( "title" ) );
        config.addField( new UserStoreFieldConfig( "description" ) );
        config.addField( new UserStoreFieldConfig( "html-email" ) );
        config.addField( new UserStoreFieldConfig( "home-page" ) );
        config.addField( new UserStoreFieldConfig( "time-zone" ) );
        config.addField( new UserStoreFieldConfig( "locale" ) );
        config.addField( new UserStoreFieldConfig( "country" ) );
        config.addField( new UserStoreFieldConfig( "global-position" ) );
        config.addField( new UserStoreFieldConfig( "phone" ) );
        config.addField( new UserStoreFieldConfig( "mobile" ) );
        config.addField( new UserStoreFieldConfig( "fax" ) );
        config.addField( new UserStoreFieldConfig( "address" ) );
        Document expectedDoc = builder.build( getClass().getResource( "userStoreConfig_valid.xml" ).openStream() );
        Document actualDoc = serializer.toXml( config );
        assertDocs( expectedDoc, actualDoc );
    }

    private void assertDocs( Document expected, Document actual )
    {
        Element root = actual.getRootElement();
        assertEquals( "config", root.getName() );
        Element userFields = root.getChild( "user-fields" );
        assertNotNull( userFields );
        List<Element> expectedUserFields = expected.getRootElement().getChild( "user-fields" ).getChildren();
        for ( Element child : expectedUserFields )
        {
            Element actualChild = userFields.getChild( child.getName() );
            assertNotNull( actualChild );
            List<Attribute> attributes = child.getAttributes();
            for ( Attribute attribute : attributes )
            {
                assertEquals( attribute.getValue(), actualChild.getAttributeValue( attribute.getName() ) );
            }
        }
    }
}
