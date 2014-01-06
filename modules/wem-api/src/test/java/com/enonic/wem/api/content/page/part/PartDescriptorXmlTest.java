package com.enonic.wem.api.content.page.part;

import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.DECIMAL_NUMBER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class PartDescriptorXmlTest
    extends BaseXmlSerializerTest
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

        final PartDescriptorXml partDescriptorXml = new PartDescriptorXml();
        partDescriptorXml.from( partDescriptor );
        final String result = XmlSerializers.partDescriptor().serialize( partDescriptorXml );

        assertXml( "part-component.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "part-component.xml" );
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        builder.key( PartDescriptorKey.from( "module-1.0.0:mypart" ) );

        XmlSerializers.partDescriptor().parse( xml ).to( builder );

        final PartDescriptor partDescriptor = builder.name( "mypart" ).build();

        assertEquals( "A Part", partDescriptor.getDisplayName() );
        final Form config = partDescriptor.getConfigForm();
        assertNotNull( config );
        assertEquals( DECIMAL_NUMBER, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
    }

}
