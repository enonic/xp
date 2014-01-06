package com.enonic.wem.api.content.page.layout;

import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.DECIMAL_NUMBER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class LayoutDescriptorXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        Form configForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( DECIMAL_NUMBER ).label( "Column width" ).build() ).
            build();

        LayoutDescriptor layoutDescriptor = LayoutDescriptor.newLayoutDescriptor().
            displayName( "A Layout" ).
            name( "mylayout" ).
            config( configForm ).
            key( LayoutDescriptorKey.from( "module-1.0.0:mylayout" ) ).
            build();

        final LayoutDescriptorXml layoutDescriptorXml = new LayoutDescriptorXml();
        layoutDescriptorXml.from( layoutDescriptor );
        final String result = XmlSerializers.layoutDescriptor().serialize( layoutDescriptorXml );

        assertXml( "layout-component.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "layout-component.xml" );
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
        builder.key( LayoutDescriptorKey.from( "module-1.0.0:mylayout" ) );

        XmlSerializers.layoutDescriptor().parse( xml ).to( builder );

        final LayoutDescriptor layoutDescriptor = builder.name( "mylayout" ).build();

        assertEquals( "A Layout", layoutDescriptor.getDisplayName() );
        final Form config = layoutDescriptor.getConfigForm();
        assertNotNull( config );
        assertEquals( DECIMAL_NUMBER, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
    }

}
