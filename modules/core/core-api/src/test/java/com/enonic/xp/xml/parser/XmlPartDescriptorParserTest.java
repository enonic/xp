package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;

import static org.junit.Assert.*;

public class XmlPartDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlPartDescriptorParser parser;

    private PartDescriptor.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlPartDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = PartDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:mypart" ) );
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
        final PartDescriptor result = this.builder.build();
        assertEquals( "myapplication:mypart", result.getKey().toString() );
        assertEquals( "mypart", result.getName() );
        assertEquals( "A Part", result.getDisplayName() );
        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );

        final Form config = result.getConfig();
        assertNotNull( config );
        assertEquals( InputTypeName.DOUBLE, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );

        assertEquals( "key.label", config.getFormItem( "width" ).toInput().getLabelI18nKey() );
        assertEquals( "key.help-text", config.getFormItem( "width" ).toInput().getHelpTextI18nKey() );

        assertEquals( "link-urls", config.getFormItem( "link-urls" ).toInlineMixin().getName() );
    }
}
