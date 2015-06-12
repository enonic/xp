package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static com.enonic.xp.form.inputtype.InputTypes.DOUBLE;
import static com.enonic.xp.form.inputtype.InputTypes.TEXT_LINE;
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
        this.parser.currentModule( ModuleKey.from( "mymodule" ) );

        this.builder = PageDescriptor.create();
        this.builder.key( DescriptorKey.from( "mymodule:mypage" ) );
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
        assertEquals( "mymodule:mypage", result.getKey().toString() );
        assertEquals( "mypage", result.getName() );
        assertEquals( "Landing page", result.getDisplayName() );

        final Form config = result.getConfig();
        assertNotNull( config );
        assertEquals( DOUBLE, config.getFormItem( "pause" ).toInput().getInputType() );
        assertEquals( "Pause parameter", config.getFormItem( "pause" ).toInput().getLabel() );
        assertNotNull( config.getFormItem( "myFormItemSet" ).toFormItemSet() );
        assertEquals( "My form item set", config.getFormItem( "myFormItemSet" ).toFormItemSet().getLabel() );
        assertEquals( TEXT_LINE, config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getInputType() );
        assertEquals( "Field set Item", config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getLabel() );

        final RegionDescriptors regions = result.getRegions();
        assertNotNull( regions );
        assertEquals( 3, regions.numberOfRegions() );
        assertNotNull( regions.getRegionDescriptor( "header" ) );
    }
}
