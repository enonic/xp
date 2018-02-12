package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.Assert.*;

public class XmlPageDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlPageDescriptorParser parser;

    private PageDescriptor.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlPageDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = PageDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:mypage" ) );
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
        final PageDescriptor result = this.builder.build();
        assertEquals( "myapplication:mypage", result.getKey().toString() );
        assertEquals( "mypage", result.getName() );
        assertEquals( "Landing page", result.getDisplayName() );
        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );

        final Form config = result.getConfig();
        assertNotNull( config );
        assertEquals( InputTypeName.DOUBLE, config.getFormItem( "pause" ).toInput().getInputType() );
        assertEquals( "Pause parameter", config.getFormItem( "pause" ).toInput().getLabel() );

        assertEquals( "key1.label", config.getFormItem( "pause" ).toInput().getLabelI18nKey() );
        assertEquals( "key1.help-text", config.getFormItem( "pause" ).toInput().getHelpTextI18nKey() );

        assertNotNull( config.getFormItem( "myFormItemSet" ).toFormItemSet() );
        assertEquals( "My form item set", config.getFormItem( "myFormItemSet" ).toFormItemSet().getLabel() );
        assertEquals( InputTypeName.TEXT_LINE, config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getInputType() );
        assertEquals( "Field set Item", config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getLabel() );

        assertEquals( "key2.label", config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getLabelI18nKey() );
        assertEquals( "key2.help-text", config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getHelpTextI18nKey() );

        final RegionDescriptors regions = result.getRegions();
        assertNotNull( regions );
        assertEquals( 3, regions.numberOfRegions() );
        assertNotNull( regions.getRegionDescriptor( "header" ) );
    }
}
