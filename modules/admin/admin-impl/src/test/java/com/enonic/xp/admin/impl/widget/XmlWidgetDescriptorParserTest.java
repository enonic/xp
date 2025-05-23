package com.enonic.xp.admin.impl.widget;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XmlWidgetDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlWidgetDescriptorParser parser;

    private WidgetDescriptor.Builder builder;

    @BeforeEach
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
        assertEquals( "My widget description", result.getDescription() );
        assertEquals( "myapplication:mywidget", result.getKeyString() );
        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );
        assertEquals( "key.description", result.getDescriptionI18nKey() );

        final Set<String> interfaces = result.getInterfaces();
        assertNotNull( interfaces );
        assertEquals( 2, interfaces.size() );

        final Map<String, String> config = result.getConfig();
        assertNotNull( config );
        assertEquals( 2, config.size() );
    }
}
