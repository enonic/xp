package com.enonic.xp.admin.impl.widget;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.xml.parser.XmlModelParserTest;

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
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = WidgetDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:mywidget" ) );
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
        assertEquals( "myapplication:mywidget", result.getKey().toString() );
        assertEquals( "mywidget", result.getName() );
        assertEquals( "My widget", result.getDisplayName() );
        assertEquals( "myapplication:mywidget", result.getKeyString() );
        assertEquals( "_/widgets/myapplication/mywidget", result.getUrl() );

        final ImmutableList<String> interfaces = result.getInterfaces();
        assertNotNull( interfaces );
        assertEquals( 2, interfaces.size() );
        assertTrue( interfaces.get( 0 ).startsWith( "com.enonic.xp.my-interface" ) );
        assertTrue( interfaces.get( 1 ).startsWith( "com.enonic.xp.my-interface" ) );
    }
}
