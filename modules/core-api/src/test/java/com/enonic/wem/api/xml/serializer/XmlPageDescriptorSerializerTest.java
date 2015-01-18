package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.xml.mapper.XmlPageDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;

import static com.enonic.wem.api.content.page.region.RegionDescriptor.newRegionDescriptor;
import static com.enonic.wem.api.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.DOUBLE;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class XmlPageDescriptorSerializerTest
    extends BaseXmlSerializerTest
{

    @Test
    public void test_to_xml()
        throws Exception
    {
        Input myTextLine = newInput().
            name( "myTextLine" ).
            inputType( TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();

        Input myCustomInput = newInput().
            name( "myCustomInput" ).
            inputType( TEXT_LINE ).
            label( "My custom input" ).
            required( false ).
            build();

        FieldSet myFieldSet = newFieldSet().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( newInput().
                name( "fieldSetItem" ).
                inputType( TEXT_LINE ).
                label( "Field set Item" ).
                required( false ).
                build() ).
            build();

        FormItemSet myFormItemSet = newFormItemSet().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( myTextLine ).
            addFormItem( myCustomInput ).
            addFormItem( myFieldSet ).
            build();

        Form pageForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( DOUBLE ).label( "Pause parameter" ).build() ).
            addFormItem( myFormItemSet ).
            build();

        PageDescriptor pageDescriptor = PageDescriptor.newPageDescriptor().
            displayName( "Landing page" ).
            config( pageForm ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "header" ).build() ).
                add( newRegionDescriptor().name( "main" ).build() ).
                add( newRegionDescriptor().name( "footer" ).build() ).
                build() ).
            key( DescriptorKey.from( "module:mypage" ) ).
            build();

        final XmlPageDescriptor xml = XmlPageDescriptorMapper.toXml( pageDescriptor );
        final String result = XmlSerializers.pageDescriptor().serialize( xml );

        assertXml( "page-descriptor.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "page-descriptor.xml" );
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
        builder.key( DescriptorKey.from( "module:mypage" ) );
        builder.regions( RegionDescriptors.newRegionDescriptors().build() );

        final XmlPageDescriptor xmlObject = XmlSerializers.pageDescriptor().parse( xml );
        XmlPageDescriptorMapper.fromXml( xmlObject, builder );

        final PageDescriptor pageDescriptor = builder.build();

        assertEquals( "Landing page", pageDescriptor.getDisplayName() );
        final Form config = pageDescriptor.getConfig();
        assertNotNull( config );
        assertEquals( DOUBLE, config.getFormItem( "pause" ).toInput().getInputType() );
        assertEquals( "Pause parameter", config.getFormItem( "pause" ).toInput().getLabel() );
        assertNotNull( config.getFormItem( "myFormItemSet" ).toFormItemSet() );
        assertEquals( "My form item set", config.getFormItem( "myFormItemSet" ).toFormItemSet().getLabel() );
        assertEquals( TEXT_LINE, config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getInputType() );
        assertEquals( "Field set Item", config.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getLabel() );
    }

}
