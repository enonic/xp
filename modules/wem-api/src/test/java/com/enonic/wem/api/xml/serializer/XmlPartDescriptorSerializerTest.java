package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.xml.mapper.XmlPartDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.DECIMAL_NUMBER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class XmlPartDescriptorSerializerTest
    extends BaseXmlSerializer2Test
{

    @Test
    public void testFrom()
        throws Exception
    {
        Form configForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( DECIMAL_NUMBER ).label( "Column width" ).build() ).
            build();

        PartDescriptor partDescriptor = PartDescriptor.newPartDescriptor().
            displayName( "A Part" ).
            name( "mypart" ).
            config( configForm ).
            key( PartDescriptorKey.from( "module-1.0.0:mypart" ) ).
            build();

        final XmlPartDescriptor xml = XmlPartDescriptorMapper.toXml( partDescriptor );
        final String result = XmlSerializers2.partDescriptor().serialize( xml );

        assertXml( "part-descriptor.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "part-descriptor.xml" );
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        builder.key( PartDescriptorKey.from( "module-1.0.0:mypart" ) );
        builder.name( "A part" );

        final XmlPartDescriptor xmlObject = XmlSerializers2.partDescriptor().parse( xml );
        XmlPartDescriptorMapper.fromXml( xmlObject, builder );
        final PartDescriptor partDescriptor = builder.build();

        assertEquals( "A Part", partDescriptor.getDisplayName() );
        final Form config = partDescriptor.getConfig();
        assertNotNull( config );
        assertEquals( DECIMAL_NUMBER, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
    }

}
