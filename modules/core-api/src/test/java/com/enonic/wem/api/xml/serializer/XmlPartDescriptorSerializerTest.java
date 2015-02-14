package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.PartDescriptor;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.xml.mapper.XmlPartDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;

import static com.enonic.wem.api.form.InlineMixin.newInlineMixin;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.DOUBLE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class XmlPartDescriptorSerializerTest
    extends BaseXmlSerializerTest
{
    private final static ModuleKey CURRENT_MODULE = ModuleKey.from( "mymodule" );

    @Test
    public void test_to_xml()
        throws Exception
    {
        Form configForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( DOUBLE ).label( "Column width" ).build() ).
            addFormItem( newInlineMixin().mixin( "mymodule:link-urls" ).build() ).
            build();

        PartDescriptor partDescriptor = PartDescriptor.newPartDescriptor().
            displayName( "A Part" ).
            name( "mypart" ).
            config( configForm ).
            key( DescriptorKey.from( "module:mypart" ) ).
            build();

        final XmlPartDescriptor xml = new XmlPartDescriptorMapper( CURRENT_MODULE ).toXml( partDescriptor );
        final String result = XmlSerializers.partDescriptor().serialize( xml );

        assertXml( "part-descriptor.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "part-descriptor.xml" );
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        builder.key( DescriptorKey.from( "module:mypart" ) );
        builder.name( "A part" );

        final XmlPartDescriptor xmlObject = XmlSerializers.partDescriptor().parse( xml );
        new XmlPartDescriptorMapper( CURRENT_MODULE ).fromXml( xmlObject, builder );
        final PartDescriptor partDescriptor = builder.build();

        assertEquals( "A Part", partDescriptor.getDisplayName() );
        final Form config = partDescriptor.getConfig();
        assertNotNull( config );
        assertEquals( DOUBLE, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
        assertEquals( "link-urls", config.getFormItem( "link-urls" ).toInlineMixin().getName() );
    }

}
