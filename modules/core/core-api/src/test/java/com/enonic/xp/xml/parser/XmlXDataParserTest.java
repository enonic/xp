package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlXDataParserTest
    extends XmlModelParserTest
{
    private XmlXDataParser parser;

    private XData.Builder builder;

    @BeforeEach
    void setup()
    {
        this.parser = new XmlXDataParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = XData.create();
        this.builder.name( XDataName.from( "myapplication:mymixin" ) );
        this.parser.builder( this.builder );
    }

    @Test
    void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    @Test
    void testParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
    {
        final XData result = this.builder.build();
        assertEquals( "myapplication:mymixin", result.getName().toString() );
        assertEquals( "display name", result.getDisplayName() );
        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );
        assertEquals( "description", result.getDescription() );
        assertEquals( "key.description", result.getDescriptionI18nKey() );

        assertEquals( 1, result.getForm().size() );
    }
}
