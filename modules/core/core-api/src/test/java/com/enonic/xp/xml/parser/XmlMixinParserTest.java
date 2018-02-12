package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.Assert.*;

public class XmlMixinParserTest
    extends XmlModelParserTest
{
    private XmlMixinParser parser;

    private Mixin.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlMixinParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = Mixin.create();
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
        final Mixin result = this.builder.build();
        assertEquals( "myapplication:mymixin", result.getName().toString() );
        assertEquals( "display name", result.getDisplayName() );
        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );
        assertEquals( "description", result.getDescription() );
        assertEquals( "key.description", result.getDescriptionI18nKey() );

        assertEquals( 1, result.getForm().size() );
    }
}
