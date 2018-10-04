package com.enonic.xp.site;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.xdata.XDataName;

import static org.junit.Assert.*;

public class SiteDescriptorTest
{
    @Test
    public void create_empty_site_descriptor()
    {
        //Builds an empty SiteDescriptor
        SiteDescriptor siteDescriptor = SiteDescriptor.create().build();
        assertEquals( null, siteDescriptor.getForm() );
        assertEquals( null, siteDescriptor.getXDataMappings() );
        assertNotNull( siteDescriptor.getFilterDescriptors() );
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
        List<XDataMapping> xDataMappingList = Lists.newArrayList();
        xDataMappingList.add( XDataMapping.create().xDataName( XDataName.from( "myapplication:my" ) ).build() );
        XDataMappings xDataMappings = XDataMappings.from( xDataMappingList );

        //Builds a SiteDescriptor
        SiteDescriptor siteDescriptor = SiteDescriptor.create().
            form( form ).
            xDataMappings( xDataMappings ).
            build();
        assertEquals( form, siteDescriptor.getForm() );
        assertEquals( xDataMappings, siteDescriptor.getXDataMappings() );
    }

}
