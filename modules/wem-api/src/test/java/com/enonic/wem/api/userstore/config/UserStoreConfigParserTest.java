package com.enonic.wem.api.userstore.config;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.exception.InvalidUserStoreConfigException;

import static org.junit.Assert.*;

public class UserStoreConfigParserTest
{
    private UserStoreConfigParser parser;

    private SAXBuilder builder;

    @Before
    public void setUp()
    {
        parser = new UserStoreConfigParser();
        builder = new SAXBuilder();
    }

    @Test(expected = InvalidUserStoreConfigException.class)
    public void parseInvalidUserStoreConfig()
        throws Exception
    {
        Document doc = buildDocument( "userStoreConfig_invalid.xml" );
        parser.parseXml( doc );
    }

    @Test(expected = InvalidUserStoreConfigException.class)
    public void parseWrongRootUserStoreConfig()
        throws Exception
    {
        Document doc = buildDocument( "userStoreConfig_wrongRoot.xml" );
        parser.parseXml( doc );
    }

    @Test(expected = InvalidUserStoreConfigException.class)
    public void parseWrongStructureUserStoreConfig()
        throws Exception
    {
        Document doc = buildDocument( "userStoreConfig_wrongStructure.xml" );
        parser.parseXml( doc );
    }

    @Test
    public void parseValidUserStoreConfig()
        throws Exception
    {
        Document doc = buildDocument( "userStoreConfig_valid.xml" );
        UserStoreConfig config = parser.parseXml( doc );
        assertEquals( 25, config.getFields().size() );
        assertNotNull( config.getField( "prefix" ) );
        assertNotNull( config.getField( "first-name" ) );
        assertNotNull( config.getField( "middle-name" ) );
        assertNotNull( config.getField( "last-name" ) );
        assertNotNull( config.getField( "suffix" ) );
        assertNotNull( config.getField( "initials" ) );
        assertNotNull( config.getField( "nick-name" ) );
        assertNotNull( config.getField( "photo" ) );
        assertNotNull( config.getField( "personal-id" ) );
        assertNotNull( config.getField( "member-id" ) );
        assertNotNull( config.getField( "organization" ) );
        assertNotNull( config.getField( "birthday" ) );
        assertNotNull( config.getField( "gender" ) );
        assertNotNull( config.getField( "title" ) );
        assertNotNull( config.getField( "description" ) );
        assertNotNull( config.getField( "html-email" ) );
        assertNotNull( config.getField( "home-page" ) );
        assertNotNull( config.getField( "time-zone" ) );
        assertNotNull( config.getField( "locale" ) );
        assertNotNull( config.getField( "country" ) );
        assertNotNull( config.getField( "global-position" ) );
        assertNotNull( config.getField( "phone" ) );
        assertNotNull( config.getField( "mobile" ) );
        assertNotNull( config.getField( "fax" ) );
        assertNotNull( config.getField( "address" ) );
    }

    private Document buildDocument( String filename )
        throws IOException, JDOMException
    {
        return builder.build( getClass().getResource( filename ).openStream() );
    }
}
