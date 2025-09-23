package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XmlLayoutDescriptorParserTest
    extends XmlModelParserTest
{
//    private XmlLayoutDescriptorParser parser;
//
//    private LayoutDescriptor.Builder builder;
//
//    @BeforeEach
//    public void setup()
//    {
//        this.parser = new XmlLayoutDescriptorParser();
//        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );
//
//        this.builder = LayoutDescriptor.create();
//        this.builder.key( DescriptorKey.from( "myapplication:mylayout" ) );
//        this.parser.builder( this.builder );
//    }
//
//    @Test
//    public void testParse()
//        throws Exception
//    {
//        parse( this.parser, ".xml" );
//        assertResult();
//    }
//
//    @Test
//    public void testParse_noNs()
//        throws Exception
//    {
//        parseRemoveNs( this.parser, ".xml" );
//        assertResult();
//    }
//
//    private void assertResult()
//        throws Exception
//    {
//        final LayoutDescriptor result = this.builder.build();
//        assertEquals( "myapplication:mylayout", result.getKey().toString() );
//        assertEquals( "mylayout", result.getName() );
//        assertEquals( "My Layout", result.getDisplayName() );
//        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );
//
//        assertEquals( "My Layout Description", result.getDescription() );
//        assertEquals( "key.description", result.getDescriptionI18nKey() );
//
//        final Form config = result.getConfig();
//        assertNotNull( config );
//        assertEquals( InputTypeName.DOUBLE, config.getInput( "pause" ).getInputType() );
//        assertEquals( "Pause parameter", config.getInput( "pause" ).getLabel() );
//
//        assertEquals( "key1.label", config.getInput( "pause" ).getLabelI18nKey() );
//        assertEquals( "key1.help-text", config.getInput( "pause" ).getHelpTextI18nKey() );
//
//        assertNotNull( config.getFormItemSet( "myFormItemSet" ) );
//        assertEquals( "My form item set", config.getFormItemSet( "myFormItemSet" ).getLabel() );
//        assertEquals( InputTypeName.TEXT_LINE, config.getInput( "myFormItemSet.fieldSetItem" ).getInputType() );
//        assertEquals( "Field set Item", config.getInput( "myFormItemSet.fieldSetItem" ).getLabel() );
//
//        assertEquals( "key2.label", config.getInput( "myFormItemSet.fieldSetItem" ).getLabelI18nKey() );
//        assertEquals( "key2.help-text", config.getInput( "myFormItemSet.fieldSetItem" ).getHelpTextI18nKey() );
//
//        final RegionDescriptors regions = result.getRegions();
//        assertNotNull( regions );
//        assertEquals( 3, regions.numberOfRegions() );
//    }
}
