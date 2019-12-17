package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlXDataParserTest
    extends XmlModelParserTest
{
    private XmlXDataParser parser;

    private XData.Builder builder;

    @BeforeEach
    public void setup()
    {
        this.parser = new XmlXDataParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = XData.create();
        this.builder.name( XDataName.from( "myapplication:mymixin" ) );
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

        assertEquals( 1, result.getForm().size() );
    }
}
