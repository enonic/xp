package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.widget.WidgetDescriptor;

import static org.junit.Assert.*;

public class XmlWidgetDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlWidgetDescriptorParser parser;

    private WidgetDescriptor.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlWidgetDescriptorParser();
        this.parser.currentModule( ApplicationKey.from( "mymodule" ) );

        this.builder = WidgetDescriptor.create();
        this.builder.key( DescriptorKey.from( "mymodule:mywidget" ) );
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
        final WidgetDescriptor result = this.builder.build();
        assertEquals( "mymodule:mywidget", result.getKey().toString() );
        assertEquals( "mywidget", result.getName() );
        assertEquals( "My widget", result.getDisplayName() );

        final ImmutableList<String> interfaces = result.getInterfaces();
        assertNotNull( interfaces );
        assertEquals( 2, interfaces.size() );
        assertTrue( interfaces.get( 0 ).startsWith( "com.enonic.xp.my-interface" ) );
        assertTrue( interfaces.get( 1 ).startsWith( "com.enonic.xp.my-interface" ) );
    }
}
