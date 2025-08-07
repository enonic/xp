package com.enonic.xp.site;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.xdata.XDataName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SiteDescriptorTest
{
    @Test
    public void create_empty_site_descriptor()
    {
        //Builds an empty SiteDescriptor
        SiteDescriptor siteDescriptor = SiteDescriptor.create().build();
        assertEquals( 0, siteDescriptor.getForm().size() );
        assertTrue( siteDescriptor.getXDataMappings().isEmpty() );
        assertNotNull( siteDescriptor.getResponseProcessors() );
    }

    @Test
    public void create_site_descriptor()
    {
        //Builds a Form
        final FormItem formItem = Input.create().
            name( "input" ).
            label( "Input" ).
            inputType( InputTypeName.DOUBLE ).
            build();

        final Form form = Form.create().
            addFormItem( formItem ).
            build();

        //Builds MixinNames
        XDataMappings xDataMappings = XDataMappings.from( XDataMapping.create().xDataName( XDataName.from( "myapplication:my" ) ).build() );

        //Builds a SiteDescriptor
        SiteDescriptor siteDescriptor = SiteDescriptor.create().
            form( form ).
            xDataMappings( xDataMappings ).
            build();
        assertEquals( form, siteDescriptor.getForm() );
        assertEquals( xDataMappings, siteDescriptor.getXDataMappings() );
    }

}
