package com.enonic.xp.core.xml.serializer;

import org.junit.Test;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.region.LayoutDescriptor;
import com.enonic.xp.core.form.Form;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.xml.mapper.XmlLayoutDescriptorMapper;
import com.enonic.xp.core.xml.model.XmlLayoutDescriptor;

import static com.enonic.xp.core.content.page.region.RegionDescriptor.newRegionDescriptor;
import static com.enonic.xp.core.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.xp.core.form.Input.newInput;
import static com.enonic.xp.core.form.inputtype.InputTypes.DOUBLE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class XmlLayoutDescriptorSerializerTest
    extends BaseXmlSerializerTest
{
    private final static ModuleKey CURRENT_MODULE = ModuleKey.from( "mymodule" );

    @Test
    public void test_to_xml()
        throws Exception
    {
        Form configForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( DOUBLE ).label( "Column width" ).build() ).
            build();

        LayoutDescriptor layoutDescriptor = LayoutDescriptor.newLayoutDescriptor().
            displayName( "A Layout" ).
            name( "mylayout" ).
            config( configForm ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "left" ).build() ).
                add( newRegionDescriptor().name( "right" ).build() ).
                build() ).
            key( DescriptorKey.from( "module:mylayout" ) ).
            build();

        XmlLayoutDescriptor xml = new XmlLayoutDescriptorMapper( CURRENT_MODULE ).toXml( layoutDescriptor );
        final String result = XmlSerializers.layoutDescriptor().serialize( xml );

        assertXml( "layout-descriptor.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "layout-descriptor.xml" );
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
        builder.key( DescriptorKey.from( "module:mylayout" ) );
        builder.name( "mylayout" );

        XmlLayoutDescriptor xmlObject = XmlSerializers.layoutDescriptor().parse( xml );
        new XmlLayoutDescriptorMapper( CURRENT_MODULE ).fromXml( xmlObject, builder );
        LayoutDescriptor layoutDescriptor = builder.build();

        assertEquals( "A Layout", layoutDescriptor.getDisplayName() );
        final Form config = layoutDescriptor.getConfig();
        assertNotNull( config );
        assertEquals( DOUBLE, config.getFormItem( "width" ).toInput().getInputType() );
        assertEquals( "Column width", config.getFormItem( "width" ).toInput().getLabel() );
    }

}
