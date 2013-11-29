package com.enonic.wem.xml.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.DECIMAL_NUMBER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ImageDescriptorXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        Form configForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( DECIMAL_NUMBER ).label( "Column width" ).build() ).
            build();

        ImageDescriptor imageDescriptor = ImageDescriptor.newImageDescriptor().
            displayName( "An Image" ).
            name( "myimage" ).
            config( configForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/image-ctrl.js" ) ).
            build();

        final ImageDescriptorXml imageDescriptorXml = new ImageDescriptorXml();
        imageDescriptorXml.from( imageDescriptor );
        final String result = XmlSerializers.imageDescriptor().serialize( imageDescriptorXml );

        assertXml( "image-component.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "image-component.xml" );
        final ImageDescriptor.Builder builder = ImageDescriptor.newImageDescriptor();

        XmlSerializers.imageDescriptor().parse( xml ).to( builder );

        final ImageDescriptor imageDescriptor = builder.name( "myimage" ).build();

        assertEquals( "An Image", imageDescriptor.getDisplayName() );
        assertEquals( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/image-ctrl.js" ), imageDescriptor.getControllerResource() );
        final Form config = imageDescriptor.getConfig();
        assertNotNull( config );
        assertEquals( DECIMAL_NUMBER, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
    }

}
