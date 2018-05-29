package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.XData;

import static org.junit.Assert.*;

public class XmlXDataParserTest
    extends XmlModelParserTest
{
    private XmlMixinParser parser;

    private XData.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlXDataParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = XData.create();
        this.builder.name( MixinName.from( "myapplication:mymixin" ) );
        this.parser.builder( this.builder );
    }

    @Test
    public void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    @Test
    public void testParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
        throws Exception
    {
        final XData result = this.builder.build();
        assertEquals( "myapplication:mymixin", result.getName().toString() );
        assertEquals( "display name", result.getDisplayName() );
        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );
        assertEquals( "description", result.getDescription() );
        assertEquals( "key.description", result.getDescriptionI18nKey() );
        assertEquals( 2, result.getAllowContentTypes().size() );
        assertTrue( result.getAllowContentTypes().contains( "test.contentType1" ) );
        assertTrue( result.getAllowContentTypes().contains( "test.contentType2" ) );

        assertEquals( 1, result.getForm().size() );
    }
}
