package com.enonic.wem.xml.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;
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
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/part-ctrl.js" ) ).
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

        XmlSerializers.partDescriptor().parse( xml ).to( builder );

        final PartDescriptor partDescriptor = builder.name( "mypart" ).build();

        assertEquals( "A Part", partDescriptor.getDisplayName() );
        assertEquals( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/part-ctrl.js" ), partDescriptor.getControllerResource() );
        final Form config = partDescriptor.getConfig();
        assertNotNull( config );
        assertEquals( DECIMAL_NUMBER, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
    }

}
