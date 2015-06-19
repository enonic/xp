package com.enonic.xp.core.impl.schema.mixin;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.xml.parser.XmlModelParserTest;

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
        this.parser.currentModule( ModuleKey.from( "mymodule" ) );

        this.builder = Mixin.create();
        this.builder.name( MixinName.from( "mymodule:mymixin" ) );
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
        assertEquals( "mymodule:mymixin", result.getName().toString() );
        assertEquals( "display name", result.getDisplayName() );
        assertEquals( "description", result.getDescription() );

        assertEquals( 1, result.getFormItems().size() );
    }
}
