package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;

import static com.enonic.xp.form.inputtype.InputTypes.DOUBLE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class XmlPartDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlPartDescriptorParser parser;

    private PartDescriptor.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlPartDescriptorParser();
        this.parser.currentModule( ModuleKey.from( "mymodule" ) );

        this.builder = PartDescriptor.create();
        this.builder.key( DescriptorKey.from( "mymodule:mypart" ) );
        this.builder.name( "mypart" );
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
        assertEquals( "mymodule:mypart", result.getKey().toString() );
        assertEquals( "mypart", result.getName() );
        assertEquals( "A Part", result.getDisplayName() );

        final Form config = result.getConfig();
        assertNotNull( config );
        assertEquals( DOUBLE, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
        assertEquals( "link-urls", config.getFormItem( "link-urls" ).toInlineMixin().getName() );
    }
}
